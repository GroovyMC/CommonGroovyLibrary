package org.groovymc.cgl.impl.fabric.resources

import groovy.transform.CompileStatic
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.ResourceLocation
import org.groovymc.cgl.impl.resources.ScriptResourceListener

@CompileStatic
class ScriptResourceListenerFabric extends ScriptResourceListener implements IdentifiableResourceReloadListener {
    public static final ResourceLocation FABRIC_ID = ResourceLocation.fromNamespaceAndPath('cgl', 'load_scripts')

    @Override
    ResourceLocation getFabricId() {
        return FABRIC_ID
    }
}
