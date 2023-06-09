/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.api.transform.registroid

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
     * A class implementing {@linkplain RegistroidAddon} which will be instantiated, and used as an addon by the {@linkplain Registroid Registroid system}.
     */
    Class value()
}