/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.impl.forge

import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import net.minecraftforge.event.AddReloadListenerEvent
import org.groovymc.cgl.impl.CommonGroovyLibrary
import org.groovymc.cgl.impl.resources.ScriptResourceListener
import org.groovymc.gml.GMod
import org.jetbrains.annotations.ApiStatus

@POJO
@CompileStatic
@GMod(CommonGroovyLibrary.MOD_ID)
@ApiStatus.Internal
class CGLForge {
    CGLForge() {
        forgeBus.addListener(AddReloadListenerEvent) { event ->
            event.addListener(new ScriptResourceListener())
        }
    }
}