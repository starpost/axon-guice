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

import com.google.common.collect.FluentIterable;
import com.google.inject.Provider;
import com.thoughtworks.xstream.converters.Converter;

/**
 * @author kmtong
 *
 */
public class AxonConfig {

	final Set<Class<? extends EventSourcedAggregateRoot<?>>> aggregateClasses;
	final Set<Class<?>> commandHandlerClasses;
	final Set<Class<?>> eventHandlerClasses;
	final Set<Class<? extends AbstractAnnotatedSaga>> sagaClasses;
	final Set<Class<?>> commandGatewayClasses;
	final Set<Class<? extends Converter>> converters;
	final Class<Provider<? extends EventStore>> eventStoreProviderClass;
	final Class<Provider<? extends SnapshotEventStore>> snapshotEventStoreProviderClass;

	public AxonConfig(Set<Class<? extends EventSourcedAggregateRoot<?>>> aggregateClasses,
			Set<Class<?>> commandHandlerClasses, //
			Set<Class<?>> eventHandlerClasses, //
			Set<Class<? extends AbstractAnnotatedSaga>> sagaClasses, //
			Set<Class<?>> commandGatewayClasses, //
			Class<Provider<? extends EventStore>> eventStoreProviderClass, //
			Class<Provider<? extends SnapshotEventStore>> snapshotEventStoreProviderClass, //
			Set<Class<? extends Converter>> converters) {
		super();
		this.aggregateClasses = aggregateClasses;
		this.commandHandlerClasses = commandHandlerClasses;
		this.eventHandlerClasses = eventHandlerClasses;
		this.sagaClasses = sagaClasses;
		this.commandGatewayClasses = commandGatewayClasses;
		if (eventStoreProviderClass == null) {
			throw new IllegalArgumentException("EventStore Provider Class not Configured");
		}
		if (snapshotEventStoreProviderClass == null) {
			throw new IllegalArgumentException("Snapshot EventStore Provider Class not Configured");
		}
		this.eventStoreProviderClass = eventStoreProviderClass;
		this.snapshotEventStoreProviderClass = snapshotEventStoreProviderClass;
		this.converters = converters;
	}

	public Set<Class<? extends EventSourcedAggregateRoot<?>>> getAggregateClasses() {
		return aggregateClasses;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends EventSourcedAggregateRoot<?>>[] getAggregateClassesAsArray() {
		return FluentIterable.from(aggregateClasses).toList().toArray(new Class[0]);
	}

	public Set<Class<?>> getCommandHandlerClasses() {
		return commandHandlerClasses;
	}

	public Class<?>[] getCommandHandlerClassesAsArray() {
		return FluentIterable.from(commandHandlerClasses).toList().toArray(new Class[0]);
	}

	public Set<Class<?>> getEventHandlerClasses() {
		return eventHandlerClasses;
	}

	public Class<?>[] getEventHandlerClassesAsArray() {
		return FluentIterable.from(eventHandlerClasses).toList().toArray(new Class[0]);
	}

	public Set<Class<? extends AbstractAnnotatedSaga>> getSagaClasses() {
		return sagaClasses;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends AbstractAnnotatedSaga>[] getSagaClassesAsArray() {
		return FluentIterable.from(sagaClasses).toList().toArray(new Class[0]);
	}

	public Set<Class<?>> getCommandGatewayClasses() {
		return commandGatewayClasses;
	}

	public Class<?>[] getCommandGatewayClassesAsArray() {
		return FluentIterable.from(commandGatewayClasses).toList().toArray(new Class[0]);
	}

	public Set<Class<? extends Converter>> getEventSerializerConverterClasses() {
		return converters;
	}

	public Class<Provider<? extends EventStore>> getEventStorePoviderClass() {
		return eventStoreProviderClass;
	}

	public Class<Provider<? extends SnapshotEventStore>> getSnapshotEventStorePoviderClass() {
		return snapshotEventStoreProviderClass;
	}

}