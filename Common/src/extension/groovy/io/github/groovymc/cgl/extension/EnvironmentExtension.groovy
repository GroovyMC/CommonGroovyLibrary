/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.extension

import groovy.transform.CompileStatic

@CompileStatic
@interface EnvironmentExtension {
    enum Side {
        SERVER, CLIENT
    }
    Side value()
}