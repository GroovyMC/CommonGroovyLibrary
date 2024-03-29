package org.groovymc.cgl.api.transform.util

import groovy.transform.CompileStatic
import org.jetbrains.annotations.Nullable

@CompileStatic
class ModIdRequester {
    private static final List<Helper> HELPERS = ServiceLoader.<Helper>loadToList(Helper, ModIdRequester.classLoader)

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
