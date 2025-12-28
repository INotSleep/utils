package com.inotsleep.utils.i18n;

import com.inotsleep.utils.i18n.langconfig.LangEntry;
import com.inotsleep.utils.i18n.langconfig.LangFile;
import com.inotsleep.utils.logging.LoggingManager;
import com.inotsleep.utils.plugin.INSUtilsPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class I18n {
    private static I18n instance;

    private final INSUtilsPlugin plugin;
    private I18nConfig config;

    Map<String, I18nPluginLang> pluginLangMap;

    private I18n(INSUtilsPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public LangEntry getEntry(String key, String lang, String plugin) {
        I18nPluginLang pluginLang = pluginLangMap.get(plugin);
        if (pluginLang == null) return null;

        return pluginLang.getLangEntry(key, lang);
    }

    public String getString(String key, String lang, String plugin) {
        LangEntry entry = getEntry(key, lang, plugin);
        if (entry == null || entry.isList()) {
            if (entry == null) LoggingManager.debug("[I18n] No entry found for key: " + key + ", lang: " + lang + ", plugin: " + plugin);
            else LoggingManager.debug("[I18n] Entry for key: " + key + ", lang: " + lang + ", plugin: " + plugin + " is not String");

            return key;
        }

        return entry.getValue();
    }

    public List<String> getStringList(String key, String lang, String plugin) {
        LangEntry entry = getEntry(key, lang, plugin);
        if (entry == null || entry.isString()) {
            if (entry == null) LoggingManager.debug("[I18n] No entry found for key: " + key + ", lang: " + lang + ", plugin: " + plugin);
            else LoggingManager.debug("[I18n] Entry for key: " + key + ", lang: " + lang + ", plugin: " + plugin + " is not List");

            return List.of(key);
        }

        return entry.getListValue();
    }

    public String getGlobalString(String key, String lang) {
        return getString(key, lang, "global");
    }

    public List<String> getGlobalStringList(String key, String lang) {
        return getStringList(key, lang, "global");
    }

    public void reload() {
        Map<String, I18nPluginLang> newPluginLangMap = new HashMap<>();
        config = new I18nConfig();

        config.reload();

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();

        File langFolder = new File(dataFolder, "lang/messages");
        if (!langFolder.exists()) langFolder.mkdirs();

        File[] files = langFolder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (!file.isDirectory()) continue;

            String pluginName = file.getName();
            I18nPluginLang pluginLang = new I18nPluginLang(pluginName);
            newPluginLangMap.put(pluginName, pluginLang);

            File[] langFiles = file.listFiles();
            if (langFiles == null) continue;

            for (File langFile : langFiles) {
                if (!langFile.isFile()) continue;
                if (!langFile.getName().endsWith(".yml")) continue;

                LangFile langFileObject = new LangFile(langFile);
                langFileObject.reload();

                pluginLang.addLang(langFile.getName().replace(".yml", ""), langFileObject);
            }
        }

        pluginLangMap = newPluginLangMap;
    }

    public static I18n getInstance() {
        return instance;
    }

    public static void init(INSUtilsPlugin plugin) {
        instance = new I18n(plugin);
    }

    public I18nConfig getConfig() {
        return config;
    }
}
