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
package demoapp.dom.types.isis.blobs.jpa;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.context.annotation.Profile;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.persistence.jpa.applib.integration.JpaEntityInjectionPointResolver;

import demoapp.dom.types.isis.blobs.persistence.IsisBlobEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Profile("demo-jpa")
//tag::class[]
@Entity
@Table(
      schema = "demo",
      name = "IsisBlobJpa"
)
@EntityListeners(JpaEntityInjectionPointResolver.class)
@DomainObject(
      logicalTypeName = "demo.IsisBlobEntity"
)
@NoArgsConstructor
public class IsisBlobJpa
        extends IsisBlobEntity {

//end::class[]
    public IsisBlobJpa(Blob initialValue) {
        this.readOnlyProperty = initialValue;
        this.readWriteProperty = initialValue;
    }

//tag::class[]
    @Id
    @GeneratedValue
    private Long id;

    @Title(prepend = "Blob JPA entity: ")
    @PropertyLayout(fieldSetId = "read-only-properties", sequence = "1")
//    @Persistent(defaultFetchGroup="false", columns = {              // <.>
//            @Column(name = "readOnlyProperty_name"),
//            @Column(name = "readOnlyProperty_mimetype"),
//            @Column(name = "readOnlyProperty_bytes")
//    })
    @Getter @Setter
    private Blob readOnlyProperty;

    @Property(editing = Editing.ENABLED)                            // <.>
    @PropertyLayout(fieldSetId = "editable-properties", sequence = "1")
//    @Persistent(defaultFetchGroup="false", columns = {
//            @Column(name = "readWriteProperty_name"),
//            @Column(name = "readWriteProperty_mimetype"),
//            @Column(name = "readWriteProperty_bytes")
//    })
    @Getter @Setter
    private Blob readWriteProperty;

    @Property(optionality = Optionality.OPTIONAL)                   // <.>
    @PropertyLayout(fieldSetId = "optional-properties", sequence = "1")
//    @Persistent(defaultFetchGroup="false", columns = {
//            @Column(name = "readOnlyOptionalProperty_name",
//                    allowsNull = "true"),                           // <.>
//            @Column(name = "readOnlyOptionalProperty_mimetype",
//                    allowsNull = "true"),
//            @Column(name = "readOnlyOptionalProperty_bytes",
//                    allowsNull = "true")
//    })
    @Getter @Setter
    private Blob readOnlyOptionalProperty;

    @Property(editing = Editing.ENABLED, optionality = Optionality.OPTIONAL)
    @PropertyLayout(fieldSetId = "optional-properties", sequence = "2")
//    @Persistent(defaultFetchGroup="false", columns = {
//            @Column(name = "readWriteOptionalProperty_name",
//                    allowsNull = "true"),
//            @Column(name = "readWriteOptionalProperty_mimetype",
//                    allowsNull = "true"),
//            @Column(name = "readWriteOptionalProperty_bytes",
//                    allowsNull = "true")
//    })
    @Getter @Setter
    private Blob readWriteOptionalProperty;

}
//end::class[]
