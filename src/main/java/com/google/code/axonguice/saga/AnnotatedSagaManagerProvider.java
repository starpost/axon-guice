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

package com.google.code.axonguice.saga;

import java.util.Collection;

import javax.inject.Inject;

import org.axonframework.eventhandling.EventBus;
import org.axonframework.saga.SagaFactory;
import org.axonframework.saga.SagaManager;
import org.axonframework.saga.SagaRepository;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.AnnotatedSagaManager;
import org.axonframework.saga.annotation.AsyncAnnotatedSagaManager;

import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * Provides {@link AnnotatedSagaManager} as {@link SagaManager} implementation.
 *
 * @author Alexey Krylov
 * @see SagaModule#bindSagaManager()
 * @since 14.02.13
 */
public class AnnotatedSagaManagerProvider implements Provider<SagaManager> {

	/*===========================================[ INSTANCE VARIABLES ]===========*/

    protected Injector injector;
    protected SagaRepository sagaRepository;
    protected EventBus eventBus;
    protected SagaFactory sagaFactory;
    protected Class<? extends AbstractAnnotatedSaga>[] sagaClasses;
    protected boolean async = false;
    protected int processorCount = 10;
    
	/*===========================================[ CONSTRUCTORS ]=================*/

    public AnnotatedSagaManagerProvider(Collection<Class<? extends AbstractAnnotatedSaga>> sagaClasses) {
        this(false, 1, sagaClasses);
    }

	public AnnotatedSagaManagerProvider(boolean async, int processorCount,
			Collection<Class<? extends AbstractAnnotatedSaga>> sagaClasses) {
		this.sagaClasses = sagaClasses.toArray(new Class[sagaClasses.size()]);
		this.async = async;
		this.processorCount = processorCount;
	}

    @Inject
    void init(Injector injector, SagaRepository sagaRepository, EventBus eventBus, SagaFactory sagaFactory) {
        this.injector = injector;
        this.sagaRepository = sagaRepository;
        this.eventBus = eventBus;
        this.sagaFactory = sagaFactory;
    }

	/*===========================================[ INTERFACE METHODS ]============*/

    @Override
	public SagaManager get() {
		SagaManager annotatedSagaManager = createSagaManager();
		// support for SagaManager @PostConstruct
		injector.injectMembers(annotatedSagaManager);
		eventBus.subscribe(annotatedSagaManager);
		return annotatedSagaManager;
	}

	private SagaManager createSagaManager() {
		if (async) {
			AsyncAnnotatedSagaManager annotatedSagaManager = new AsyncAnnotatedSagaManager(sagaClasses);
			annotatedSagaManager.setSagaRepository(sagaRepository);
			annotatedSagaManager.setSagaFactory(sagaFactory);
			annotatedSagaManager.setProcessorCount(processorCount);
			annotatedSagaManager.start();
			return annotatedSagaManager;
		} else {
			return new AnnotatedSagaManager(sagaRepository, sagaFactory, sagaClasses);
		}
	}
}