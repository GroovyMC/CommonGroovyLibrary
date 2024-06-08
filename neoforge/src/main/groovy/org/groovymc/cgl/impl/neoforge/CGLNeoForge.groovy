package org.groovymc.cgl.impl.neoforge

import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import net.neoforged.neoforge.event.AddReloadListenerEvent
import org.groovymc.cgl.impl.CommonGroovyLibrary
import org.groovymc.cgl.impl.resources.ScriptResourceListener
import org.groovymc.gml.GMod
import org.jetbrains.annotations.ApiStatus

@POJO
@CompileStatic
@GMod(CommonGroovyLibrary.MOD_ID)
@ApiStatus.Internal
class CGLNeoForge {
    CGLNeoForge() {
        CommonGroovyLibrary.LOGGER.info('CGL NeoForge initialised')
        gameBus.addListener(AddReloadListenerEvent) { event ->
            event.addListener(new ScriptResourceListener())
        }
    }
}
