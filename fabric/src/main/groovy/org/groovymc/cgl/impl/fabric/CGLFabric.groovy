package org.groovymc.cgl.impl.fabric

import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.server.packs.PackType
import org.groovymc.cgl.impl.CommonGroovyLibrary
import org.groovymc.cgl.impl.fabric.resources.ScriptResourceListenerFabric

CommonGroovyLibrary.LOGGER.info('CGL Fabric initialised')

ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ScriptResourceListenerFabric())
