/**
 * STARPOST CONFIDENTIAL
 * _____________________
 * 
 * [2014] - [2016] StarPost Supply Chain Management Co. (Shenzhen) Ltd. 
 * All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of
 * StarPost Supply Chain Management Co. (Shenzhen) Ltd. and its suppliers, if
 * any. The intellectual and technical concepts contained herein are proprietary
 * to StarPost Supply Chain Management Co. (Shenzhen) Ltd. and its suppliers and
 * may be covered by China and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from StarPost Supply Chain Management Co. (Shenzhen)
 * Ltd.
 *
 */
package com.google.code.axonguice;

import javax.inject.Singleton;

import org.axonframework.commandhandling.gateway.GatewayProxyFactory;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.SnapshotEventStore;

import com.google.code.axonguice.commandhandling.AggregateRootCommandHandlingModule;
import com.google.code.axonguice.commandhandling.CommandHandlingModule;
import com.google.code.axonguice.config.AxonConfig;
import com.google.code.axonguice.config.CustomCommandGatewayProvider;
import com.google.code.axonguice.config.GatewayProxyFactoryProvider;
import com.google.code.axonguice.config.JdbcEventStoreProvider;
import com.google.code.axonguice.domain.DomainModule;
import com.google.code.axonguice.domain.eventsourcing.EventSourcedDomainModule;
import com.google.code.axonguice.eventhandling.EventHandlingModule;
import com.google.code.axonguice.repository.RepositoryModule;
import com.google.code.axonguice.repository.eventsourcing.EventSourcedRepositoryModule;
import com.google.code.axonguice.saga.SagaModule;
import com.google.inject.Scopes;

/**
 * @author kmtong
 *
 */
public class SimpleAxonGuiceModule extends AxonGuiceModule {

	final AxonConfig config;

	public SimpleAxonGuiceModule(AxonConfig config) {
		this.config = config;
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	protected DomainModule createDomainModule() {
		return new EventSourcedDomainModule(config.getAggregateClassesAsArray());
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	protected RepositoryModule createRepositoryModule() {
		return new EventSourcedRepositoryModule(config.getAggregateClassesAsArray()) {

			@Override
			protected void bindEventStore() {
				bind(EventStore.class).toProvider(JdbcEventStoreProvider.class).in(Scopes.SINGLETON);
			}

			@Override
			protected void bindSnaphotEventStore() {
				bind(SnapshotEventStore.class).toProvider(JdbcEventStoreProvider.class).in(Scopes.SINGLETON);
			}

		};
	}

	@Override
	protected CommandHandlingModule createCommandHandlingModule() {
		return new CommandHandlingModule(config.getCommandHandlerClassesAsArray()) {
			/**
			 * 此为绑定定制化的Command Gateway
			 */
			@Override
			@SuppressWarnings({ "rawtypes", "unchecked" })
			protected void configure() {
				// 统一提供一个GatewayProxyFactory
				bind(GatewayProxyFactory.class).toProvider(GatewayProxyFactoryProvider.class).in(Singleton.class);
				for (Class<?> gatewayClass : config.getCommandGatewayClasses()) {
					CustomCommandGatewayProvider p = new CustomCommandGatewayProvider(gatewayClass);
					requestInjection(p);
					bind(gatewayClass).toProvider(p).in(Singleton.class);
				}
			}
		};
	}

	@Override
	protected AggregateRootCommandHandlingModule createAggregateRootCommandHandlingModule() {
		return new AggregateRootCommandHandlingModule(config.getAggregateClassesAsArray());
	}

	@Override
	protected EventHandlingModule createEventHandlingModule() {
		return new EventHandlingModule(config.getEventHandlerClassesAsArray());
	}

	@Override
	protected SagaModule createSagaModule() {
		return new SagaModule(config.getSagaClassesAsArray());
	}

	@Override
	protected void configure() {
		super.configure();
		bind(AxonConfig.class).toInstance(this.config);
	}

}
