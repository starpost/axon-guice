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

import com.google.code.axonguice.AxonGuiceModule;
import com.google.code.axonguice.commandhandling.annotation.CommandHandlerComponent;
import com.google.code.axonguice.grouping.AbstractClassesGroupingModule;
import com.google.code.axonguice.grouping.ClassesSearchGroup;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.unitofwork.UnitOfWork;
import org.axonframework.unitofwork.UnitOfWorkFactory;
import org.reflections.Reflections;

import java.util.Collection;

/**
 * Registers all command handling required components plus command handlers as CommandBus subscribers.
 *
 * @author Alexey Krylov
 * @see AxonGuiceModule#createCommandHandlingModule()
 * @since 06.02.13
 */
public abstract class CommandHandlingModule extends AbstractClassesGroupingModule<Object> {

	/*===========================================[ CONSTRUCTORS ]=================*/

    public CommandHandlingModule(Class<?>... commandHandlersClasses) {
        super(commandHandlersClasses);
    }

    public CommandHandlingModule(Collection<ClassesSearchGroup> commandHandlersClassesSearchGroup) {
        super(commandHandlersClassesSearchGroup);
    }

    public CommandHandlingModule(String... commandHandlersScanPackages) {
        super(commandHandlersScanPackages);
    }

	/*===========================================[ CLASS METHODS ]================*/

    protected void bindCommandHandlers(Iterable<Class<?>> handlerClasses) {
        logger.info("Binding Command Handlers");
        for (Class<?> handlerClass : handlerClasses) {
            logger.info(String.format("\tFound: [%s]", handlerClass.getName()));
            bindCommandHandler(handlerClass);
        }
    }

    protected void bindCommandHandler(Class<?> handlerClass) {
        Provider commandHandlerProvider = new AnnotationCommandHandlerProvider(handlerClass);
        requestInjection(commandHandlerProvider);
        bind(handlerClass).toProvider(commandHandlerProvider).asEagerSingleton();
    }

	/*===========================================[ INTERFACE METHODS ]============*/

    @Override
    protected void configure() {
        bindCommandBus();
        bindCommandGateway();
        bindUnitOfWorkFactory();
        bindUnitOfWork();
        bindCommandHandlers();
    }

    protected abstract void bindCommandBus();

    protected void bindCommandGateway() {
        bind(CommandGateway.class).toProvider(DefaultCommandGatewayProvider.class).in(Scopes.SINGLETON);
    }

    protected void bindUnitOfWorkFactory() {
        bind(UnitOfWorkFactory.class).toProvider(DefaultUnitOfWorkFactoryProvider.class).in(Scopes.SINGLETON);
    }

    protected void bindUnitOfWork() {
        bind(UnitOfWork.class).toProvider(UnitOfWorkProvider.class);
    }

    protected void bindCommandHandlers() {
        if (classesGroup.isEmpty()) {
            for (ClassesSearchGroup classesSearchGroup : classesSearchGroups) {
                Collection<String> packagesToScan = classesSearchGroup.getPackages();
                logger.info(String.format("Searching %s for Command Handlers", packagesToScan));

                Reflections reflections = createReflections(packagesToScan);

                // Extraction of instantiable @CommandHandler implementations
                bindCommandHandlers(filterSearchResult(reflections.getTypesAnnotatedWith(CommandHandlerComponent.class), classesSearchGroup));
            }
        } else {
            bindCommandHandlers(classesGroup);
        }
    }
    
}