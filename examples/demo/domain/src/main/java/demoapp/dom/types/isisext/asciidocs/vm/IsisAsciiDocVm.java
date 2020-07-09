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
package demoapp.dom.types.isisext.asciidocs.vm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.valuetypes.asciidoc.applib.value.AsciiDoc;

import lombok.Getter;
import lombok.Setter;

import demoapp.dom._infra.asciidocdesc.HasAsciiDocDescription;
import demoapp.dom.types.isisext.asciidocs.holder.IsisAsciiDocHolder;

//tag::class[]
@XmlRootElement(name = "root")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@DomainObject(
        nature=Nature.VIEW_MODEL,
        objectType = "demo.IsisAsciiDocVm"
)
@lombok.NoArgsConstructor                                                       // <.>
public class IsisAsciiDocVm
        implements HasAsciiDocDescription, IsisAsciiDocHolder {

//end::class[]
    public IsisAsciiDocVm(AsciiDoc initialValue) {
        this.readOnlyProperty = initialValue;
        this.readWriteProperty = initialValue;
    }

//tag::class[]
    @Title(prepend = "AsciiDoc view model: ")
    @MemberOrder(name = "read-only-properties", sequence = "1")
    @XmlElement(required = true)                                                // <.>
    @Getter @Setter
    private AsciiDoc readOnlyProperty;

//end::class[]
    @Property(editing = Editing.ENABLED)                                        // <.>
    @PropertyLayout(hidden = Where.EVERYWHERE) // TODO: editable properties broken for view models - new value doesn't stick
    @MemberOrder(name = "editable-properties", sequence = "1")
    @XmlElement(required = true)
    @Getter @Setter
    private AsciiDoc readWriteProperty;

//tag::class[]
    @Property(optionality = Optionality.OPTIONAL)                               // <.>
    @MemberOrder(name = "optional-properties", sequence = "1")
    @Getter @Setter
    private AsciiDoc readOnlyOptionalProperty;

//end::class[]
    @Property(editing = Editing.ENABLED, optionality = Optionality.OPTIONAL)
    @PropertyLayout(hidden = Where.EVERYWHERE) // TODO: editable properties broken for view models - new value doesn't stick
    @MemberOrder(name = "optional-properties", sequence = "2")
    @Getter @Setter
    private AsciiDoc readWriteOptionalProperty;

//tag::class[]
}
//end::class[]
