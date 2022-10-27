/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.transform.util

import groovy.transform.CompileStatic
import org.jetbrains.annotations.Nullable

@CompileStatic
class ModIdRequester {
    private static final List<Helper> HELPERS = [].tap {
        ServiceLoader.load(Helper, ModIdRequester.class.classLoader).each(it.&add)
    }

    @Nullable
    static String getModId(String packageName) {
        return HELPERS.stream()
            .map {it.getModId(packageName)}
            .filter {it !== null}
            .findFirst().orElse(null)
    }

    @CompileStatic
    static interface Helper {
        @Nullable
        String getModId(String packageName)
    }
}
