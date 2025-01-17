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
package org.apache.isis.viewer.wicket.model.links;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.ActionLayout.Position;
import org.apache.isis.applib.id.LogicalType;
import org.apache.isis.commons.collections.Can;
import org.apache.isis.commons.internal.exceptions._Exceptions;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.viewer.common.model.action.ActionUiMetaModel;
import org.apache.isis.viewer.wicket.model.common.CommonContextUtils;
import org.apache.isis.viewer.wicket.model.models.EntityModel;

import lombok.NonNull;
import lombok.val;

public final class LinkAndLabel extends LinkAndLabelAbstract {

    private static final long serialVersionUID = 1L;

    public static LinkAndLabel of(
            final ActionLinkUiComponentFactoryWkt uiComponentFactory,
            final String named,
            final EntityModel actionHolderModel,
            final ObjectAction objectAction) {
        return new LinkAndLabel(uiComponentFactory, named, actionHolderModel, objectAction);
    }

    private LinkAndLabel(
            final ActionLinkUiComponentFactoryWkt uiComponentFactory,
            final String named,
            final EntityModel actionHolderModel,
            final ObjectAction objectAction) {
        super(uiComponentFactory, named, actionHolderModel, objectAction);
    }

    public static Can<LinkAndLabel> positioned(Position pos, Stream<LinkAndLabel> stream) {
        return stream.filter(LinkAndLabel.positioned(pos))
        .collect(Can.toCan());
    }

    public static Predicate<LinkAndLabel> positioned(Position pos) {
        return ActionUiMetaModel.positioned(pos, LinkAndLabel::getActionUiMetaModel);
    }

    public static List<LinkAndLabel> recoverFromIncompleteDeserialization(List<SerializationProxy> proxies) {
        return proxies.stream()
                .map(SerializationProxy::restore)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // -- SERIALIZATION PROXY

    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 1L;
        private final @NonNull ActionLinkUiComponentFactoryWkt uiComponentFactory;
        private final @Nullable String named;
        private final @NonNull EntityModel actionHolder;
        private final @NonNull LogicalType actionHolderLogicalType;
        private final @NonNull String objectActionId;

        private SerializationProxy(LinkAndLabel target) {
            this.uiComponentFactory = target.uiComponentFactory;
            this.named = target.getNamed();
            this.actionHolder = (EntityModel) target.getActionHolder();
            // make sure we do this without side-effects
            this.actionHolderLogicalType = actionHolder.getLogicalElementType()
                    .orElseThrow(_Exceptions::unexpectedCodeReach);
            this.objectActionId = target.getObjectAction().getId();
        }

        private Object readResolve() {
            return restore();
        }

        private LinkAndLabel restore() {
            val commonContext = CommonContextUtils.getCommonContext();
            val objectMember = commonContext.getSpecificationLoader()
            .specForLogicalType(actionHolderLogicalType)
            .flatMap(actionHolderSpec->actionHolderSpec.getMember(objectActionId))
            .orElseThrow(()->
                _Exceptions.noSuchElement("could not restore objectAction from id %s", objectActionId));
            return new LinkAndLabel(uiComponentFactory, named, actionHolder, (ObjectAction) objectMember);
        }

    }

}
