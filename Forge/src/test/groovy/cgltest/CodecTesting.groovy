/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package cgltest

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import io.github.groovymc.cgl.api.transform.codec.CodecSerializable

@CompileStatic
@CodecSerializable(property = "CODEC_GOES_HERE")
@TupleConstructor
class CodecTesting {
    String myProperty
    static void yes() {
        CodecTesting.CODEC_GOES_HERE
    }
}
