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

package org.apache.isis.core.metamodel.facets.value;

import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.commons.internal.base._Casts;
import org.apache.isis.core.internaltestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.internaltestsupport.jmocking.JUnitRuleMockery2.Mode;
import org.apache.isis.core.metamodel._testing.MetaModelContext_forTesting;
import org.apache.isis.core.metamodel.context.MetaModelContext;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.object.encodeable.EncodableFacet;
import org.apache.isis.core.metamodel.facets.object.encodeable.encoder.EncodableFacetUsingEncoderDecoder;
import org.apache.isis.core.metamodel.facets.object.parseable.ParseableFacet;
import org.apache.isis.core.metamodel.facets.object.parseable.parser.ParseableFacetUsingParser;
import org.apache.isis.core.metamodel.facets.object.value.vsp.ValueSemanticsProviderAndFacetAbstract;
import org.apache.isis.core.metamodel.spec.ManagedObject;
import org.apache.isis.core.security.authentication.AuthenticationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public abstract class ValueSemanticsProviderAbstractTestCase {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock protected FacetHolder mockFacetHolder;
    @Mock protected AuthenticationContext mockAuthenticationContext;
    @Mock protected ManagedObject mockAdapter;
    
    protected MetaModelContext metaModelContext;
    
    private ValueSemanticsProviderAndFacetAbstract<?> valueSemanticsProvider;
    private EncodableFacetUsingEncoderDecoder encodeableFacet;
    private ParseableFacetUsingParser parseableFacet;

    @Before
    public void setUp() throws Exception {

        Locale.setDefault(Locale.UK);

        metaModelContext = MetaModelContext_forTesting.builder()
                .authenticationContext(mockAuthenticationContext)
                .build();

        context.checking(new Expectations() {
            {

                never(mockAuthenticationContext);
                //never(mockSessionServiceInternal);
                
                allowing(mockFacetHolder).getMetaModelContext();
                will(returnValue(metaModelContext));
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        context.assertIsSatisfied();
    }

    protected void allowMockAdapterToReturn(final Object pojo) {
        context.checking(new Expectations() {
            {
                allowing(mockAdapter).getPojo();
                will(returnValue(pojo));
            }
        });
    }

    protected void setValue(final ValueSemanticsProviderAndFacetAbstract<?> value) {
        this.valueSemanticsProvider = value;
        this.encodeableFacet = new EncodableFacetUsingEncoderDecoder(
                value, 
                mockFacetHolder);
        this.parseableFacet = new ParseableFacetUsingParser(value, mockFacetHolder);
    }

    protected <T> ValueSemanticsProviderAndFacetAbstract<T> getValue(Class<T> type) {
        return _Casts.uncheckedCast(valueSemanticsProvider);
    }

    protected EncodableFacet getEncodeableFacet() {
        return encodeableFacet;
    }

    protected ParseableFacet getParseableFacet() {
        return parseableFacet;
    }

    protected ManagedObject createAdapter(final Object object) {
        return mockAdapter;
    }

    @Test
    public void testParseNull() throws Exception {
        Assume.assumeThat(valueSemanticsProvider.getParser(), is(not(nullValue())));
        try {
            valueSemanticsProvider.parseTextEntry(null, null);
            fail();
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testParseEmptyString() throws Exception {
        Assume.assumeThat(valueSemanticsProvider.getParser(), is(not(nullValue())));

        final Object newValue = valueSemanticsProvider.parseTextEntry(null, "");
        assertNull(newValue);
    }

    @Test
    public void testDecodeNULL() throws Exception {
        Assume.assumeThat(valueSemanticsProvider.getEncoderDecoder(), is(not(nullValue())));

        final Object newValue = encodeableFacet.fromEncodedString(EncodableFacetUsingEncoderDecoder.ENCODED_NULL);
        assertNull(newValue);
    }

    @Test
    public void testEmptyEncoding() {
        Assume.assumeThat(valueSemanticsProvider.getEncoderDecoder(), is(not(nullValue())));

        assertEquals(EncodableFacetUsingEncoderDecoder.ENCODED_NULL, encodeableFacet.toEncodedString(null));
    }

    @Test
    public void testTitleOfForNullObject() {
        assertEquals("", valueSemanticsProvider.displayTitleOf(null));
    }
}
