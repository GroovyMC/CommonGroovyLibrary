package org.groovymc.cgl.impl.neoforge.transform

import com.google.auto.service.AutoService
import groovy.transform.CompileStatic
import org.groovymc.cgl.api.transform.util.ModIdRequester
import org.groovymc.gml.transform.api.ModRegistry

@CompileStatic
@AutoService(ModIdRequester.Helper.class)
@SuppressWarnings('unused')
class NeoForgeModIdRequester implements ModIdRequester.Helper {
    @Override
    String getModId(String packageName) {
        return ModRegistry.getData(packageName)?.modId()
    }
}
