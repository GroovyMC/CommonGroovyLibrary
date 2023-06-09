/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package cgltest

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber
class CommandTest {
    @SubscribeEvent
    static void test(RegisterCommandsEvent event) {
        event.dispatcher.register('echo') {
            then registered('name', event.buildContext, Registries.ITEM) {argument ->
                executes {
                    player?.sendSystemMessage(Component.literal(argument(it).getDescriptionId()))
                }
            }
        }

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
                            final message = it.getPlayer()?.isOp() ? Component.literal("Sorry, you're not OP.") : Component.literal("Yes you are OP!")
                            if (asFailure(it)) {
                                sendFailure(message)
                            } else {
                                sendSuccess(message)
                            }
                        }
                    }
                }

                then 'nether', {
                    requires { it.getPlayer()?.op }
                    executes {
                        player?.changeDimension server.getLevel(Level.NETHER)
                    }
                }
            }
        }
    }
}
