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
package org.apache.isis.core.metamodel.spec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.repository.EntityState;
import org.apache.isis.core.commons.collections.Can;
import org.apache.isis.core.commons.internal.assertions._Assert;
import org.apache.isis.core.commons.internal.base._NullSafe;
import org.apache.isis.core.commons.internal.collections._Arrays;
import org.apache.isis.core.commons.internal.exceptions._Exceptions;
import org.apache.isis.core.metamodel.adapter.oid.RootOid;
import org.apache.isis.core.metamodel.commons.ClassExtensions;
import org.apache.isis.core.metamodel.commons.MethodExtensions;
import org.apache.isis.core.metamodel.commons.MethodUtil;
import org.apache.isis.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.isis.core.metamodel.facets.collections.CollectionFacet;
import org.apache.isis.core.metamodel.facets.object.viewmodel.ViewModelFacet;
import org.apache.isis.core.metamodel.interactions.InteractionUtils;
import org.apache.isis.core.metamodel.interactions.ObjectVisibilityContext;
import org.apache.isis.core.metamodel.interactions.VisibilityContext;
import org.apache.isis.core.metamodel.objectmanager.load.ObjectLoader;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;

import lombok.NonNull;
import lombok.val;
import lombok.experimental.UtilityClass;

/**
 * A collection of utilities for {@link ManagedObject}. 
 * @since 2.0
 *
 */
@UtilityClass
public final class ManagedObjects {
    
    // -- CATEGORISATION

    public static boolean isNullOrUnspecifiedOrEmpty(@Nullable ManagedObject adapter) {
        if(adapter==null || adapter==ManagedObject.unspecified()) {
            return true;
        }
        return adapter.getPojo()==null;
    }
    
    /** whether has at least a spec */
    public static boolean isSpecified(@Nullable ManagedObject adapter) {
        return adapter!=null && adapter!=ManagedObject.unspecified();
    }
    
    /**
     * @return whether the corresponding type can be mapped onto a REFERENCE (schema) or an Oid,
     * that is the type is 'identifiable' (aka 'referencable' or 'bookmarkable') 
     */
    public static boolean isIdentifiable(@Nullable ManagedObject adapter) {
        return spec(adapter)
                .map(ObjectSpecification::isIdentifiable)
                .orElse(false);
    }
    
    public static boolean isEntity(ManagedObject adapter) {
        return spec(adapter)
                .map(ObjectSpecification::isEntity)
                .orElse(false);
    }

    public static boolean isValue(ManagedObject adapter) {
        return spec(adapter)
                .map(ObjectSpecification::isValue)
                .orElse(false);
    }
    
    public static Optional<String> getDomainType(ManagedObject adapter) {
        return spec(adapter)
                .map(ObjectSpecification::getSpecId)
                .map(ObjectSpecId::asString);
    }
    
    // -- IDENTIFICATION
    
    public static Optional<ObjectSpecification> spec(@Nullable ManagedObject adapter) {
        return isSpecified(adapter) ? Optional.of(adapter.getSpecification()) : Optional.empty(); 
    }
    
    public static Optional<RootOid> identify(@Nullable ManagedObject adapter) {
        return isSpecified(adapter) ? adapter.getRootOid() : Optional.empty(); 
    }
    
    public static RootOid identifyElseFail(@Nullable ManagedObject adapter) {
        return identify(adapter)
                .orElseThrow(()->_Exceptions.illegalArgument("cannot identify %s", adapter));
    }
    
    public static Optional<Bookmark> bookmark(@Nullable ManagedObject adapter) {
        return identify(adapter)
                .map(RootOid::asBookmark);
    }
    
    public static Bookmark bookmarkElseFail(@Nullable ManagedObject adapter) {
        return bookmark(adapter)
                .orElseThrow(()->_Exceptions.illegalArgument("cannot bookmark %s", adapter));
    }
    
    public static Optional<String> stringify(@Nullable ManagedObject adapter) {
        return identify(adapter)
                .map(RootOid::enString);
    }
    
    public static String stringifyElseFail(@Nullable ManagedObject adapter) {
        return stringify(adapter)
                .orElseThrow(()->_Exceptions.illegalArgument("cannot stringify %s", adapter));
    }


    // -- COMPARE UTILITIES

    public static int compare(@Nullable ManagedObject p, @Nullable ManagedObject q) {
        return NATURAL_NULL_FIRST.compare(p, q);
    }

    public static Comparator<ManagedObject> orderingBy(ObjectAssociation sortProperty, boolean ascending) {

        final Comparator<ManagedObject> comparator = ascending 
                ? NATURAL_NULL_FIRST 
                : NATURAL_NULL_FIRST.reversed();

        return (p, q) -> {
            val pSort = sortProperty.get(p, InteractionInitiatedBy.FRAMEWORK);
            val qSort = sortProperty.get(q, InteractionInitiatedBy.FRAMEWORK);
            return comparator.compare(pSort, qSort);
        };

    }

    // -- PREDEFINED COMPARATOR

    private static final Comparator<ManagedObject> NATURAL_NULL_FIRST = new Comparator<ManagedObject>(){
        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public int compare(@Nullable ManagedObject p, @Nullable ManagedObject q) {
            val pPojo = ManagedObject.unwrapSingle(p);
            val qPojo = ManagedObject.unwrapSingle(q);
            if(pPojo instanceof Comparable && qPojo instanceof Comparable) {
                return _NullSafe.compareNullsFirst((Comparable)pPojo, (Comparable)qPojo);
            }
            if(Objects.equals(pPojo, qPojo)) {
                return 0;
            }

            final int hashCompare = Integer.compare(Objects.hashCode(pPojo), Objects.hashCode(qPojo));
            if(hashCompare!=0) {
                return hashCompare;
            }
            //XXX what to return on hash-collision?
            return -1;
        }

    };
    
    // -- COPY UTILITIES
    
    @Nullable 
    public static ManagedObject copyIfClonable(@Nullable ManagedObject adapter) {

        if(adapter==null) {
            return null;
        }
        
        val viewModelFacet = adapter.getSpecification().getFacet(ViewModelFacet.class);
        if(viewModelFacet != null) {
            val viewModelPojo = adapter.getPojo();
            if(viewModelFacet.isCloneable(viewModelPojo)) {
                return ManagedObject.of(
                        adapter.getSpecification(), 
                        viewModelFacet.clone(viewModelPojo));
            }
        }
        
        return adapter;
        
    }
    
    // -- TITLE UTILITIES
    
    public static String abbreviatedTitleOf(ManagedObject adapter, int maxLength, String suffix) {
        return abbreviated(titleOf(adapter), maxLength, suffix);
    }
    
    private static String titleOf(ManagedObject adapter) {
        return adapter!=null?adapter.titleString(null):"";
    }

    private static String abbreviated(final String str, final int maxLength, String suffix) {
        return str.length() < maxLength ? str : str.substring(0, maxLength - 3) + suffix;
    }

    // -- ENTITY UTILITIES
    
    /**
     * @param managedObject
     * @return managedObject
     * @throws AssertionError if managedObject is a detached entity  
     */
    public static ManagedObject requiresAttached(@NonNull ManagedObject managedObject) {
        val entityState = ManagedObject._entityState(managedObject);
        if(entityState.isPersistable()) {
            // ensure we have an attached entity
            _Assert.assertEquals(
                    EntityState.PERSISTABLE_ATTACHED, 
                    entityState,
                    ()-> String.format("entity %s is required to be attached (not detached) at this stage", 
                            managedObject.getSpecification().getSpecId()));
        }
        return managedObject;
    }
    
    @Nullable
    public static ManagedObject reattach(@Nullable ManagedObject managedObject) {
        if(isNullOrUnspecifiedOrEmpty(managedObject)) {
            return managedObject;
        }
        val entityState = ManagedObject._entityState(managedObject);
        if(!entityState.isPersistable()) {
            return managedObject;
        }
        if(!entityState.isDetached()) {
            return managedObject;
        }
        
        val objectIdentifier = identify(managedObject)
                .map(RootOid::getIdentifier);
                
        if(!objectIdentifier.isPresent()) {
            return managedObject;
        }
        
        val objectLoadRequest = ObjectLoader.Request.of(
                managedObject.getSpecification(), 
                objectIdentifier.get());
        
        return managedObject.getObjectManager().loadObject(objectLoadRequest);
    }

    // -- VISIBILITY UTIL
    
    @UtilityClass
    public static final class VisibilityUtil {
    
        public static Predicate<? super ManagedObject> filterOn(InteractionInitiatedBy interactionInitiatedBy) {
            return $->ManagedObjects.VisibilityUtil.isVisible($, interactionInitiatedBy);
        }
    
        /**
         * Filters a collection (an adapter around either a Collection or an Object[]) and returns a stream of
         * {@link ManagedObject}s of those that are visible (as per any facet(s) installed on the element class
         * of the collection).
         * @param collectionAdapter - an adapter around a collection (as returned by a getter of a collection, or of an autoCompleteNXxx() or choicesNXxx() method, etc
         * @param interactionInitiatedBy
         */
        public static Stream<ManagedObject> streamVisibleAdapters(
                final ManagedObject collectionAdapter,
                final InteractionInitiatedBy interactionInitiatedBy) {
    
            return CollectionFacet.streamAdapters(collectionAdapter)
                    .filter(VisibilityUtil.filterOn(interactionInitiatedBy));
        }
        
        private static Stream<Object> streamVisiblePojos(
                final ManagedObject collectionAdapter,
                final InteractionInitiatedBy interactionInitiatedBy) {
    
            return CollectionFacet.streamAdapters(collectionAdapter)
                    .filter(VisibilityUtil.filterOn(interactionInitiatedBy))
                    .map(ManagedObject::unwrapSingle);
        }
        
        public static Object[] visiblePojosAsArray(
                final ManagedObject collectionAdapter,
                final InteractionInitiatedBy interactionInitiatedBy) {
    
            return streamVisiblePojos(collectionAdapter, interactionInitiatedBy)
                    .collect(_Arrays.toArray(Object.class));
        }
        
        public static Object visiblePojosAutofit(
                final ManagedObject collectionAdapter,
                final InteractionInitiatedBy interactionInitiatedBy,
                final Class<?> requiredContainerType) {
            
            val visiblePojoStream = streamVisiblePojos(collectionAdapter, interactionInitiatedBy);
            val autofittedObjectContainer = CollectionFacet.AutofitUtils
                    .collect(visiblePojoStream, requiredContainerType);
            return autofittedObjectContainer;
        }
        
        
        /**
         * @param adapter - an adapter around the domain object whose visibility is being checked
         * @param interactionInitiatedBy
         */
        public static boolean isVisible(
                ManagedObject adapter,
                InteractionInitiatedBy interactionInitiatedBy) {
    
            if(isNullOrUnspecifiedOrEmpty(adapter)) {
                // a choices list could include a null (eg example in ToDoItems#choices1Categorized()); want to show as "visible"
                return true;
            }
            val spec = adapter.getSpecification();
            if(spec.isEntity()) {
                if(ManagedObject._isDestroyed(adapter)) {
                    return false;
                }
            }
            if(interactionInitiatedBy == InteractionInitiatedBy.FRAMEWORK) { 
                return true; 
            }
            val visibilityContext = createVisibleInteractionContext(
                    adapter,
                    InteractionInitiatedBy.USER,
                    Where.OBJECT_FORMS);
    
            return InteractionUtils.isVisibleResult(spec, visibilityContext)
                    .isNotVetoing();
        }
        
        private static VisibilityContext createVisibleInteractionContext(
                final ManagedObject adapter,
                final InteractionInitiatedBy interactionInitiatedBy,
                final Where where) {
            
            return new ObjectVisibilityContext(
                    adapter,
                    adapter.getSpecification().getIdentifier(),
                    interactionInitiatedBy,
                    where);
        }
    
    }
    
    
    // -- INVOCATION UTILITY
    
    @UtilityClass
    public static final class InvokeUtil {
    
        public static Object invokeWithPPM(
                final Constructor<?> ppmConstructor, 
                final Method method, 
                final ManagedObject adapter, 
                final Can<ManagedObject> pendingArguments,
                final List<Object> additionalArguments) {
            
            val ppmTuple = MethodExtensions.construct(ppmConstructor, ManagedObject.unwrapMultipleAsArray(pendingArguments));
            val paramPojos = _Arrays.combineWithExplicitType(Object.class, ppmTuple, additionalArguments.toArray());
            return MethodExtensions.invoke(method, ManagedObject.unwrapSingle(adapter), paramPojos);
        }
        
        public static Object invokeWithPPM(
                final Constructor<?> ppmConstructor, 
                final Method method, 
                final ManagedObject adapter, 
                final Can<ManagedObject> argumentAdapters) {
            return invokeWithPPM(ppmConstructor, method, adapter, argumentAdapters, Collections.emptyList());
        }
        
        public static void invokeAll(Collection<Method> methods, final ManagedObject adapter) {
            MethodUtil.invoke(methods, ManagedObject.unwrapSingle(adapter));
        }
    
        public static Object invoke(Method method, ManagedObject adapter) {
            return MethodExtensions.invoke(method, ManagedObject.unwrapSingle(adapter));
        }
    
        public static Object invoke(Method method, ManagedObject adapter, Object arg0) {
            return MethodExtensions.invoke(method, ManagedObject.unwrapSingle(adapter), new Object[] {arg0});
        }
    
        public static Object invoke(Method method, ManagedObject adapter, Can<ManagedObject> argumentAdapters) {
            return MethodExtensions.invoke(method, ManagedObject.unwrapSingle(adapter), ManagedObject.unwrapMultipleAsArray(argumentAdapters));
        }
    
        public static Object invoke(Method method, ManagedObject adapter, ManagedObject arg0Adapter) {
            return invoke(method, adapter, ManagedObject.unwrapSingle(arg0Adapter));
        }
    
        public static Object invoke(Method method, ManagedObject adapter, ManagedObject[] argumentAdapters) {
            return MethodExtensions.invoke(method, ManagedObject.unwrapSingle(adapter), ManagedObject.unwrapMultipleAsArray(argumentAdapters));
        }

        /**
         * Invokes the method, adjusting arguments as required to make them fit the method's parameters.
         * <p>
         * That is:
         * <ul>
         * <li>if the method declares parameters but arguments are missing, then will provide 'null' defaults for these.</li>
         * </ul>
         */
        public static Object invokeAutofit(Method method, ManagedObject adapter) {
            return invoke(method, adapter, new ManagedObject[method.getParameterTypes().length]);
        }
    
        /**
         * Invokes the method, adjusting arguments as required to make them fit the method's parameters.
         * <p>
         * That is:
         * <ul>
         * <li>if the method declares parameters but arguments are missing, then will provide 'null' defaults for these.</li>
         * <li>if the method does not declare all parameters for arguments, then truncates arguments.</li>
         * <li>any {@code additionalArgValues} must also fit at the end of the resulting parameter list</li>
         * </ul>
         */
        public static Object invokeAutofit(
                final Method method, 
                final ManagedObject target, 
                final Can<? extends ManagedObject> pendingArgs,
                final List<Object> additionalArgValues) {
    
            val argArray = adjust(method, pendingArgs, additionalArgValues);
            
            return MethodExtensions.invoke(method, ManagedObject.unwrapSingle(target), argArray);
        }
    
        /**
         * same as {@link #invokeAutofit(Method, ManagedObject, List, List)} w/o additionalArgValues
         */
        public static Object invokeAutofit(
                final Method method, 
                final ManagedObject target, 
                final Can<? extends ManagedObject> pendingArgs) {
            
            return invokeAutofit(method, target, pendingArgs, Collections.emptyList());
        }
    
        private static Object[] adjust(
                final Method method, 
                final Can<? extends ManagedObject> pendingArgs,
                final List<Object> additionalArgValues) {
            
            val parameterTypes = method.getParameterTypes();
            val paramCount = parameterTypes.length;
            val additionalArgCount = additionalArgValues.size();
            val pendingArgsToConsiderCount = paramCount - additionalArgCount;
            
            val argIterator = argIteratorFrom(pendingArgs);
            val adjusted = new Object[paramCount];
            for(int i=0; i<pendingArgsToConsiderCount; i++) {
                
                val paramType = parameterTypes[i];
                val arg = argIterator.hasNext() ? ManagedObject.unwrapSingle(argIterator.next()) : null;
                
                adjusted[i] = honorPrimitiveDefaults(paramType, arg);
            }
            
            // add the additional parameter values (if any)
            int paramIndex = pendingArgsToConsiderCount;
            for(val additionalArg : additionalArgValues) {
                val paramType = parameterTypes[paramIndex];
                adjusted[paramIndex] = honorPrimitiveDefaults(paramType, additionalArg);
                ++paramIndex;
            }
            
            return adjusted;
    
        }
    
        private static Iterator<? extends ManagedObject> argIteratorFrom(Can<? extends ManagedObject> pendingArgs) {
            return pendingArgs!=null ? pendingArgs.iterator() : Collections.emptyIterator();
        }
    
        private static Object honorPrimitiveDefaults(
                final Class<?> expectedType, 
                final @Nullable Object value) {
            
            if(value == null && expectedType.isPrimitive()) {
                return ClassExtensions.toDefault(expectedType);
            }
            return value;
        }
        
    
    }
    

}
