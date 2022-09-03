/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.extension

import groovy.transform.CompileStatic
import net.minecraft.network.chat.ClickEvent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType

import java.nio.file.Path

/**
 * General static extensions.
 * @author CommonGroovyLibrary
 */
@CompileStatic
class StaticGeneralExtensions {
    static <T extends Recipe> RecipeType<T> simple(RecipeType self, ResourceLocation name) {
        final actualName = name.toString()
        return new RecipeType<T>() {
            @Override
            String toString() {
                return actualName
            }
        }
    }

    // region ClickEvents
    static ClickEvent openUrl(ClickEvent self, String url) {
        new ClickEvent(ClickEvent.Action.OPEN_URL, url)
    }
    static ClickEvent copyToClipboard(ClickEvent self, String text) {
        new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text)
    }
    static ClickEvent openFile(ClickEvent self, String file) {
        new ClickEvent(ClickEvent.Action.OPEN_FILE, file)
    }
    static ClickEvent openFile(ClickEvent self, Path file) {
        new ClickEvent(ClickEvent.Action.OPEN_FILE, file.toString())
    }
    static ClickEvent runCommand(ClickEvent self, String command) {
        new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
    }
    static ClickEvent suggestCommand(ClickEvent self, String command) {
        new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)
    }
    static ClickEvent changePage(ClickEvent self, int pageNumber) {
        new ClickEvent(ClickEvent.Action.CHANGE_PAGE, pageNumber.toString())
    }
    // endregion
}
