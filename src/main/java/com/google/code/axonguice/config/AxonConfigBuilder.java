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

import java.util.Set;

import org.axonframework.eventsourcing.EventSourcedAggregateRoot;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.SnapshotEventStore;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;

import com.google.common.collect.Sets;
import com.google.inject.Provider;
import com.thoughtworks.xstream.converters.Converter;

/**
 * @author kmtong
 *
 */
public class AxonConfigBuilder {

	Set<Class<? extends EventSourcedAggregateRoot<?>>> aggregateClasses = Sets.newHashSet();
	Set<Class<?>> commandHandlerClasses = Sets.newHashSet();
	Set<Class<?>> eventHandlerClasses = Sets.newHashSet();
	Set<Class<? extends AbstractAnnotatedSaga>> sagaClasses = Sets.newHashSet();
	Set<Class<?>> commandGatewayClasses = Sets.newHashSet();
	Set<Class<? extends Converter>> converterClasses = Sets.newHashSet();
	Class<Provider<? extends EventStore>> eventStoreProviderClass;
	Class<Provider<? extends SnapshotEventStore>> snapshotEventStoreProviderClass;

	public AxonConfigBuilder withAggregate(Class<? extends EventSourcedAggregateRoot<?>> aggregateClass) {
		aggregateClasses.add(aggregateClass);
		return this;
	}

	public AxonConfigBuilder withAggregates(Set<Class<? extends EventSourcedAggregateRoot<?>>> aggregateClasses) {
		aggregateClasses.addAll(aggregateClasses);
		return this;
	}

	public AxonConfigBuilder withSaga(Class<? extends AbstractAnnotatedSaga> sagaClass) {
		sagaClasses.add(sagaClass);
		return this;
	}

	public AxonConfigBuilder withSagas(Set<Class<? extends AbstractAnnotatedSaga>> sagaClass) {
		sagaClasses.addAll(sagaClass);
		return this;
	}

	public AxonConfigBuilder withCommandHandler(Class<?> commandHandler) {
		commandHandlerClasses.add(commandHandler);
		return this;
	}

	public AxonConfigBuilder withCommandHandlers(Set<Class<?>> commandHandlers) {
		commandHandlerClasses.addAll(commandHandlers);
		return this;
	}

	public AxonConfigBuilder withEventHandler(Class<?> eventHandler) {
		eventHandlerClasses.add(eventHandler);
		return this;
	}

	public AxonConfigBuilder withEventHandlers(Set<Class<?>> eventHandlers) {
		eventHandlerClasses.addAll(eventHandlers);
		return this;
	}

	public AxonConfigBuilder withCommandGateway(Class<?> commandGateway) {
		commandGatewayClasses.add(commandGateway);
		return this;
	}

	public AxonConfigBuilder withCommandGateways(Set<Class<?>> commandGateways) {
		commandGatewayClasses.addAll(commandGateways);
		return this;
	}

	public AxonConfigBuilder withXStreamConverter(Class<? extends Converter> converter) {
		converterClasses.add(converter);
		return this;
	}

	public AxonConfigBuilder withEventStoreProvider(Class<Provider<? extends EventStore>> eventStoreProviderClass) {
		this.eventStoreProviderClass = eventStoreProviderClass;
		return this;
	}

	public AxonConfigBuilder withSnapshotEventStoreProvider(
			Class<Provider<? extends SnapshotEventStore>> snapshotEventStoreProviderClass) {
		this.snapshotEventStoreProviderClass = snapshotEventStoreProviderClass;
		return this;
	}

	public AxonConfig build() {
		return new AxonConfig(aggregateClasses, commandHandlerClasses, eventHandlerClasses, sagaClasses,
				commandGatewayClasses, eventStoreProviderClass, snapshotEventStoreProviderClass, converterClasses);
	}

	public static AxonConfigBuilder create() {
		return new AxonConfigBuilder();
	}

}
