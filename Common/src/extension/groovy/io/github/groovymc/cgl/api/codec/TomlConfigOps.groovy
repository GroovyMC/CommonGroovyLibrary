/*
 * Copyright (C) 2022 GroovyMC and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package io.github.groovymc.cgl.api.codec

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.toml.TomlFormat
import com.mojang.serialization.DynamicOps
import groovy.transform.CompileStatic

@CompileStatic
class TomlConfigOps extends NightConfigOps {

    static final TomlConfigOps COMMENTED = new TomlConfigOps(true)
    static final TomlConfigOps UNCOMMENTED = new TomlConfigOps(false)

    final boolean commented

    private TomlConfigOps(boolean commented) {
        this.commented = commented
    }

    @Override
    protected Config newConfig() {
        return TomlFormat.newConfig()
    }

    @Override
    DynamicOps<Object> withoutComments() {
        return UNCOMMENTED
    }
}
