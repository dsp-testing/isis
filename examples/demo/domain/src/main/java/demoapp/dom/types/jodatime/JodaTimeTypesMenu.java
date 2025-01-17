
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
package demoapp.dom.types.jodatime;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberSupport;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import demoapp.dom._infra.values.ValueHolderRepository;
import demoapp.dom.types.jodatime.jodadatetime.JodaDateTimes;
import demoapp.dom.types.jodatime.jodadatetime.persistence.JodaDateTimeEntity;
import demoapp.dom.types.jodatime.jodalocaldate.JodaLocalDates;
import demoapp.dom.types.jodatime.jodalocaldatetime.JodaLocalDateTimes;
import demoapp.dom.types.jodatime.jodalocaltime.JodaLocalTimes;

@DomainService(nature=NatureOfService.VIEW, logicalTypeName = "demo.JodaTimeTypesMenu")
@DomainObjectLayout(named="JodaTimeTypes")
//@Log4j2
public class JodaTimeTypesMenu {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-clock")
    public JodaDateTimes dateTimes(){
        return new JodaDateTimes();
    }
    @MemberSupport
    public String disableDateTimes(){
        return getJodaDisabledMessage();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-clock")
    public JodaLocalDates localDates(){
        return new JodaLocalDates();
    }
    @MemberSupport
    public String disableLocalDates(){
        return getJodaDisabledMessage();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-clock")
    public JodaLocalDateTimes localDateTimes(){
        return new JodaLocalDateTimes();
    }
    @MemberSupport
    public String disableLocalDateTimes(){
        return getJodaDisabledMessage();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa="fa-clock")
    public JodaLocalTimes localTimes(){
        return new JodaLocalTimes();
    }
    @MemberSupport
    public String disableLocalTimes(){
        return getJodaDisabledMessage();
    }

    // -- HELPER

    @Autowired(required = false)
    private ValueHolderRepository<org.joda.time.DateTime, ? extends JodaDateTimeEntity> entities;

    private boolean isJodaSupported() {
        return entities!=null;
    }

    private String getJodaDisabledMessage() {
        return isJodaSupported()
                ? null
                : "Joda is deprecated use java.time classes instead.";
    }


}
