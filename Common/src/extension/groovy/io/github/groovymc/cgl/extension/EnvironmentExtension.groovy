package io.github.groovymc.cgl.extension

import groovy.transform.CompileStatic

@CompileStatic
@interface EnvironmentExtension {
    enum Side {
        SERVER, CLIENT
    }
    Side value()
}