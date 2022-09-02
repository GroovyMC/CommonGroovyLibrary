/*
 * Copyright (C) 2022 GroovyMC
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
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
