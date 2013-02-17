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

package com.google.code.axonguice;

import com.google.code.axonguice.commandhandling.AggregateRootCommandHandlingModule;
import com.google.code.axonguice.commandhandling.CommandHandlingModule;
import com.google.code.axonguice.common.annotation.ParameterResolverModule;
import com.google.code.axonguice.domain.DomainModule;
import com.google.code.axonguice.eventhandling.EventHandlingModule;
import com.google.code.axonguice.jsr250.Jsr250Module;
import com.google.code.axonguice.repository.RepositoryModule;
import com.google.code.axonguice.saga.SagaModule;
import com.google.inject.AbstractModule;

import java.util.Arrays;

public class AxonGuiceModule extends AbstractModule {

    /*===========================================[ INSTANCE VARIABLES ]===========*/

    protected String[] packages;

    /*===========================================[ CONSTRUCTORS ]=================*/

    public AxonGuiceModule(String... packages) {
        if (packages != null && packages.length > 0) {
            this.packages = Arrays.copyOf(packages, packages.length);
        }
    }

    /*===========================================[ INTERFACE METHODS ]============*/

    @Override
    protected void configure() {
        // support of @PostConstuct and @Resource
        if (isJsr250SupportEnabled()) {
            install(createJsr250Module());
        }

        install(createParameterResolverModule());
        install(createDomainModule());
        install(createRepositoryModule());
        install(createCommandHandlingModule());
        install(createAggregateRootCommandHandlingModule());
        install(createEventHandlingModule());
        install(createSagaModule());
    }

    protected boolean isJsr250SupportEnabled() {
        return true;
    }

    /**
     * Override to provide your custom JSR-250 support module.
     *
     * @return JSR-250 support module
     */
    protected Jsr250Module createJsr250Module() {
        return new Jsr250Module();
    }

    protected ParameterResolverModule createParameterResolverModule() {
        return new ParameterResolverModule();
    }

    protected DomainModule createDomainModule() {
        return new DomainModule(packages);
    }

    protected RepositoryModule createRepositoryModule() {
        return new RepositoryModule(packages);
    }

    protected CommandHandlingModule createCommandHandlingModule() {
        return new CommandHandlingModule(packages);
    }

    protected AggregateRootCommandHandlingModule createAggregateRootCommandHandlingModule() {
        return new AggregateRootCommandHandlingModule(packages);
    }

    protected EventHandlingModule createEventHandlingModule() {
        return new EventHandlingModule(packages);
    }

    protected SagaModule createSagaModule() {
        return new SagaModule(packages);
    }
}