/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.api.extension.chat

import groovy.transform.CompileStatic
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

/**
 * Extensions for modifying {@linkplain Component Components}.
 * @author CommonGroovyLibrary
 */
@CompileStatic
class ComponentExtension {
    static MutableComponent plus(MutableComponent self, Component component) {
        return self.append(component)
    }

    static MutableComponent plus(MutableComponent self, String component) {
        return self.append(component)
    }

    static MutableComponent leftShift(MutableComponent self, Style style) {
        self.withStyle(style)
        return self
    }

    static MutableComponent leftShift(MutableComponent self, ChatFormatting style) {
        self.withStyle(style)
        return self
    }

    static MutableComponent leftShift(MutableComponent self, List<ChatFormatting> style) {
        self.withStyle(style.<ChatFormatting> toArray(new ChatFormatting[0]))
        return self
    }
}
