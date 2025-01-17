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

import java.util.stream.Stream;

public interface HasFacetHolder extends FacetHolder {

    // -- INTERFACE

    FacetHolder getFacetHolder();

    // -- SHORTCUTS

    @Override
    default public int getFacetCount() {
        return getFacetHolder().getFacetCount();
    }

    @Override
    default public <T extends Facet> T getFacet(Class<T> cls) {
        return getFacetHolder().getFacet(cls);
    }

    @Override
    default public boolean containsFacet(Class<? extends Facet> facetType) {
        return getFacetHolder().containsFacet(facetType);
    }

    @Override
    default public Stream<Facet> streamFacets() {
        return getFacetHolder().streamFacets();
    }

    @Override
    default public void addFacet(Facet facet) {
        getFacetHolder().addFacet(facet);
    }

    @Override
    default public void addOrReplaceFacet(Facet facet) {
        getFacetHolder().addOrReplaceFacet(facet);
    }

}
