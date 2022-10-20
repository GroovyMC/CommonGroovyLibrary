/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.extension.brigadier

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

import java.util.function.Function
import java.util.function.Predicate

import static groovy.lang.Closure.DELEGATE_FIRST

/**
 * General extensions for brigadier.
 * @author CommonGroovyLibrary
 */
@CompileStatic
final class CommandExtensions {
    /**
     * Registers and configures brigadier under the {@code name}.
     * @param dispatcher the dispatcher to register to
     * @param name the name of the brigadier
     * @param closure a {@linkplain Closure} that configures the brigadier
     */
    static <S> void register(CommandDispatcher<S> dispatcher, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.LiteralArgumentBuilder<S>',
            strategy = DELEGATE_FIRST
    ) Closure closure) {
        final command = LiteralArgumentBuilder.<S> literal(name)
        closure.delegate = command
        closure.resolveStrategy = DELEGATE_FIRST
        closure(command)
        dispatcher.register(command)
    }

    /**
     * Creates and configures a {@linkplain LiteralArgumentBuilder}.
     * @param name the name of the brigadier
     * @param closure a {@linkplain Closure} that configures the command
     * @return the configured builder
     */
    @SuppressWarnings(['DuplicatedCode', 'unused'])
    static <S, T extends ArgumentBuilder<S, T>> LiteralArgumentBuilder<S> literal(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.LiteralArgumentBuilder<S>',
            strategy = DELEGATE_FIRST
    ) Closure closure) {
        final command = LiteralArgumentBuilder.<S> literal(name)
        closure.delegate = command
        closure.resolveStrategy = DELEGATE_FIRST
        closure(command)
        return command
    }

    /**
     * Creates and configures and appends a {@linkplain LiteralArgumentBuilder}.
     * @param name the name of the brigadier
     * @param closure a {@linkplain Closure} that configures the sub-command
     */
    static <S, T extends ArgumentBuilder<S, T>> T then(ArgumentBuilder<S, T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.LiteralArgumentBuilder<S>',
            strategy = DELEGATE_FIRST
    ) Closure closure) {
        final command = LiteralArgumentBuilder.<S> literal(name)
        closure.delegate = command
        closure.resolveStrategy = DELEGATE_FIRST
        closure(command)
        return self.then(command)
    }

    /**
     * Adds a predicate that decides if the command can be used by a source.
     * @param self the argument builder
     * @param predicate the predicate
     */
    static <S, T extends ArgumentBuilder<S, T>> T requires(ArgumentBuilder<S, T> self, @ClosureParams(
            value = FromString, options = 'S'
    ) Closure<Boolean> predicate) {
        return self.requires((Predicate<S>) ((S it) -> predicate(it)))
    }

    /**
     * Sets a function to be run when the command is executed at this node.
     * @param self the builder
     * @param closure the closure to execute when the command is invoked
     */
    static <S, T extends ArgumentBuilder<S, T>> T executes(ArgumentBuilder<S, T> self, @ClosureParams(
            value = FromString,
            options = 'com.mojang.brigadier.context.CommandContext<S>'
    ) @DelegatesTo(
            type = 'com.mojang.brigadier.context.CommandContext<S>',
            strategy = DELEGATE_FIRST
    ) Closure closure) {
        return self.executes((Command<S>) (CommandContext<S> it) -> {
            closure.delegate = it
            closure.resolveStrategy = DELEGATE_FIRST
            closure(it)
            Command.SINGLE_SUCCESS
        })
    }

    /**
     * Adds an argument to the builder.
     * @param self the builder
     * @param name the name of the argument
     * @param argumentType the type of the argument
     * @param getterFactory a factory for the {@linkplain ArgumentGetter}
     * @param closure a closure which will be invoked in order to further configure the builder
     *
     * @see ArgumentExtensions
     */
    static <S, T extends ArgumentBuilder<S, T>, Z, X> T argument(ArgumentBuilder<S, T> self, String name,
                                                                 ArgumentType<Z> argumentType, Function<String, ArgumentGetter<S, X>> getterFactory, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,Z>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.api.extension.brigadier.ArgumentGetter<S,X>'
    ) Closure closure) {
        final argument = RequiredArgumentBuilder.<S, Z> argument(name, argumentType)
        closure.delegate = argument
        closure.resolveStrategy = DELEGATE_FIRST
        closure(getterFactory.apply(name))
        self.then(argument)
    }


    /**
     * Adds an argument to the builder. <br>
     * Similar to {@linkplain #argument(com.mojang.brigadier.builder.ArgumentBuilder, java.lang.String, com.mojang.brigadier.arguments.ArgumentType, java.util.function.Function, groovy.lang.Closure)}
     * but instead uses the {@linkplain #defaultGetter()}.
     *
     * @param self the builder
     * @param name the name of the argument
     * @param argumentType the type of the argument
     * @param closure a closure which will be invoked in order to further configure the builder
     *
     * @see ArgumentExtensions
     * @see #argument(com.mojang.brigadier.builder.ArgumentBuilder, java.lang.String, com.mojang.brigadier.arguments.ArgumentType, java.util.function.Function, groovy.lang.Closure)
     */
    static <S, T extends ArgumentBuilder<S, T>, Z> T argument(ArgumentBuilder<S, T> self, String name,
                                                              ArgumentType<Z> argumentType, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.RequiredArgumentBuilder<S,Z>',
            strategy = DELEGATE_FIRST
    ) @ClosureParams(
            value = FromString,
            options = 'io.github.groovymc.cgl.api.extension.brigadier.ArgumentGetter<S,Z>'
    ) Closure closure) {
        argument(self, name, argumentType, nm -> new ArgumentGetter<S, Z>() {
            @Override
            Z call(CommandContext<S> context) {
                return (Z) context.getArgument(nm, Object)
            }
        }, closure)
    }

    /**
     * Creates an factory for ArgumentGetters that returns the same object as the argument's type.
     */
    static <A, B> Function<String, ArgumentGetter<A, B>> defaultGetter() {
        return (String nm) -> new ArgumentGetter<A, B>() {
            @Override
            B call(CommandContext<A> context) {
                return (B) context.getArgument(nm, Object)
            }
        }
    }
}
