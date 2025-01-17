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

package org.apache.isis.core.metamodel.facetapi;

import java.util.function.Consumer;

import org.apache.isis.core.metamodel.facets.actions.action.invocation.ActionInvocationFacet;

public interface Facet extends FacetWithAttributes {

    /**
     * The {@link FacetHolder holder} of this facet.
     */
    FacetHolder getFacetHolder();

    /**
     * Allows reparenting of Facet.
     *
     * <p>
     * Used by Facet decorators.
     *
     * @param facetHolder
     */
    public void setFacetHolder(FacetHolder facetHolder);

    /**
     * Underlying {@link Facet} of the same {@link #facetType() type}, if any.
     */
    public Facet getUnderlyingFacet();

    /**
     * Sets underlying {@link Facet}, that is, creating a chain.
     *
     * <p>
     * Must be of the same {@link #facetType() type}.
     */
    public void setUnderlyingFacet(Facet underlyingFacet);

    /**
     * Determines the type of this facet to be stored under.
     *
     * <p>
     * The framework looks for {@link Facet}s of certain well-known facet types.
     * Each facet implementation must specify which type of facet it corresponds
     * to. This therefore allows the (rules of the) programming model to be
     * varied without impacting the rest of the framework.
     *
     * <p>
     * For example, the <tt>ActionInvocationFacet</tt> specifies the facet to
     * invoke an action. The typical implementation of this wraps a
     * <tt>public</tt> method. However, a different facet factory could be
     * installed that creates facet also of type {@link ActionInvocationFacet}
     * but that have some other rule, such as requiring an <i>action</i> prefix,
     * or that decorate the interaction by logging it, for example.
     */
    Class<? extends Facet> facetType();

    /**
     * Whether this facet implementation is derived (as opposed to explicit);
     * used to determine precedence.
     *
     * <p>
     * For example, we might derive the typical length of a property based on
     * its type; but if the typical length has been explicitly specified using
     * an annotation then that should take precedence.
     */
    public boolean isDerived();

    /**
     * Whether this facet implementation is a fallback. Meaning it is treated
     * with lowest priority, always overruled by any other facet of same type.
     */
    public boolean isFallback();

    /**
     * Whether this facet implementation should replace existing (none-fallback)
     * implementations.
     */
    public boolean alwaysReplace();

    // -- FACET ALIAS SUPPORT

    /**
     * Adds a facet this facet contributes.
     * @since 2.0
     */
    void addContributedFacet(Facet contributedFacet);

    /**
     * Traverses all contributed facets (if any).
     * @since 2.0
     */
    void forEachContributedFacet(Consumer<Facet> onContributedFacet);

    /**
     * An alternative type this Facet can be looked up via {@link FacetHolder#getFacet(Class)}.
     * @apiNote like {@link #facetType()} the alias must be unique within any facet-holder's
     * registered facet-types, otherwise an {@link IllegalArgumentException} is thrown during
     * facet-processing; this is to ensure unambiguous lookup of facets by their alias type
     * @since 2.0
     */
    Class<? extends Facet> facetAliasType();

}
