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

package io.github.groovymc.cgl.transform.registroid

import groovy.transform.CompileStatic

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Used in order to mark addon annotations. <br>
 * Annotate an annotation you want to use as {@linkplain RegistroidAddon Registroid addons} with this annotations, and specify the {@linkplain RegistroidAddonClass#value() addon class}.
 */
@Documented
@CompileStatic
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@interface RegistroidAddonClass {
    /**
     * A class implementing {@linkplain RegistroidAddon} which will be instantiated, and used as an addon by the {@linkplain io.github.groovymc.cgl.transform.Registroid Registroid system}.
     */
    Class value()
}