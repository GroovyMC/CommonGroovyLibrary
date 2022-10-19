/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.impl.transform.forge

import com.matyrobbrt.gml.transform.gmods.GModASTTransformer
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.api.transform.util.ModClassTransformer

@CompileStatic
class ForgeModClassTransformer implements ModClassTransformer.Helper {
    @Override
    void addTransformer(String modId, ModClassTransformer.Transformer transformer) {
        GModASTTransformer.registerTransformer(modId) { clazz, annotation, source ->
            transformer.transform(clazz, annotation, source)
        }
    }
}
