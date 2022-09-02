/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.extension

import groovy.transform.CompileStatic
import net.minecraft.server.level.ServerPlayer

@CompileStatic
class GeneralExtensions {

    static boolean isOp(ServerPlayer player) {
        return player.server.playerList.isOp(player.gameProfile)
    }

}
