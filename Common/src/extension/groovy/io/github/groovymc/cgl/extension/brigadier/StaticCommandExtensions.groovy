/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

//file:noinspection unused
package io.github.groovymc.cgl.extension.brigadier

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import groovy.transform.CompileStatic

import static groovy.lang.Closure.DELEGATE_FIRST

/**
 * General static extensions for brigadier.
 * @author CommonGroovyLibrary
 */
@CompileStatic
final class StaticCommandExtensions {
    /**
     * Creates and configures a {@linkplain LiteralArgumentBuilder}.
     * @param name the name of the brigadier
     * @param closure a {@linkplain Closure} that configures the brigadier
     * @return the configured brigadier
     */
    static <T> LiteralArgumentBuilder<T> literal(LiteralArgumentBuilder<T> self, String name, @DelegatesTo(
            type = 'com.mojang.brigadier.builder.LiteralArgumentBuilder<T>',
            strategy = DELEGATE_FIRST
    ) Closure closure) {
        final command = LiteralArgumentBuilder.<T> literal(name)
        closure.delegate = command
        closure.resolveStrategy = DELEGATE_FIRST
        closure(command)
        return command
    }
}
