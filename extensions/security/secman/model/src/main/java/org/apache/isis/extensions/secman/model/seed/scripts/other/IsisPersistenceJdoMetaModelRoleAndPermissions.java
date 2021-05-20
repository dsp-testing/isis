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
package org.apache.isis.extensions.secman.model.seed.scripts.other;

import org.apache.isis.applib.services.appfeat.ApplicationFeatureId;
import org.apache.isis.commons.collections.Can;
import org.apache.isis.extensions.secman.api.permission.dom.ApplicationPermissionMode;
import org.apache.isis.extensions.secman.api.permission.dom.ApplicationPermissionRule;
import org.apache.isis.extensions.secman.api.role.fixtures.AbstractRoleAndPermissionsFixtureScript;

/**
 * Provides access to download the JDO metamodel.
 *
 * @since 2.0 {@index}
 */
public class IsisPersistenceJdoMetaModelRoleAndPermissions
extends AbstractRoleAndPermissionsFixtureScript {

    private static final String SERVICE_OBJECT_TYPE = "isis.persistence.jdo.JdoMetamodelMenu";

    public static final String ROLE_NAME = SERVICE_OBJECT_TYPE.replace(".","-");

    public IsisPersistenceJdoMetaModelRoleAndPermissions() {
        super(ROLE_NAME, "Access to download the JDO metamodel");
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        newPermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                Can.of(
                        ApplicationFeatureId.newType(SERVICE_OBJECT_TYPE)
                        )
        );
    }
}