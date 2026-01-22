package com.inotsleep.insutils.internal.i18n.config;

import com.inotsleep.insutils.spi.config.Config;
import com.inotsleep.insutils.api.INSUtils;
import com.inotsleep.insutils.api.config.Path;

public class I18nConfig extends Config {
    @Path("default-language")
    private String defaultLanguage = "en_us";

    @Path("fallback-enabled")
    private boolean fallbackEnabled = true;

    @Path("fallback-language")
    private String fallbackLanguage = "en_us";

    @Path("fetch-translation-files")
    private boolean fetchTranslationFiles = true;

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public boolean isFallbackEnabled() {
        return fallbackEnabled;
    }

    public void setFallbackEnabled(boolean fallback) {
        this.fallbackEnabled = fallback;
    }

    public String getFallbackLanguage() {
        return fallbackLanguage;
    }

    public void setFallbackLanguage(String fallbackLanguage) {
        this.fallbackLanguage = fallbackLanguage;
    }

    public boolean isFetchTranslationFiles() {
        return fetchTranslationFiles;
    }

    public void setFetchTranslationFiles(boolean fetchTranslationFiles) {
        this.fetchTranslationFiles = fetchTranslationFiles;
    }

    public I18nConfig() {
        super(INSUtils.getInstance().getDataFolder(), "i18n/config.yml");
    }
}
