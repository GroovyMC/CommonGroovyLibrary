/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.impl.transform.forge

import com.google.auto.service.AutoService
import com.matyrobbrt.gml.transform.api.ModRegistry
import groovy.transform.CompileStatic
import org.groovymc.cgl.api.transform.util.ModIdRequester

@CompileStatic
@AutoService(ModIdRequester.Helper.class)
@SuppressWarnings('unused')
class ForgeModIdRequester implements ModIdRequester.Helper {
    @Override
    String getModId(String packageName) {
        return ModRegistry.getData(packageName)?.modId()
    }
}
