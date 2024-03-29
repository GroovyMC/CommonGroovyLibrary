package org.groovymc.cgl.api.extension

import groovy.transform.CompileStatic

/**
 * Used to mark an extension that should exist on only one side.
 * @author CommonGroovyLibrary
 */
@CompileStatic
@interface EnvironmentExtension {
    enum Side {
        SERVER, CLIENT
    }

    Side value()
}
