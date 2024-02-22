package org.groovymc.cgl.impl.fabric.resources

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.ResourceLocation
import org.groovymc.cgl.impl.resources.ScriptResourceListener

class ScriptResourceListenerFabric extends ScriptResourceListener implements IdentifiableResourceReloadListener {
    public static final ResourceLocation FABRIC_ID = new ResourceLocation('cgl', 'load_scripts')

    @Override
    ResourceLocation getFabricId() {
        return FABRIC_ID
    }
}
