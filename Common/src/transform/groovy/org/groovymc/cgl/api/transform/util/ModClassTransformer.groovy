/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.api.transform.util

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.SourceUnit

@CompileStatic
class ModClassTransformer {
    private static final List<Helper> HELPERS = ServiceLoader.<Helper>loadToList(Helper, ModClassTransformer.class.classLoader)

    static void registerTransformer(String modId, Transformer transformer) {
        HELPERS.each { it.addTransformer(modId, transformer) }
    }

    @CompileStatic
    static interface Helper {
        void addTransformer(String modId, Transformer transformer)
    }

    @CompileStatic
    static interface Transformer {
        void transform(ClassNode classNode, AnnotationNode annotationNode, SourceUnit source)
    }
}
