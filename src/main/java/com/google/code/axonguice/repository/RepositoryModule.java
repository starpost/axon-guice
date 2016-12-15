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

package com.google.code.axonguice.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.axonframework.domain.AggregateRoot;
import org.axonframework.eventsourcing.EventSourcedAggregateRoot;
import org.reflections.Reflections;

import com.google.code.axonguice.AxonGuiceModule;
import com.google.code.axonguice.config.Duration;
import com.google.code.axonguice.grouping.AbstractClassesGroupingModule;
import com.google.code.axonguice.grouping.ClassesSearchGroup;
import com.google.code.axonguice.util.ReflectionsHelper;
import com.google.common.collect.Maps;

/**
 * Registers all Aggregate Roots repositories and all related components.
 *
 * @author Alexey Krylov
 * @see AxonGuiceModule#createRepositoryModule()
 * @since 06.02.13
 */
public abstract class RepositoryModule<T extends AggregateRoot> extends AbstractClassesGroupingModule<T> {

    private Map<Class<? extends EventSourcedAggregateRoot<?>>, Duration> cachingClasses = Maps.newHashMap();

	/*===========================================[ CONSTRUCTORS ]=================*/

    @SafeVarargs
    protected RepositoryModule(Class<? extends T>... classes) {
        super(classes);
    }

    protected RepositoryModule(String... aggregatesRepositoriesScanPackages) {
        super(aggregatesRepositoriesScanPackages);
    }

    protected RepositoryModule(Collection<ClassesSearchGroup> aggregatesRepositoriesSearchGroups) {
        super(aggregatesRepositoriesSearchGroups);
    }

    /*===========================================[ INTERFACE METHODS ]============*/

    @Override
    protected void configure() {
        bindRepositories();
    }

	public void setCachingClasses(Map<Class<? extends EventSourcedAggregateRoot<?>>, Duration> map) {
		this.cachingClasses = map;
	}

    protected void bindRepositories() {
        logger.info("Binding EventSourced Aggregate Roots Repositories");
        if (classesGroup.isEmpty()) {
            Class<T> firstTypeParameterClass = ReflectionsHelper.getFirstTypeParameterClass(getClass());

            for (ClassesSearchGroup classesSearchGroup : classesSearchGroups) {
                Collection<String> packagesToScan = classesSearchGroup.getPackages();
                logger.info(String.format("Searching %s for EventSourced Aggregate Roots to bind Repositories", packagesToScan));

                Reflections reflections = createReflections(packagesToScan);
                Collection<Class<? extends T>> aggregateRoots = ReflectionsHelper.findAggregateRoots(reflections, firstTypeParameterClass);
                bindRepositories(filterSearchResult(aggregateRoots, classesSearchGroup));
            }
        } else {
            bindRepositories(classesGroup);
        }
        logger.info("Binding Cached EventSourced Aggregate Roots Repositories");
        if (cachingClasses != null && !cachingClasses.isEmpty()) {
        	bindCachingRepositories(cachingClasses);
        }
    }

    protected void bindRepositories(Iterable<Class<? extends T>> aggregateRoots) {
        for (Class<? extends T> aggregateRootClass : aggregateRoots) {
            logger.info(String.format("\tFound: [%s]", aggregateRootClass.getName()));
            bindRepository(aggregateRootClass);
        }
    }

	protected void bindCachingRepositories(
			Map<Class<? extends EventSourcedAggregateRoot<?>>, Duration> cachingClasses2) {
		for (Entry<Class<? extends EventSourcedAggregateRoot<?>>, Duration> aggregateRootClass : cachingClasses2
				.entrySet()) {
			logger.info(String.format("\tFound: [%s]", aggregateRootClass.getKey().getName()));
			bindCachingRepository(aggregateRootClass.getKey(), aggregateRootClass.getValue());
		}
	}

	protected abstract void bindRepository(Class<? extends T> aggregateRootClass);

	protected abstract void bindCachingRepository(Class<? extends EventSourcedAggregateRoot<?>> aggregateRootClass,
			Duration duration);
}