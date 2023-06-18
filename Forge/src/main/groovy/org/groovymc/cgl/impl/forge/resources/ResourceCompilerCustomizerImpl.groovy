package org.groovymc.cgl.impl.forge.resources

import com.google.auto.service.AutoService
import org.codehaus.groovy.control.CompilerConfiguration
import org.groovymc.cgl.impl.resources.ResourceCompilerCustomizer

@AutoService(ResourceCompilerCustomizer.class)
class ResourceCompilerCustomizerImpl implements ResourceCompilerCustomizer {
    @Override
    void customize(CompilerConfiguration compilerConfiguration) {
        // Nothing needs done on forge
    }
}
