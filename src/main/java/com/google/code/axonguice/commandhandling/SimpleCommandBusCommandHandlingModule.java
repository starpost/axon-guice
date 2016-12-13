package com.google.code.axonguice.commandhandling;

import java.util.Collection;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;

import com.google.code.axonguice.grouping.ClassesSearchGroup;
import com.google.inject.Scopes;

public class SimpleCommandBusCommandHandlingModule extends CommandHandlingModule {

	public SimpleCommandBusCommandHandlingModule(Class<?>... commandHandlersClasses) {
		super(commandHandlersClasses);
	}

	public SimpleCommandBusCommandHandlingModule(Collection<ClassesSearchGroup> commandHandlersClassesSearchGroup) {
		super(commandHandlersClassesSearchGroup);
	}

	public SimpleCommandBusCommandHandlingModule(String... commandHandlersScanPackages) {
		super(commandHandlersScanPackages);
	}

	@Override
	protected void bindCommandBus() {
		bind(CommandBus.class).to(SimpleCommandBus.class).in(Scopes.SINGLETON);
	}

}
