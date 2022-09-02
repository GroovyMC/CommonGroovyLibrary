/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
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
                    requires { it.isAnyone() }
                    entities('multipleEntitySelector') { entities ->
                        executes {
                            final toKill = entities(it)
                            toKill.each {
                                it.remove(Entity.RemovalReason.KILLED)
                            }
                            sendSuccess Component.literal("Killed ${toKill.size()} entities!")
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

                then literal('amIOp?') {
                    requires anyone
                    bool('asFailure') { asFailure ->
                        executes {
                            final message = player?.op ? Component.literal("Sorry, you're not OP.") : Component.literal("Yes you are OP!")
                            send"${asFailure(it) ? "Failure" : "Success"}"(message)
                        }
                    }
                }
            }
        }
    }
}
