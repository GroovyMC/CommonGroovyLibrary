/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.impl.transform.forge

import com.matyrobbrt.gml.transform.api.ModRegistry
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.transform.util.ModIdRequester
import org.jetbrains.annotations.ApiStatus

@CompileStatic
class ForgeModIdRequester implements ModIdRequester.Helper {
    @Override
    String getModId(String packageName) {
        return ModRegistry.getData(packageName)?.modId()
    }
}
