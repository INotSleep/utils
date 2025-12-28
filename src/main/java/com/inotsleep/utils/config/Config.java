package com.inotsleep.utils.config;

import com.inotsleep.utils.logging.LoggingManager;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.composer.Composer;
import org.snakeyaml.engine.v2.nodes.*;
import org.snakeyaml.engine.v2.parser.ParserImpl;
import org.snakeyaml.engine.v2.scanner.StreamReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public abstract class Config extends SerializableObject {
    File configFile;
    boolean readOnly;
    InputStream stream;

    public Config(File configFile) {
        this.configFile = configFile;
        this.readOnly = false;
    }

    public Config(File baseDir, String fileName) {
        configFile = new File(baseDir, fileName);
        readOnly = false;
    }

    public Config(InputStream stream) {
        readOnly = true;
        this.stream = stream;
    }

    public void reload() {
        if (!readOnly && !configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                LoggingManager.error("Unable to create directory: " + configFile.getParentFile());
            }
        }

        if (!readOnly && !configFile.exists()) {
            LoggingManager.info("Configuration file " + configFile.getName() + " does not exist. Creating...");
            try {
                if (!configFile.createNewFile()) LoggingManager.warn("Config file is already exists, but File#exists() returned false.");
            } catch (IOException e) {
                LoggingManager.error( "Unable to create configuration file: " + configFile.getName(), e);
                return;
            }
            save();
            return;
        }

        LoadSettings settings = LoadSettings.builder().build();

        MappingNode rootNode = null;

        try {
            if (stream == null) stream = Files.newInputStream(configFile.toPath());

            Composer composer = new Composer(settings, new ParserImpl(settings, new StreamReader(settings, new YamlUnicodeReader(stream))));

            while (composer.hasNext()) {
                Node node = composer.next();
                rootNode = (MappingNode) node;
            }

            if (rootNode == null) return;

            deserialize(rootNode);

            stream.close();
        } catch (IOException e) {
            LoggingManager.error("Unable to read configuration file: " + configFile.getName(), e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LoggingManager.error("Unable to read configuration file: " + configFile.getName(), e);
                }
            }
        }
    }



    public void save() {
        if (readOnly) {
            LoggingManager.warn("Configuration file " + configFile.getName() + " tried to save, but it's read-only.");
        }

        if (!configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                LoggingManager.error("Unable to create directory: " + configFile.getParentFile());
            }
        }

        if (!configFile.exists()) {
            LoggingManager.error("Configuration file " + configFile.getName() + " does not exist. Creating...");
            try {
                if (!configFile.createNewFile()) LoggingManager.warn("Config file is already exists, but File#exists() returned false.");
            } catch (IOException e) {
                LoggingManager.error("Unable to create configuration file: " + configFile.getName(), e);
                return;
            }
        }

        MappingNode root = new MappingNode(Tag.MAP, new ArrayList<>(), FlowStyle.AUTO);
        serialize(root);

        DumpSettings settings = DumpSettings.builder()
                .setExplicitStart(false)
                .setExplicitRootTag(Optional.empty())
                .setDumpComments(true)
                .build();

        Dump dump = new Dump(settings);


        try (FileOutputStream fileStream = new FileOutputStream(configFile)) {
            dump.dumpNode(root, new YamlOutputStreamWriter(fileStream, StandardCharsets.UTF_8) {
                @Override
                public void processIOException(IOException e) {
                    LoggingManager.error("Unable to save configuration file: ", e);
                }
            });
        }  catch (IOException e) {
            LoggingManager.error("Unable to save configuration file: ", e);
        }
    }
}
