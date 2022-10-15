/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.environment

import groovy.transform.CompileStatic

@CompileStatic
interface Environment {
    public static final Environment INSTANCE = ServiceLoader.load(Environment)
            .findFirst()
            .orElseThrow({ -> new NullPointerException("Failed to load service for ${Environment.name}") })

    Platform getPlatform()
    Side getSide()
    boolean isProduction()
}