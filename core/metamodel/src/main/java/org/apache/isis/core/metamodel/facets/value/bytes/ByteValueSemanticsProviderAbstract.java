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

package org.apache.isis.core.metamodel.facets.value.bytes;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import org.apache.isis.applib.exceptions.recoverable.TextEntryParseException;
import org.apache.isis.core.metamodel.commons.LocaleUtil;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.object.value.vsp.ValueSemanticsProviderAndFacetAbstract;
import org.apache.isis.core.metamodel.spec.ManagedObject;

public abstract class ByteValueSemanticsProviderAbstract
extends ValueSemanticsProviderAndFacetAbstract<Byte>
implements ByteValueFacet {

    private static Class<? extends Facet> type() {
        return ByteValueFacet.class;
    }

    private static final Byte DEFAULT_VALUE = Byte.valueOf((byte) 0);
    private static final int MAX_LENGTH = 4; // allowing for -ve sign
    private static final int TYPICAL_LENGTH = MAX_LENGTH;

    private final NumberFormat format;

    public ByteValueSemanticsProviderAbstract(final FacetHolder holder, final Class<Byte> adaptedClass) {
        super(type(), holder, adaptedClass, TYPICAL_LENGTH, MAX_LENGTH, Immutability.IMMUTABLE, EqualByContent.HONOURED, DEFAULT_VALUE);
        final String formatRequired = getConfiguration().getValueTypes().getJavaLang().getByte().getFormat();

        format = formatRequired != null
                ? new DecimalFormat(formatRequired)
                : NumberFormat.getNumberInstance(getConfiguration().getCore().getRuntime().getLocale().map(LocaleUtil::findLocale).orElse(Locale.getDefault()));
    }


    // //////////////////////////////////////////////////////////////////
    // Parser
    // //////////////////////////////////////////////////////////////////

    @Override
    protected Byte doParse(final Object context, final String entry) {
        try {
            return Byte.valueOf(format.parse(entry).byteValue());
        } catch (final ParseException e) {
            throw new TextEntryParseException("Not a number " + entry, e);
        }
    }

    @Override
    public String titleString(final Object value) {
        return titleString(format, value);
    }

    // //////////////////////////////////////////////////////////////////
    // EncoderDecoder
    // //////////////////////////////////////////////////////////////////

    @Override
    public String doEncode(final Byte object) {
        return object.toString();
    }

    @Override
    protected Byte doRestore(final String data) {
        return Byte.parseByte(data);
    }

    @Override
    public String titleStringWithMask(final Object value, final String usingMask) {
        return titleString(new DecimalFormat(usingMask), value);
    }

    // //////////////////////////////////////////////////////////////////
    // ByteValueFacet
    // //////////////////////////////////////////////////////////////////

    @Override
    public Byte byteValue(final ManagedObject object) {
        return (Byte) object.getPojo();
    }

    @Override
    public ManagedObject createValue(final Byte value) {
        return getObjectManager().adapt(value);
    }

    // ///// toString ///////

    @Override
    public String toString() {
        return "ByteValueSemanticsProvider: " + format;
    }

    @Override public void appendAttributesTo(final Map<String, Object> attributeMap) {
        super.appendAttributesTo(attributeMap);
        attributeMap.put("format", format);
    }

}
