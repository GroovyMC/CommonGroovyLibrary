package io.github.groovymc.cgl.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface EnvironmentSpecific {
    enum Platform {
        CLIENT, SERVER
    }
    Platform value();
}
