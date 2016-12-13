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
import com.google.code.axonguice.commandhandling.SimpleCommandBusCommandHandlingModule;
import com.google.code.axonguice.commandhandling.SimpleCommandHandler;
import com.google.code.axonguice.domain.DomainModule;
import com.google.code.axonguice.domain.api.command.OrderCommandHandler;
import com.google.code.axonguice.domain.eventsourcing.EventSourcedDomainModule;
import com.google.code.axonguice.domain.model.Order;
import com.google.code.axonguice.eventhandling.EventHandlingModule;
import com.google.code.axonguice.eventhandling.SimpleEventHandler;
import com.google.code.axonguice.repository.RepositoryModule;
import com.google.code.axonguice.repository.eventsourcing.EventSourcedRepositoryModule;
import com.google.code.axonguice.saga.SagaModule;
import com.google.code.axonguice.saga.TestOrderSaga;

/**
 * @author Alexey Krylov
 * @since 07.02.13
 */
public class AxonGuiceTestModule extends AxonGuiceModule {

    /*===========================================[ CLASS METHODS ]================*/

    @Override
    protected DomainModule createDomainModule() {
        return new EventSourcedDomainModule(Order.class);
    }

    @Override
    protected RepositoryModule createRepositoryModule() {
        return new EventSourcedRepositoryModule(Order.class);
    }

    @Override
    protected CommandHandlingModule createCommandHandlingModule() {
        return new SimpleCommandBusCommandHandlingModule(SimpleCommandHandler.class, OrderCommandHandler.class);
    }

    @Override
    protected AggregateRootCommandHandlingModule createAggregateRootCommandHandlingModule() {
        return new AggregateRootCommandHandlingModule(Order.class);
    }

    @Override
    protected EventHandlingModule createEventHandlingModule() {
        return new EventHandlingModule(SimpleEventHandler.class);
    }

    @Override
    protected SagaModule createSagaModule() {
        return new SagaModule(TestOrderSaga.class);
    }
}