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

package com.google.code.axonguice.commandhandling.annotation;

import com.google.code.axonguice.commandhandling.CommandHandlingModule;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerAdapter;
import org.axonframework.commandhandling.annotation.CommandHandler;

import java.lang.annotation.*;
import java.util.Collection;

/**
 * Specifies that the class is an containter of {@link CommandHandler}
 * annotated methods. This annotation is mandatory for handlers auto-discovery option -
 * when you use {@link CommandHandlingModule#CommandHandlingModule(String...)} or {@link
 * CommandHandlingModule#CommandHandlingModule(Collection)}.
 *
 * @author Alexey Krylov
 * @see CommandHandlingModule
 * @see AnnotationCommandHandlerAdapter
 * @since 07.02.13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandHandlerComponent {
}
