package com.google.code.axonguice.commandhandling;

import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.InterceptorChain;
import org.axonframework.unitofwork.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingCommandHandlerInterceptor implements CommandHandlerInterceptor {

	final static Logger logger = LoggerFactory.getLogger(LoggingCommandHandlerInterceptor.class);

	@Override
	public Object handle(CommandMessage<?> commandMessage, UnitOfWork unitOfWork, InterceptorChain interceptorChain)
			throws Throwable {
		long st = System.currentTimeMillis();
		try {
			return interceptorChain.proceed();
		} finally {
			logger.debug("Time Spent for {}: {}ms", commandMessage.getCommandName(), (System.currentTimeMillis() - st));
		}
	}

}
