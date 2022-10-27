/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.environment

import groovy.transform.CompileStatic

/**
 * Represents which loader the game is running on or a mod is compiling for.
 */
@CompileStatic
enum Loader {
    FORGE,
    QUILT
}