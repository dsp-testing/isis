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
package org.apache.isis.viewer.wicket.model.models;

import java.util.Collections;
import java.util.List;

import org.apache.isis.commons.internal.exceptions._Exceptions;
import org.apache.isis.core.metamodel.spec.ManagedObject;
import org.apache.isis.core.metamodel.spec.feature.OneToManyAssociation;

import lombok.NonNull;

public class EntityCollectionModelDummy
extends EntityCollectionModelAbstract {

    private static final long serialVersionUID = 1L;

    public static EntityCollectionModelDummy forCollectionModel(
            final @NonNull EntityCollectionModel collectionModel) {
        return new EntityCollectionModelDummy(collectionModel);
    }

    protected EntityCollectionModelDummy(
            final @NonNull EntityCollectionModel collectionModel) {
        super(collectionModel.getCommonContext(),
                collectionModel.getMetaModel());
    }

    @Override
    public Variant getVariant() {
        return Variant.STANDALONE;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public String getName() {
        return "dummy";
    }

    @Override
    protected List<ManagedObject> load() {
        return Collections.emptyList();
    }

    @Override
    public OneToManyAssociation getMetaModel() {
        throw _Exceptions.unsupportedOperation();
    }

}
