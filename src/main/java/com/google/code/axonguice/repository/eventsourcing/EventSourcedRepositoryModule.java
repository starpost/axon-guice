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

package com.google.code.axonguice.repository.eventsourcing;

import java.util.Collection;

import org.axonframework.eventsourcing.EventSourcedAggregateRoot;
import org.axonframework.eventsourcing.SnapshotterTrigger;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.SnapshotEventStore;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.repository.Repository;

import com.google.code.axonguice.config.Duration;
import com.google.code.axonguice.grouping.ClassesSearchGroup;
import com.google.code.axonguice.repository.RepositoryModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Types;

/**
 * Implementation of {@link RepositoryModule} related to Event Sourcing. Default EventStore/SnapshotEventStore is
 * {@link FileSystemEventStore}.
 *
 * @author Alexey Krylov
 * @since 17.02.13
 */
public class EventSourcedRepositoryModule extends RepositoryModule<EventSourcedAggregateRoot> {

	/*===========================================[ CONSTRUCTORS ]=================*/

    @SafeVarargs
    public EventSourcedRepositoryModule(Class<? extends EventSourcedAggregateRoot>... classes) {
        super(classes);
    }

    public EventSourcedRepositoryModule(String... aggregatesRepositoriesScanPackages) {
        super(aggregatesRepositoriesScanPackages);
    }

    public EventSourcedRepositoryModule(Collection<ClassesSearchGroup> aggregatesRepositoriesSearchGroups) {
        super(aggregatesRepositoriesSearchGroups);
    }

    /*===========================================[ CLASS METHODS ]================*/

	@Override
    protected void configure() {
        bindEventStore();
        bindSnaphotEventStore();
        super.configure();
    }

    protected void bindEventStore() {
        // upcasting is here
        bind(EventStore.class).toProvider(FileSystemEventStoreProvider.class).in(Scopes.SINGLETON);
    }

    protected void bindSnaphotEventStore() {
        bind(SnapshotEventStore.class).toProvider(FileSystemEventStoreProvider.class).in(Scopes.SINGLETON);
    }

	/*===========================================[ INTERFACE METHODS ]============*/

    @Override
    protected void bindRepository(Class<? extends EventSourcedAggregateRoot> aggregateRootClass) {
        bindSnapshotterTrigger(aggregateRootClass);
        Provider repositoryProvider = new EventSourcingRepositoryProvider(aggregateRootClass);
        requestInjection(repositoryProvider);
        bind(Key.get(TypeLiteral.get(Types.newParameterizedType(Repository.class, aggregateRootClass)))).toProvider(repositoryProvider).in(Scopes.SINGLETON);
        logger.info(String.format("\t\tRepository set to: [%s]", repositoryProvider.getClass().getName()));
    }

	@Override
	protected void bindCachingRepository(Class<? extends EventSourcedAggregateRoot<?>> aggregateRootClass,
			Duration duration) {
        bindSnapshotterTrigger(aggregateRootClass);
        Provider repositoryProvider = new CachingEventSourcingRepositoryProvider(aggregateRootClass, duration);
        requestInjection(repositoryProvider);
        bind(Key.get(TypeLiteral.get(Types.newParameterizedType(Repository.class, aggregateRootClass)))).toProvider(repositoryProvider).in(Scopes.SINGLETON);
        logger.info(String.format("\t\tCaching Repository set to: [%s]", repositoryProvider.getClass().getName()));
	}

	protected void bindSnapshotterTrigger(Class<? extends EventSourcedAggregateRoot> aggregateRootClass) {
        Provider snapshotterTriggerProvider = new EventCountSnapshotterTriggerProvider(aggregateRootClass);
        requestInjection(snapshotterTriggerProvider);
        bind(Key.get(SnapshotterTrigger.class, Names.named(aggregateRootClass.getName()))).toProvider(snapshotterTriggerProvider).in(Scopes.SINGLETON);
        logger.info(String.format("\t\tSnapshotterTrigger set to: [%s]", snapshotterTriggerProvider.getClass().getName()));
    }

}