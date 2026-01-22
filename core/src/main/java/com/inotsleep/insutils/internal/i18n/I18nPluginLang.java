package com.inotsleep.insutils.internal.i18n;

import com.inotsleep.insutils.api.i18n.LangEntry;
import com.inotsleep.insutils.internal.i18n.config.LangFile;
import com.inotsleep.insutils.api.logging.LoggingManager;

import java.util.HashMap;
import java.util.Map;

public class I18nPluginLang {
    private final Map<String, LangFile> langMap = new HashMap<>();

    private final String name;

    public I18nPluginLang(String name) {
        this.name = name;
    }


    public void addLang(String lang, LangFile langFile) {
        langMap.put(lang, langFile);
    }

    public LangEntry getLangEntry(String key, String lang) {
        LangFile langFile = langMap.get(lang);
        if (langFile != null) return langFile.getEntry(key);

        LoggingManager.debug("[I18n] Lang file not found plugin: " + name + " lang: " + lang);

        String defaultLang = I18nImpl.getInstance().getConfig().getDefaultLanguage();
        langFile = langMap.get(defaultLang);
        if (langFile != null) return langFile.getEntry(key);

        LoggingManager.debug("[I18n] Lang file not found plugin: " + name + " lang: " + defaultLang);

        return null;
    }

    public LangFile getLangFile(String lang) {
        return langMap.get(lang);
    }
}
