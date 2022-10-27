/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.environment

import groovy.transform.CompileStatic

/**
 * A utility for getting information about the current game environment, including loader, physical side, and whether
 * the current environment is a development environment.
 */
@CompileStatic
interface GameEnvironment {
    public static final GameEnvironment INSTANCE = ServiceLoader.load(GameEnvironment)
            .findFirst()
            .orElseThrow({ -> new NullPointerException("Failed to load service for ${GameEnvironment.name}") })

    /**
     * Gets the current loader.
     */
    Loader getLoader()

    /**
     * Gets the current physical side.
     */
    Side getSide()

    /**
     * Returns true if the game is running in production, or false if it is running in a development environment.
     */
    boolean isProduction()

    default Platform getPlatform() {
        return Platform.getForLoaderAndSide(loader, side)
    }
}