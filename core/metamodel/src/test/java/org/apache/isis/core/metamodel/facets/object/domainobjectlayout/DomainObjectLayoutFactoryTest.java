/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.core.metamodel.facets.object.domainobjectlayout;

import org.assertj.core.api.Assertions;
import org.jmock.auto.Mock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.layout.component.CssClassFaPosition;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facets.AbstractFacetFactoryJUnit4TestCase;
import org.apache.isis.core.metamodel.facets.FacetFactory;
import org.apache.isis.core.metamodel.facets.all.describedas.DescribedAsFacet;
import org.apache.isis.core.metamodel.facets.all.named.NamedFacet;
import org.apache.isis.core.metamodel.facets.members.cssclass.CssClassFacet;
import org.apache.isis.core.metamodel.facets.members.cssclassfa.CssClassFaFacet;
import org.apache.isis.core.metamodel.facets.object.bookmarkpolicy.BookmarkPolicyFacet;
import org.apache.isis.core.metamodel.facets.object.paged.PagedFacet;
import org.apache.isis.core.metamodel.facets.object.plural.PluralFacet;
import org.apache.isis.core.metamodel.spec.ManagedObject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DomainObjectLayoutFactoryTest extends AbstractFacetFactoryJUnit4TestCase {

    DomainObjectLayoutFacetFactory facetFactory;

    // -- TEST LIFE CYCLING

    @Before
    public void setUp() throws Exception {
        facetFactory = new DomainObjectLayoutFacetFactory();
        facetFactory.setMetaModelContext(super.metaModelContext);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    // -- DOMAIN OBJECTS FOR TESTING

    @DomainObjectLayout(
            bookmarking = BookmarkPolicy.AS_ROOT,
            cssClass = "foobar",
            cssClassFa = "foo",
            cssClassFaPosition = CssClassFaPosition.RIGHT,
            describedAs = "This is a description",
            named = "Name override",
            paged = 20,
            plural = "Customers Plural Form"
            )
    class Customer { }

    @DomainObjectLayout 
    class CustomerWithDefaults { }

    // -- LAYOUT TESTS

    public static class Bookmarking extends DomainObjectLayoutFactoryTest {

        public static class ForDomainObjectLayout extends Bookmarking {

            @Before
            public void setUp2() throws Exception {

            }

            @Test
            public void whenSpecified() {

                final Class<?> cls = DomainObjectLayoutFactoryTest.Customer.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(BookmarkPolicyFacet.class);
                assertNotNull(facet);
                assertTrue(facet instanceof BookmarkPolicyFacetForDomainObjectLayoutAnnotation);

                final BookmarkPolicyFacetForDomainObjectLayoutAnnotation facetImpl = 
                        (BookmarkPolicyFacetForDomainObjectLayoutAnnotation) facet;

                Assert.assertThat(facetImpl.value(), is(BookmarkPolicy.AS_ROOT));

                expectNoMethodsRemoved();
            }

            @Test
            public void whenDefaults() {

                final Class<?> cls = CustomerWithDefaults.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final BookmarkPolicyFacet facet = facetHolder.getFacet(BookmarkPolicyFacet.class);
                Assert.assertThat(facet.value(), is(BookmarkPolicy.NOT_SPECIFIED));

                expectNoMethodsRemoved();
            }
        }

    }
    // --

    public static class CssClass extends DomainObjectLayoutFactoryTest {

        @Mock
        ManagedObject mockAdapter;

        public static class ForDomainObjectLayout extends CssClass {

            @Before
            public void setUp2() throws Exception {
            }

            @Override
            @Before
            public void setUp() throws Exception {
                super.setUp();
            }

            @Test
            public void whenSpecified() {

                final Class<?> cls = DomainObjectLayoutFactoryTest.Customer.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(CssClassFacet.class);
                assertNotNull(facet);
                assertTrue(facet instanceof CssClassFacetForDomainObjectLayoutAnnotation);

                final CssClassFacetForDomainObjectLayoutAnnotation facetImpl = (CssClassFacetForDomainObjectLayoutAnnotation) facet;
                Assertions.assertThat(facetImpl.cssClass(mockAdapter)).isEqualTo("foobar");

                expectNoMethodsRemoved();
            }

            @Test
            public void whenDefaults() {

                final Class<?> cls = CustomerWithDefaults.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(CssClassFacet.class);
                assertNull(facet);

                expectNoMethodsRemoved();
            }
        }

    }

    public static class CssClassFa extends DomainObjectLayoutFactoryTest {

        @Mock
        ManagedObject mockAdapter;

        public static class ForDomainObjectLayout extends CssClassFa {

            @Before
            public void setUp2() throws Exception {
            }


            @Test
            public void whenSpecified() {

                final Class<?> cls = DomainObjectLayoutFactoryTest.Customer.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(CssClassFaFacet.class);
                assertNotNull(facet);
                assertTrue(facet instanceof CssClassFaFacetForDomainObjectLayoutAnnotation);

                final CssClassFaFacetForDomainObjectLayoutAnnotation facetImpl = (CssClassFaFacetForDomainObjectLayoutAnnotation) facet;
                assertThat(facetImpl.asSpaceSeparated(), equalTo("fa fa-fw fa-foo"));
                assertThat(facetImpl.getPosition(), is(CssClassFaPosition.RIGHT));

                expectNoMethodsRemoved();
            }

            @Test
            public void whenDefaults() {

                final Class<?> cls = CustomerWithDefaults.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(CssClassFaFacet.class);
                assertNull(facet);

                expectNoMethodsRemoved();
            }
        }


    }

    public static class DescribedAs extends DomainObjectLayoutFactoryTest {

        @Mock
        ManagedObject mockAdapter;

        public static class ForDomainObjectLayout extends DescribedAs {

            @Before
            public void setUp2() throws Exception {
            }

            @Test
            public void whenSpecified() {

                final Class<?> cls = DomainObjectLayoutFactoryTest.Customer.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(DescribedAsFacet.class);
                assertNotNull(facet);
                assertTrue(facet instanceof DescribedAsFacetForDomainObjectLayoutAnnotation);

                final DescribedAsFacetForDomainObjectLayoutAnnotation facetImpl = (DescribedAsFacetForDomainObjectLayoutAnnotation) facet;
                Assert.assertThat(facetImpl.value(), is("This is a description"));

                expectNoMethodsRemoved();
            }

            @Test
            public void whenDefaults() {

                final Class<?> cls = CustomerWithDefaults.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(DescribedAsFacet.class);
                assertNull(facet);

                expectNoMethodsRemoved();
            }
        }


    }

    public static class Named extends DomainObjectLayoutFactoryTest {

        @Mock
        ManagedObject mockAdapter;

        public static class ForDomainObjectLayout extends Named {

            @Before
            public void setUp2() throws Exception {
            }

            @Test
            public void whenSpecified() {

                final Class<?> cls = DomainObjectLayoutFactoryTest.Customer.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(NamedFacet.class);
                assertNotNull(facet);
                assertTrue(facet instanceof NamedFacetForDomainObjectLayoutAnnotation);

                final NamedFacetForDomainObjectLayoutAnnotation facetImpl = (NamedFacetForDomainObjectLayoutAnnotation) facet;
                Assert.assertThat(facetImpl.value(), is("Name override"));

                expectNoMethodsRemoved();
            }

            @Test
            public void whenDefaults() {

                final Class<?> cls = CustomerWithDefaults.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(NamedFacet.class);
                assertNull(facet);

                expectNoMethodsRemoved();
            }
        }


    }

    public static class Paged extends DomainObjectLayoutFactoryTest {

        @Mock
        ManagedObject mockAdapter;

        public static class ForDomainObjectLayout extends Paged {

            @Before
            public void setUp2() throws Exception {
            }

            @Test
            public void whenSpecified() {

                final Class<?> cls = DomainObjectLayoutFactoryTest.Customer.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(PagedFacet.class);
                assertNotNull(facet);
                assertTrue(facet instanceof PagedFacetForDomainObjectLayoutAnnotation);

                final PagedFacetForDomainObjectLayoutAnnotation facetImpl = (PagedFacetForDomainObjectLayoutAnnotation) facet;
                Assert.assertThat(facetImpl.value(), is(20));

                expectNoMethodsRemoved();
            }

            @Test
            public void whenDefaults() {

                final Class<?> cls = CustomerWithDefaults.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls,mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(PagedFacet.class);
                assertNull(facet);

                expectNoMethodsRemoved();
            }
        }

    }

    public static class Plural extends DomainObjectLayoutFactoryTest {

        @Mock
        ManagedObject mockAdapter;

        public static class ForDomainObjectLayout extends Plural {

            @Before
            public void setUp2() throws Exception {
            }

            @Test
            public void whenSpecified() {

                final Class<?> cls = DomainObjectLayoutFactoryTest.Customer.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final Facet facet = facetHolder.getFacet(PluralFacet.class);
                assertNotNull(facet);
                assertTrue(facet instanceof PluralFacetForDomainObjectLayoutAnnotation);

                final PluralFacetForDomainObjectLayoutAnnotation facetImpl = (PluralFacetForDomainObjectLayoutAnnotation) facet;
                Assert.assertThat(facetImpl.value(), is("Customers Plural Form"));

                expectNoMethodsRemoved();
            }

            @Test
            public void whenDefaults() {

                final Class<?> cls = CustomerWithDefaults.class;

                facetFactory.process(new FacetFactory.ProcessClassContext(cls, mockMethodRemover, facetHolder));

                final PluralFacet facet = facetHolder.getFacet(PluralFacet.class);
                assertNotNull(facet);
                Assert.assertThat(facet.value(), is(""));

                expectNoMethodsRemoved();
            }
        }

    }

}
