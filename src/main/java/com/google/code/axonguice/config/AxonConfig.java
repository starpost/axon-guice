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

import javax.inject.Provider;

import org.axonframework.eventsourcing.EventSourcedAggregateRoot;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.SnapshotEventStore;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;

import com.google.common.collect.FluentIterable;
import com.thoughtworks.xstream.converters.Converter;

/**
 * @author kmtong
 *
 */
public class AxonConfig {

	final Set<Class<? extends EventSourcedAggregateRoot<?>>> aggregateClasses;
	final Map<Class<? extends EventSourcedAggregateRoot<?>>, Duration> aggregateCachingClassesMap;
	final Set<Class<?>> commandHandlerClasses;
	final Set<Class<?>> eventHandlerClasses;
	final Set<Class<? extends AbstractAnnotatedSaga>> sagaClasses;
	final Set<Class<?>> commandGatewayClasses;
	final Set<Class<? extends Converter>> converters;
	final Class<? extends Provider<? extends EventStore>> eventStoreProviderClass;
	final Class<? extends Provider<? extends SnapshotEventStore>> snapshotEventStoreProviderClass;
	final boolean asyncSagaManager;
	final int processorCount;
	final boolean useDisruptorCommandBus;

	public AxonConfig(Set<Class<? extends EventSourcedAggregateRoot<?>>> aggregateClasses,
			Map<Class<? extends EventSourcedAggregateRoot<?>>, Duration> aggregateCachingClassesMap,
			Set<Class<?>> commandHandlerClasses, //
			Set<Class<?>> eventHandlerClasses, //
			Set<Class<? extends AbstractAnnotatedSaga>> sagaClasses, //
			Set<Class<?>> commandGatewayClasses, //
			Class<? extends Provider<? extends EventStore>> eventStoreProviderClass, //
			Class<? extends Provider<? extends SnapshotEventStore>> snapshotEventStoreProviderClass, //
			Set<Class<? extends Converter>> converters, boolean asyncSagaManager, int processorCount,
			boolean useDisruptorCommandBus) {
		super();
		this.aggregateClasses = aggregateClasses;
		this.aggregateCachingClassesMap = aggregateCachingClassesMap;
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
		this.asyncSagaManager = asyncSagaManager;
		this.processorCount = processorCount;
		this.useDisruptorCommandBus = useDisruptorCommandBus;
	}

	public Set<Class<? extends EventSourcedAggregateRoot<?>>> getAggregateClasses() {
		return aggregateClasses;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends EventSourcedAggregateRoot<?>>[] getAggregateClassesAsArray() {
		return FluentIterable.from(aggregateClasses).toList().toArray(new Class[0]);
	}

	public Map<Class<? extends EventSourcedAggregateRoot<?>>, Duration> getAggregateCachingClasses() {
		return aggregateCachingClassesMap;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends EventSourcedAggregateRoot<?>>[] getAggregateCachingClassesAsArray() {
		return FluentIterable.from(aggregateCachingClassesMap.keySet()).toList().toArray(new Class[0]);
	}

	/**
	 * Caching and non-caching aggregate roots are returned
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends EventSourcedAggregateRoot<?>>[] getAllAggregateClassesAsArray() {
		return FluentIterable.from(aggregateClasses).append(aggregateCachingClassesMap.keySet()).toList()
				.toArray(new Class[0]);
	}

	public Duration getCachePeriod(Class<? extends EventSourcedAggregateRoot<?>> cls) {
		return aggregateCachingClassesMap.get(cls);
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

	public Class<? extends Provider<? extends EventStore>> getEventStorePoviderClass() {
		return eventStoreProviderClass;
	}

	public Class<? extends Provider<? extends SnapshotEventStore>> getSnapshotEventStorePoviderClass() {
		return snapshotEventStoreProviderClass;
	}

	public boolean isAsyncSagaManager() {
		return asyncSagaManager;
	}

	public int getProcessorCount() {
		return processorCount;
	}

	public boolean isUseDisruptorCommandBus() {
		return useDisruptorCommandBus;
	}

}
