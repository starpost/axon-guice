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
package com.google.code.axonguice.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

import org.axonframework.eventsourcing.EventSourcedAggregateRoot;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.SnapshotEventStore;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.converters.Converter;

/**
 * @author kmtong
 *
 */
public class AxonConfigBuilder {

	Set<Class<? extends EventSourcedAggregateRoot<?>>> aggregateClasses = Sets.newHashSet();
	Map<Class<? extends EventSourcedAggregateRoot<?>>, Duration> aggregateCachingClassesMap = Maps.newHashMap();
	Set<Class<?>> commandHandlerClasses = Sets.newHashSet();
	Set<Class<?>> eventHandlerClasses = Sets.newHashSet();
	Set<Class<? extends AbstractAnnotatedSaga>> sagaClasses = Sets.newHashSet();
	Set<Class<?>> commandGatewayClasses = Sets.newHashSet();
	Set<Class<? extends Converter>> converterClasses = Sets.newHashSet();
	Class<? extends Provider<? extends EventStore>> eventStoreProviderClass;
	Class<? extends Provider<? extends SnapshotEventStore>> snapshotEventStoreProviderClass;

	boolean asyncSagaManager = false;
	int processorCount = 1;
	boolean useDisruptor = false;

	public AxonConfigBuilder withAggregate(Class<? extends EventSourcedAggregateRoot<?>> aggregateClass) {
		withAggregate(aggregateClass, null, null);
		return this;
	}

	public AxonConfigBuilder withAggregate(Class<? extends EventSourcedAggregateRoot<?>> aggregateClass, Long ttl,
			TimeUnit unit) {
		if (ttl != null) {
			this.aggregateCachingClassesMap.put(aggregateClass, new Duration(ttl, unit));
		} else {
			this.aggregateClasses.add(aggregateClass);
		}
		return this;
	}

	public AxonConfigBuilder withAggregates(Set<Class<? extends EventSourcedAggregateRoot<?>>> aggregateClasses) {
		this.aggregateClasses.addAll(aggregateClasses);
		return this;
	}

	public AxonConfigBuilder withSaga(Class<? extends AbstractAnnotatedSaga> sagaClass) {
		this.sagaClasses.add(sagaClass);
		return this;
	}

	public AxonConfigBuilder withSagas(Set<Class<? extends AbstractAnnotatedSaga>> sagaClass) {
		this.sagaClasses.addAll(sagaClass);
		return this;
	}

	public AxonConfigBuilder withCommandHandler(Class<?> commandHandler) {
		this.commandHandlerClasses.add(commandHandler);
		return this;
	}

	public AxonConfigBuilder withCommandHandlers(Set<Class<?>> commandHandlers) {
		this.commandHandlerClasses.addAll(commandHandlers);
		return this;
	}

	public AxonConfigBuilder withEventHandler(Class<?> eventHandler) {
		this.eventHandlerClasses.add(eventHandler);
		return this;
	}

	public AxonConfigBuilder withEventHandlers(Set<Class<?>> eventHandlers) {
		this.eventHandlerClasses.addAll(eventHandlers);
		return this;
	}

	public AxonConfigBuilder withCommandGateway(Class<?> commandGateway) {
		this.commandGatewayClasses.add(commandGateway);
		return this;
	}

	public AxonConfigBuilder withCommandGateways(Set<Class<?>> commandGateways) {
		this.commandGatewayClasses.addAll(commandGateways);
		return this;
	}

	public AxonConfigBuilder withXStreamConverter(Class<? extends Converter> converter) {
		this.converterClasses.add(converter);
		return this;
	}

	public AxonConfigBuilder withEventStoreProvider(
			Class<? extends Provider<? extends EventStore>> eventStoreProviderClass) {
		this.eventStoreProviderClass = eventStoreProviderClass;
		return this;
	}

	public AxonConfigBuilder withSnapshotEventStoreProvider(
			Class<? extends Provider<? extends SnapshotEventStore>> snapshotEventStoreProviderClass) {
		this.snapshotEventStoreProviderClass = snapshotEventStoreProviderClass;
		return this;
	}

	public AxonConfigBuilder asyncSagaManager(boolean async) {
		this.asyncSagaManager = async;
		return this;
	}

	public AxonConfigBuilder processorCount(int processorCount) {
		this.processorCount = processorCount;
		return this;
	}

	public AxonConfigBuilder useDisruptorCommandBus(boolean use) {
		this.useDisruptor = use;
		return this;
	}

	public AxonConfig build() {
		return new AxonConfig(aggregateClasses, aggregateCachingClassesMap, commandHandlerClasses, eventHandlerClasses,
				sagaClasses, commandGatewayClasses, eventStoreProviderClass, snapshotEventStoreProviderClass,
				converterClasses, asyncSagaManager, processorCount, useDisruptor);
	}

	public static AxonConfigBuilder create() {
		return new AxonConfigBuilder();
	}

}
