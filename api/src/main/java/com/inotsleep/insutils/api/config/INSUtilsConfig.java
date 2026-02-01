package com.inotsleep.insutils.api.config;

public interface INSUtilsConfig {
    Configuration getConfiguration();

    interface Configuration {
        boolean getSaveItemAsBase64();
    }
}
