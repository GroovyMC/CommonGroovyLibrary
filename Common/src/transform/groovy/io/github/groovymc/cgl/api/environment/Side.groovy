/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.environment

import groovy.transform.CompileStatic

/**
 * Represents a physical side that the game can exist on.
 */
@CompileStatic
enum Side {
    SERVER,
    CLIENT
}