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

package org.apache.isis.core.metamodel.facets.object.ignore.javalang;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.commons.internal._Constants;
import org.apache.isis.commons.internal.collections._Lists;
import org.apache.isis.commons.internal.reflection._Annotations;
import org.apache.isis.core.metamodel.commons.ClassExtensions;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;

import lombok.val;

/**
 * Designed to simply filter out any synthetic methods.
 *
 * <p>
 * Does not add any {@link Facet}s.
 */
public class RemoveMethodsFacetFactory extends FacetFactoryAbstract {

    @SuppressWarnings("unused")
    private static final String JAVA_CLASS_PREFIX = "java.";

    public static class MethodAndParameterTypes {
        public final String methodName;
        public final Class<?>[] methodParameters;

        public MethodAndParameterTypes(final String methodName, final Class<?>[] methodParameters) {
            this.methodName = methodName;
            this.methodParameters = methodParameters;
        }
    }

    private final List<MethodAndParameterTypes> javaLangObjectMethodsToIgnore = _Lists.newArrayList();

    public RemoveMethodsFacetFactory() {
        super(FeatureType.OBJECTS_ONLY);

        final Class<?> typeToIgnore = Object.class;

        final Method[] methods = typeToIgnore.getMethods();
        for (final Method method : methods) {
            javaLangObjectMethodsToIgnore
            .add(new RemoveMethodsFacetFactory.MethodAndParameterTypes(method.getName(), method.getParameterTypes()));
        }

    }

    @Override
    public void process(final ProcessClassContext processClassContext) {
        super.process(processClassContext);

        Class<?> cls = processClassContext.getCls();
        Method[] methods = cls.getMethods();

        val config = processClassContext.getFacetHolder().getMetaModelContext().getConfiguration();
        val isExplicitAction = config.getApplib().getAnnotation().getAction().isExplicit();

        for (Method method : methods) {
            // remove synthetic methods
            if (method.isSynthetic()) {
                processClassContext.removeMethod(method);
            }

            // removeJavaLangComparable(processClassContext);
            if(method.getName().equals("compareTo")) {
                processClassContext.removeMethod(method);
            }

            // remove property setter, if has not explicitly an @Action annotation
            // this code block is not required, if @Action annotations are explicit per config
            if(!isExplicitAction
                    && method.getParameterCount() == 1
                    && method.getName().startsWith("set")
                    && method.getName().length() > 3) {

                if(!_Annotations.synthesize(method, Action.class).isPresent()) {
                    processClassContext.removeMethod(method);
                }
            }

        }

        removeSuperclassMethods(processClassContext.getCls(), processClassContext);

        // removeJavaLangObjectMethods(processClassContext);
        for (final MethodAndParameterTypes mapt : javaLangObjectMethodsToIgnore) {
            processClassContext.removeMethod(mapt.methodName, null, mapt.methodParameters);
        }

        // removeInitMethod(processClassContext);
        processClassContext.removeMethod("init", void.class, _Constants.emptyClasses);
    }

    private void removeSuperclassMethods(Class<?> type, final ProcessClassContext processClassContext) {
        if (type == null) {
            return;
        }

        if (!ClassExtensions.isJavaClass(type)) {
            removeSuperclassMethods(type.getSuperclass(), processClassContext);
            return;
        }

        final Method[] methods = type.getMethods();
        for (final Method method : methods) {
            processClassContext.removeMethod(method);
        }

    }

}
