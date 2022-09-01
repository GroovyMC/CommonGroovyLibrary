/*
 * Copyright (C) 2022 GroovyMC
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package io.github.groovymc.cgl.transform.util

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.SourceUnit

@CompileStatic
class ModClassTransformer {
    private static final List<Helper> HELPERS = [].tap {
        ServiceLoader.load(Helper, ModClassTransformer.class.classLoader).each(it.&add)
    }

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
