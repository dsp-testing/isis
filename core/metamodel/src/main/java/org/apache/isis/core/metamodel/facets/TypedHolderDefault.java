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
package org.apache.isis.core.metamodel.facets;

import org.apache.isis.core.metamodel.facetapi.FacetHolderImpl;
import org.apache.isis.core.metamodel.facetapi.FeatureType;

public class TypedHolderDefault extends FacetHolderImpl implements TypedHolder {

    private final FeatureType featureType;
    private Class<?> type;

    public TypedHolderDefault(final FeatureType featureType, final Class<?> type) {
        this.featureType = featureType;
        this.type = type;
    }

    @Override
    public FeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    /**
     * For {@link FeatureType#COLLECTION collection}s and for
     * {@link FeatureType#ACTION_PARAMETER_COLLECTION}s, represents the element type.
     */
    @Override
    public void setType(final Class<?> type) {
        this.type = type;
    }

    @Override // as used for logging, not strictly required
    public String toString() {
        return type.getSimpleName();
    }

}
