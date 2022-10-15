package io.github.groovymc.cgl.transform.environment

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.AnnotatedNode
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
@CompileStatic
interface ExistsOnProcessor {
    void process(AnnotatedNode node, Side side, List<Platform> platforms)
}