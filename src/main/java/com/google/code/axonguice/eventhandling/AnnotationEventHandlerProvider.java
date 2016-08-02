/*
 * Copyright (C) 2013 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.axonguice.eventhandling;

import javax.inject.Inject;

import org.axonframework.common.annotation.ParameterResolverFactory;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerAdapter;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

/**
 * Registers specified handler class as EventHandler subscriber.
 *
 * @author Alexey Krylov
 * @see AnnotationEventListenerAdapter
 * @see EventHandlingModule#bindEventHandler(Class)
 * @since 07.02.13
 */
public class AnnotationEventHandlerProvider implements Provider {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    protected Injector injector;
    protected EventBus eventBus;
    protected ParameterResolverFactory resolver; 
    protected Class<?> handlerClass;

    /*===========================================[ CONSTRUCTORS ]=================*/

    public AnnotationEventHandlerProvider(Class<?> handlerClass) {
        this.handlerClass = handlerClass;
    }

    @Inject
    void init(Injector injector, EventBus eventBus, ParameterResolverFactory resolver) {
        this.injector = injector;
        this.eventBus = eventBus;
        this.resolver = resolver;
    }

    /*===========================================[ INTERFACE METHODS ]============*/

    @Override
    public Object get() {
        try {
            Object handlerInstance = handlerClass.newInstance();
            injector.injectMembers(handlerInstance);
            AnnotationEventListenerAdapter adapter = new AnnotationEventListenerAdapter(handlerInstance, resolver);
            eventBus.subscribe(adapter);
            return handlerInstance;
        } catch (Exception e) {
            throw new ProvisionException(String.format("Unable to instantiate EventHandler class: [%s]", handlerClass), e);
        }
    }
}