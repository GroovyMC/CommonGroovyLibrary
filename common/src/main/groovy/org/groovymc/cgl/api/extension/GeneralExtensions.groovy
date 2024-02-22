package org.groovymc.cgl.api.extension

import groovy.transform.CompileStatic
import net.minecraft.server.level.ServerPlayer

/**
 * General extension methods.
 * @author CommonGroovyLibrary
 */
@CompileStatic
class GeneralExtensions {

    static boolean isOp(ServerPlayer player) {
        return player.server.playerList.isOp(player.gameProfile)
    }

}
