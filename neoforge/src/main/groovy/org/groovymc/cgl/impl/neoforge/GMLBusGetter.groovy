package org.groovymc.cgl.impl.neoforge

import com.google.auto.service.AutoService
import groovy.transform.CompileStatic
import net.neoforged.bus.api.IEventBus
import org.groovymc.cgl.reg.neoforge.NeoForgeBusGetter
import net.neoforged.fml.ModContainer
import org.groovymc.gml.GModContainer
import org.jetbrains.annotations.ApiStatus

@CompileStatic
@ApiStatus.Internal
@AutoService(NeoForgeBusGetter.class)
@SuppressWarnings('unused')
class GMLBusGetter implements NeoForgeBusGetter {
    @Override
    IEventBus getModEventBus(ModContainer modContainer) {
        if (modContainer instanceof GModContainer) {
            return modContainer.modBus
        }
        null
    }
}
