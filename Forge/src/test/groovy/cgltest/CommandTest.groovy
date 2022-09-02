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

package cgltest

import com.matyrobbrt.gml.bus.EventBusSubscriber
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

@EventBusSubscriber
class CommandTest {
    @SubscribeEvent
    static void test(RegisterCommandsEvent event) {
        event.dispatcher.register('no') {
            requires admin
            executes {
                player?.sendSystemMessage(Component.literal('yes'))
            }
            then literal('yes') {
                requires { it.isGameMaster() }
                executes {
                    player?.sendSystemMessage(Component.literal('12'))
                }
                string('firstArgument') { firstArgument ->
                    requires { it.owner }
                    integer('secondArgument', 12) { secondArgument ->
                        executes {
                            source.getPlayer()?.sendSystemMessage(Component.literal(
                                    firstArgument(it) + '#' + secondArgument(it)
                            ))
                        }
                    }
                }

                then literal('kill') {
                    requires { it.hasPermission(0) }
                    entities('multipleEntitySelector') { entities ->
                        executes {
                            final toKill = entities(it)
                            toKill.each {
                                it.remove(Entity.RemovalReason.KILLED)
                            }
                            source.sendSuccess Component.literal("Killed ${toKill.size()} entities!")
                        }
                    }
                }

                then literal('sendMessage') {
                    requires gameMaster
                    player('singlePlayer') { player ->
                        component('message') { message ->
                            executes {
                                player(it).sendSystemMessage(message(it))
                            }
                        }
                    }
                }
            }
        }
    }
}
