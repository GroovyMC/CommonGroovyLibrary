package org.groovymc.cgl.impl.neoforge

import com.google.auto.service.AutoService
import groovy.transform.CompileStatic
import org.groovymc.cgl.reg.forge.ForgeBusGetter
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModContainer
import org.groovymc.gml.GModContainer
import org.jetbrains.annotations.ApiStatus

@CompileStatic
@ApiStatus.Internal
@AutoService(ForgeBusGetter.class)
@SuppressWarnings('unused')
class GMLBusGetter implements ForgeBusGetter {
    @Override
    IEventBus getModEventBus(ModContainer modContainer) {
        if (modContainer instanceof GModContainer) {
            return modContainer.modBus
        }
        null
    }
}
