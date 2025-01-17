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
package demoapp.dom.domain.objects.other.embedded;

import org.springframework.context.annotation.Profile;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.factory.FactoryService;

import lombok.RequiredArgsConstructor;

@Profile("demo-jdo")
@DomainService(nature=NatureOfService.VIEW, logicalTypeName = "demo.EmbeddedTypeMenu")
@RequiredArgsConstructor
public class EmbeddedTypeMenu {

    private final FactoryService factoryService;
    private final NumberConstantJdoRepository repo;

    @Action
    @ActionLayout(cssClassFa="fa-stop-circle", describedAs = "Experimental support for embedded types")
    public EmbeddedTypeVm embeddedTypes(){

        if(repo.listAll().size() == 0) {
            repo.add("Pi", ComplexNumberJdo.of(Math.PI, 0.));
            repo.add("Euler's Constant", ComplexNumberJdo.of(Math.E, 0.));
            repo.add("Imaginary Unit", ComplexNumberJdo.of(0, 1));
        }

        return factoryService.viewModel(new EmbeddedTypeVm());
    }
}
