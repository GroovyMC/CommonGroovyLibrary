package cgltest

import groovy.transform.CompileStatic
import org.groovymc.gml.GMod

@GMod('cgltest')
@CompileStatic
class CGLTestMod {
    CGLTestMod() {
        CodecTesting.yes()
    }
}
