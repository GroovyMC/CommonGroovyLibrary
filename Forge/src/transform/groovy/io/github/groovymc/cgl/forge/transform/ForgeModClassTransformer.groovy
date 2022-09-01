package io.github.groovymc.cgl.forge.transform

import com.matyrobbrt.gml.transform.gmods.GModASTTransformer
import groovy.transform.CompileStatic
import io.github.groovymc.cgl.transform.util.ModClassTransformer

@CompileStatic
class ForgeModClassTransformer implements ModClassTransformer.Helper {
    @Override
    void addTransformer(String modId, ModClassTransformer.Transformer transformer) {
        GModASTTransformer.registerTransformer(modId) { clazz, annotation, source ->
            transformer.transform(clazz, annotation, source)
        }
    }
}
