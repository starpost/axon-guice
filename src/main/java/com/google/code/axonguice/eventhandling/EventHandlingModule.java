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

import java.util.Collection;
import java.util.concurrent.Executors;

import org.axonframework.eventhandling.AnnotationClusterSelector;
import org.axonframework.eventhandling.Cluster;
import org.axonframework.eventhandling.ClusterSelector;
import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.CompositeClusterSelector;
import org.axonframework.eventhandling.DefaultClusterSelector;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleCluster;
import org.axonframework.eventhandling.async.AsynchronousCluster;
import org.axonframework.eventhandling.async.FullConcurrencyPolicy;
import org.axonframework.eventhandling.async.SequencingPolicy;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.reflections.Reflections;

import com.google.code.axonguice.AxonGuiceModule;
import com.google.code.axonguice.eventhandling.annotation.EventHandlerComponent;
import com.google.code.axonguice.eventhandling.scheduling.SimpleEventSchedulerProvider;
import com.google.code.axonguice.grouping.AbstractClassesGroupingModule;
import com.google.code.axonguice.grouping.ClassesSearchGroup;
import com.google.common.collect.Lists;
import com.google.inject.Provider;
import com.google.inject.Scopes;

/**
 * Registers all event handling required components plus event handlers as EventBus subscribers.
 *
 * @author Alexey Krylov
 * @see AxonGuiceModule#createEventHandlingModule()
 * @since 06.02.13
 */
public class EventHandlingModule extends AbstractClassesGroupingModule<Object> {

    /*===========================================[ CONSTRUCTORS ]=================*/

    public EventHandlingModule(Class<?>... eventHandlersClasses) {
        super(eventHandlersClasses);
    }

    public EventHandlingModule(Collection<ClassesSearchGroup> eventHandlersClassesSearchGroups) {
        super(eventHandlersClassesSearchGroups);
    }

    public EventHandlingModule(String... eventHandlersScanPackages) {
        super(eventHandlersScanPackages);
    }

    /*===========================================[ INTERFACE METHODS ]============*/

    @Override
    protected void configure() {
        bindEventBus();
        bindEventHandlers();
        bindEventScheduler();
    }

    protected void bindEventBus() {
        Cluster defaultCluster = new SimpleCluster("default");
        
		Cluster backgroundCluster = new AsynchronousCluster("background", Executors.newCachedThreadPool(),
				new FullConcurrencyPolicy());

        DefaultClusterSelector defaultGroup = new DefaultClusterSelector(defaultCluster);
    	AnnotationClusterSelector bgGroup = new AnnotationClusterSelector(AsyncEventHandler.class, backgroundCluster);
    	
    	ClusterSelector selector = new CompositeClusterSelector(Lists.newArrayList(bgGroup, defaultGroup));
    	ClusteringEventBus bus = new ClusteringEventBus(selector);
    	bind(EventBus.class).toInstance(bus);
//        bind(EventBus.class).to(SimpleEventBus.class).in(Scopes.SINGLETON);
    }

    protected void bindEventHandlers() {
        logger.info("Binding Event Handlers");
        if (classesGroup.isEmpty()) {
            for (ClassesSearchGroup classesSearchGroup : classesSearchGroups) {
                Collection<String> packagesToScan = classesSearchGroup.getPackages();
                logger.info(String.format("Searching %s for Event Handlers", packagesToScan));

                Reflections reflections = createReflections(packagesToScan);

                // Extraction of instantiable @EventHandler implementations
                Iterable<Class<?>> validHandlerClasses = filterSearchResult(reflections.getTypesAnnotatedWith(EventHandlerComponent.class), classesSearchGroup);
                bindEventHandlers(validHandlerClasses);
            }
        } else {
            bindEventHandlers(classesGroup);
        }
    }

    protected void bindEventHandlers(Iterable<Class<?>> handlerClasses) {
        for (Class<?> handlerClass : handlerClasses) {
            logger.info(String.format("\tFound: [%s]", handlerClass.getName()));
            bindEventHandler(handlerClass);
        }
    }

    protected void bindEventHandler(Class<?> handlerClass) {
        Provider eventHandlerProvider = new AnnotationEventHandlerProvider(handlerClass);
        requestInjection(eventHandlerProvider);
        bind(handlerClass).toProvider(eventHandlerProvider).asEagerSingleton();
    }

    protected void bindEventScheduler() {
        bind(EventScheduler.class).toProvider(SimpleEventSchedulerProvider.class).in(Scopes.SINGLETON);
    }
}