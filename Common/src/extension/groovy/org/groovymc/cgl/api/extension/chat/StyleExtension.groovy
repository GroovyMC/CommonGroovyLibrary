/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

//file:noinspection unused

package org.groovymc.cgl.api.extension.chat

import groovy.transform.AutoFinal
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.groovymc.cgl.api.chat.StyleBuilder
import net.minecraft.network.chat.Style

/**
 * Utility methods for creating and configuring {@linkplain Style Styles}.
 * @author CommonGroovyLibrary
 */
@AutoFinal
@CompileStatic
class StyleExtension {
    static Style of(Style type,
                    @DelegatesTo(value = StyleBuilder, strategy = Closure.DELEGATE_FIRST)
                    @ClosureParams(value = SimpleType, options = 'org.groovymc.cgl.api.chat.StyleBuilder')
                    Closure closure) {
        final builder = new StyleBuilder()
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call(builder)
        return builder.style
    }
}
