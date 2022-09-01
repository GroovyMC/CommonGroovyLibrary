/*
 * Copyright (C) 2022 GroovyMC
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package io.github.groovymc.cgl.forge

import com.matyrobbrt.gml.BaseGMod
import com.matyrobbrt.gml.GMod
import groovy.transform.CompileStatic
import groovy.transform.stc.POJO
import io.github.groovymc.cgl.CommonGroovyLibrary

@POJO
@CompileStatic
@GMod(CommonGroovyLibrary.MOD_ID)
class CGLForge implements BaseGMod {
}