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

package io.github.groovymc.cgl.extension

import groovy.transform.CompileStatic
import net.minecraft.network.chat.ClickEvent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType

import java.nio.file.Path

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
