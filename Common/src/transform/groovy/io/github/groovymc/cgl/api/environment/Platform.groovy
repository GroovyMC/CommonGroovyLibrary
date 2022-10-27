package io.github.groovymc.cgl.api.environment

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

@CompileStatic
@TupleConstructor
enum Platform {
    FORGE_CLIENT(Loader.FORGE, Side.CLIENT),
    FORGE_SERVER(Loader.FORGE, Side.SERVER),
    QUILT_CLIENT(Loader.QUILT, Side.CLIENT),
    QUILT_SERVER(Loader.QUILT, Side.SERVER)

    final Loader loader
    final Side side

    static Platform getForLoaderAndSide(Loader loader, Side side) {
        return values().find {it.loader === loader && it.side === side}
    }
}
