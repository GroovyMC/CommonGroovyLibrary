/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.forge.transform

import com.matyrobbrt.gml.transform.api.ModRegistry
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.transform.util.ModIdRequester

@CompileStatic
class ForgeModIdRequester implements ModIdRequester.Helper {
    @Override
    String getModId(String packageName) {
        return ModRegistry.getData(packageName)?.modId()
    }
}
