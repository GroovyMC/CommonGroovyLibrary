package org.groovymc.cgl.api.codec

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.toml.TomlFormat
import com.mojang.serialization.DynamicOps
import groovy.transform.CompileStatic

/**
 * A {@link NightConfigOps} that reads or writes to TOML.
 */
@CompileStatic
class TomlConfigOps extends NightConfigOps {

    /**
     * A TomlConfigOps that attaches comments to its encoded configs
     */
    static final TomlConfigOps COMMENTED = new TomlConfigOps(true)

    /**
     * A TomlConfigOps that does not attach comments to its encoded configs
     */
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
