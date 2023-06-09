/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package org.groovymc.cgl.impl.forge

import com.google.auto.service.AutoService
import com.matyrobbrt.gml.GModContainer
import groovy.transform.CompileStatic
import org.groovymc.cgl.reg.forge.ForgeBusGetter
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModContainer
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
