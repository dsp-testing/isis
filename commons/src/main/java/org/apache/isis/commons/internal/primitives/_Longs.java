/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.commons.internal.primitives;

import java.util.OptionalLong;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.isis.commons.internal.exceptions._Exceptions;

import static org.apache.isis.commons.internal.base._With.requires;

import lombok.NonNull;
import lombok.Value;
import lombok.val;
import lombok.experimental.UtilityClass;

/**
 * <h1>- internal use only -</h1>
 * <p>
 * Long Utility
 * </p>
 * <p>
 * <b>WARNING</b>: Do <b>NOT</b> use any of the classes provided by this package! <br/>
 * These may be changed or removed without notice!
 * </p>
 *
 * @since 2.0
 */
@UtilityClass
public class _Longs {

    // -- RANGE

    @Value(staticConstructor = "of")
    public static class Bound {
        long value;
        boolean inclusive;
        public static @NonNull Bound inclusive(long value) { return of(value, true); }
        public static @NonNull Bound exclusive(long value) { return of(value, true); }
    }

    @Value(staticConstructor = "of")
    public static class Range {
        @NonNull Bound lowerBound;
        @NonNull Bound upperBound;
        public boolean contains(long value) {
            val isBelowLower = lowerBound.isInclusive()
                    ? value < lowerBound.getValue()
                    : value <= lowerBound.getValue();
            if(isBelowLower) {
                return false;
            }
            val isAboveUpper = upperBound.isInclusive()
                    ? value > upperBound.getValue()
                    : value >= upperBound.getValue();
            if(isAboveUpper) {
                return false;
            }
            return true;
        }
        /**
         * @param value
         * @return the value or if not within range, the nearest integer to the value, that is within range
         */
        public long bounded(long value) {
            //if(empty) return value; // noop
            if(contains(value)) {
                return value;
            }
            final long nearestToLower = nearestToLower();
            final long nearestToUpper = nearestToUpper();
            final long distanceToLower = value - nearestToLower;
            final long distanceToUpper = value - nearestToUpper;
            return (distanceToLower <= distanceToUpper)
                    ? nearestToLower
                    : nearestToUpper;
        }
        private long nearestToLower() {
            //if(empty) throw _Exceptions.unsupportedOperation();
            return lowerBound.isInclusive() ? lowerBound.getValue() : lowerBound.getValue()+1;
        }
        private long nearestToUpper() {
            //if(empty) throw _Exceptions.unsupportedOperation();
            return upperBound.isInclusive() ? upperBound.getValue() : upperBound.getValue()-1;
        }
        @Override
        public String toString() {
            return String.format("%s%d,%d%S",
                    lowerBound.isInclusive() ? '[' : '(', lowerBound.getValue(),
                    upperBound.getValue(), upperBound.isInclusive() ? ']' : ')');
        }
    }

    // -- RANGE FACTORIES

    /**
     * Range includes a and b.
     */
    public static Range rangeClosed(long a, long b) {
        if(a>b) {
            throw _Exceptions.illegalArgument("bounds must be ordered in [%d, %d]", a, b);
        }
        return Range.of(Bound.inclusive(a), Bound.inclusive(b));
    }

    /**
     * Range includes a but not b.
     */
    public static Range rangeOpenEnded(long a, long b) {
        if(a==b) {
            throw _Exceptions.unsupportedOperation("empty range not implemented");
            //return Range.empty();
        }
        if(a>=b) {
            throw _Exceptions.illegalArgument("bounds must be ordered in [%d, %d]", a, b);
        }
        return Range.of(Bound.inclusive(a), Bound.exclusive(b));
    }

    // -- PARSING

    /**
     * Parses the string argument as a signed integer in the radix
     * specified by the second argument. The characters in the string
     * must all be digits of the specified radix (as determined by
     * whether {@link java.lang.Character#digit(char, int)} returns a
     * nonnegative value), except that the first character may be an
     * ASCII minus sign {@code '-'} ({@code '\u005Cu002D'}) to
     * indicate a negative value or an ASCII plus sign {@code '+'}
     * ({@code '\u005Cu002B'}) to indicate a positive value. The
     * resulting integer value is returned.
     *
     *
     * <li>The radix is either smaller than
     * {@link java.lang.Character#MIN_RADIX} or
     * larger than {@link java.lang.Character#MAX_RADIX}.
     *
     * <li>Any character of the string is not a digit of the specified
     * radix, except that the first character may be a minus sign
     * {@code '-'} ({@code '\u005Cu002D'}) or plus sign
     * {@code '+'} ({@code '\u005Cu002B'}) provided that the
     * string is longer than length 1.
     *
     * <li>The value represented by the string is not a value of type
     * {@code int}.
     * </ul>
     *
     * @param      s   the {@code String} containing the integer
     *                  representation to be parsed
     * @param      radix   the radix to be used while parsing {@code s}.
     * @param      onFailure on parsing failure consumes the failure message
     * @return optionally the long represented by the string argument in the specified radix
     * @implNote Copied over from JDK's {@link Integer#parseInt(String)} to provide a variant
     * with minimum potential heap pollution (does not produce stack-traces on parsing failures)
     */
    public OptionalLong parseLong(@Nullable final String s, final int radix, final Consumer<String> onFailure) {
       requires(onFailure, "onFailure");

        if (s == null) {
            onFailure.accept("null");
            OptionalLong.empty();
        }

        if (radix < Character.MIN_RADIX) {
            onFailure.accept("radix " + radix + " less than Character.MIN_RADIX");
            OptionalLong.empty();
        }
        if (radix > Character.MAX_RADIX) {
            onFailure.accept("radix " + radix + " greater than Character.MAX_RADIX");
            OptionalLong.empty();
        }

        long result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        long limit = -Long.MAX_VALUE;
        long multmin;
        int digit;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Long.MIN_VALUE;
                } else if (firstChar != '+') {
                    onFailure.accept(s);
                    OptionalLong.empty();
                }
                if (len == 1) {// Cannot have lone "+" or "-"
                    onFailure.accept(s);
                    OptionalLong.empty();
                }
                i++;
            }
            multmin = limit / radix;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(s.charAt(i++),radix);
                if (digit < 0) {
                    onFailure.accept(s);
                    OptionalLong.empty();
                }
                if (result < multmin) {
                    onFailure.accept(s);
                    OptionalLong.empty();
                }
                result *= radix;
                if (result < limit + digit) {
                    onFailure.accept(s);
                    OptionalLong.empty();
                }
                result -= digit;
            }
        } else {
            onFailure.accept(s);
            OptionalLong.empty();
        }
        return OptionalLong.of(negative ? result : -result);
    }

    // -- SHORTCUTS

    public OptionalLong parseLong(final String s, final int radix) {
        return parseLong(s, radix, IGNORE_ERRORS);
    }

    // -- HELPER

    private static final Consumer<String> IGNORE_ERRORS = t->{};


}
