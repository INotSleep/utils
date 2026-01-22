package com.inotsleep.insutils.spi.config;

import com.inotsleep.insutils.api.config.ConfigHandle;

import java.io.File;
import java.io.InputStream;

public abstract class UnsafeConfig extends UnsafeSerializableObject {
    File configFile;
    boolean readOnly;
    InputStream stream;

    public UnsafeConfig(File configFile) {
        this.configFile = configFile;
        this.readOnly = false;
    }

    public UnsafeConfig(File baseDir, String fileName) {
        configFile = new File(baseDir, fileName);
        readOnly = false;
    }

    public UnsafeConfig(InputStream stream) {
        readOnly = true;
        this.stream = stream;
    }

    public void reload() {
        ConfigHandle.getInstance().reloadConfig(this);
    }

    public void save() {
        ConfigHandle.getInstance().saveConfig(this);
    }

    public File getFile() {
        return configFile;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public InputStream getStream() {
        return stream;
    }
}
