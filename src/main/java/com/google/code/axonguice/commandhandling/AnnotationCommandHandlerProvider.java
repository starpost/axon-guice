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

package com.google.code.axonguice.commandhandling;


import javax.inject.Inject;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerAdapter;
import org.axonframework.common.annotation.ParameterResolverFactory;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

/**
 * Registers specified handler class as CommandBus subscriber.
 *
 * @author Alexey Krylov
 * @see CommandHandlingModule#bindCommandHandler(Class)
 * @see AnnotationCommandHandlerAdapter
 * @since 07.02.13
 */
public class AnnotationCommandHandlerProvider implements Provider {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    protected Injector injector;
    protected CommandBus commandBus;
    protected ParameterResolverFactory resolver; 
    protected Class<?> handlerClass;

    /*===========================================[ CONSTRUCTORS ]=================*/

    public AnnotationCommandHandlerProvider(Class<?> handlerClass) {
        this.handlerClass = handlerClass;
    }

    @Inject
    void init(Injector injector, CommandBus commandBus, ParameterResolverFactory resolver) {
        this.injector = injector;
        this.commandBus = commandBus;
        this.resolver = resolver;
    }

    /*===========================================[ INTERFACE METHODS ]============*/

    @Override
    public Object get() {
        try {
            Object handlerInstance = handlerClass.newInstance();
            injector.injectMembers(handlerInstance);
            // AnnotationCommandHandlerAdapter.subscribe(handlerInstance, commandBus);
            AnnotationCommandHandlerAdapter adapter = new AnnotationCommandHandlerAdapter(handlerInstance, resolver);
            for (String cmd : adapter.supportedCommands()) {
                commandBus.subscribe(cmd, adapter);
            }
            return handlerInstance;
        } catch (Exception e) {
            throw new ProvisionException(String.format("Unable to instantiate CommandHandler class: [%s]", handlerClass), e);
        }
    }
}