/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.impl.forge

import com.matyrobbrt.gml.BaseGMod
import com.matyrobbrt.gml.GMod
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import io.github.groovymc.cgl.impl.CommonGroovyLibrary
import org.jetbrains.annotations.ApiStatus

@POJO
@CompileStatic
@GMod(CommonGroovyLibrary.MOD_ID)
@ApiStatus.Internal
class CGLForge implements BaseGMod {
}