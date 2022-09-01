package io.github.groovymc.cgl.forge

import com.matyrobbrt.gml.GModContainer
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.reg.forge.ForgeBusGetter
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModContainer

@CompileStatic
class GMLBusGetter implements ForgeBusGetter {
    @Override
    IEventBus getModEventBus(ModContainer modContainer) {
        if (modContainer instanceof GModContainer) {
            return modContainer.modBus
        }
        return null
    }
}
