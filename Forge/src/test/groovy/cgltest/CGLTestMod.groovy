/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package cgltest

import groovy.transform.CompileStatic
import org.groovymc.gml.GMod

@GMod('cgltest')
@CompileStatic
class CGLTestMod {
    CGLTestMod() {
        CodecTesting.yes()
    }
}
