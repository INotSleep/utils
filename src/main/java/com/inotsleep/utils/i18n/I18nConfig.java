package com.inotsleep.utils.i18n;

import com.inotsleep.utils.config.Config;
import com.inotsleep.utils.config.Path;
import com.inotsleep.utils.plugin.INSUtilsAPICore;

public class I18nConfig extends Config {
    @Path("default-language")
    private String defaultLanguage = "en";

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public I18nConfig() {
        super(INSUtilsAPICore.getInstance().getDataFolder(), "i18n/config.yml");
    }
}
