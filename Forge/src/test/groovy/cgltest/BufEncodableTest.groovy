package cgltest

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import io.github.groovymc.cgl.transform.fbb.BufferEncodable

@CompileStatic
@TupleConstructor
@BufferEncodable
class BufEncodableTest {
    int daInt
    boolean someBool
    String theString

    List<Double> theDoubles
}
