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
package demoapp.dom.types.isis.localresourcepaths.persistence;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.value.LocalResourcePath;

import demoapp.dom._infra.asciidocdesc.HasAsciiDocDescription;
import demoapp.dom._infra.values.ValueHolder;
import demoapp.dom.types.isis.localresourcepaths.holder.IsisLocalResourcePathHolder2;

@DomainObject(
        objectType = "demo.IsisLocalResourcePathEntity" // shared permissions with concrete sub class
)
public abstract class IsisLocalResourcePathEntity
implements
    HasAsciiDocDescription,
    IsisLocalResourcePathHolder2,
    ValueHolder<LocalResourcePath> {

    @Override
    public LocalResourcePath value() {
        return getReadOnlyProperty();
    }

}