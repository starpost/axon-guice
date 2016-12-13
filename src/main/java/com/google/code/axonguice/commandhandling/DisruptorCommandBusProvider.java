package com.google.code.axonguice.commandhandling;

import javax.inject.Inject;
import javax.inject.Provider;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.disruptor.DisruptorCommandBus;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventstore.EventStore;

public class DisruptorCommandBusProvider implements Provider<CommandBus> {

	@Inject
	EventBus eventBus;

	@Inject
	EventStore eventStore;

	@Override
	public CommandBus get() {
		DisruptorCommandBus disruptor = new DisruptorCommandBus(eventStore, eventBus);
		return disruptor;
	}

}
