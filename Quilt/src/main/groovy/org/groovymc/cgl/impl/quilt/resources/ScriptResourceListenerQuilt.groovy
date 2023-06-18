package org.groovymc.cgl.impl.quilt.resources

import net.minecraft.resources.ResourceLocation
import org.groovymc.cgl.impl.resources.ScriptResourceListener

class ScriptResourceListenerQuilt extends ScriptResourceListener implements IdentifiableResourceReloader {
    public static final ResourceLocation QUILT_ID = new ResourceLocation('cgl', 'load_scripts')

    @Override
    ResourceLocation getQuiltId() {
        return QUILT_ID
    }
}
