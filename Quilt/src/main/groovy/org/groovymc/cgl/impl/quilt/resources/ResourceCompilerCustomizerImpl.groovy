package org.groovymc.cgl.impl.quilt.resources

import com.google.auto.service.AutoService
import org.codehaus.groovy.control.CompilerConfiguration
import org.groovymc.cgl.impl.resources.ResourceCompilerCustomizer
import org.groovymc.groovyduvet.core.api.RemappingCustomizer

@AutoService(ResourceCompilerCustomizer.class)
class ResourceCompilerCustomizerImpl implements ResourceCompilerCustomizer {
    @Override
    void customize(CompilerConfiguration compilerConfiguration) {
        compilerConfiguration.addCompilationCustomizers(new RemappingCustomizer())
    }
}
