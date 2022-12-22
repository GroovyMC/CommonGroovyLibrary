/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.impl

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class CommonGroovyLibrary {
    public static final String MOD_ID = 'commongroovylibrary'
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID)
}
