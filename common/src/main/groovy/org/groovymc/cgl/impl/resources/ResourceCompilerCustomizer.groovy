package org.groovymc.cgl.impl.resources

import org.codehaus.groovy.control.CompilerConfiguration

interface ResourceCompilerCustomizer {
    void customize(CompilerConfiguration compilerConfiguration)
}