package com.google.code.axonguice.commandhandling;

import java.util.Collection;

import org.axonframework.commandhandling.CommandBus;

import com.google.code.axonguice.grouping.ClassesSearchGroup;
import com.google.inject.Scopes;

public class DisruptorCommandBusCommandHandlingModule extends CommandHandlingModule {

	public DisruptorCommandBusCommandHandlingModule(Class<?>... commandHandlersClasses) {
		super(commandHandlersClasses);
	}

	public DisruptorCommandBusCommandHandlingModule(Collection<ClassesSearchGroup> commandHandlersClassesSearchGroup) {
		super(commandHandlersClassesSearchGroup);
	}

	public DisruptorCommandBusCommandHandlingModule(String... commandHandlersScanPackages) {
		super(commandHandlersScanPackages);
	}

	@Override
	protected void bindCommandBus() {
		bind(CommandBus.class).toProvider(DisruptorCommandBusProvider.class).in(Scopes.SINGLETON);
	}

}
