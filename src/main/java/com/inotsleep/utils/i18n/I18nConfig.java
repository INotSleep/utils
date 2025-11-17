package com.inotsleep.utils.i18n;

import com.inotsleep.utils.config.AbstractConfig;
import com.inotsleep.utils.config.Path;
import com.inotsleep.utils.plugin.INSUtilsAPICore;

import java.io.File;

public class I18nConfig extends AbstractConfig {
    @Path("default-language")
    String defaultLanguage = "en";

    public I18nConfig(String fileName) {
        super(INSUtilsAPICore.getInstance().getDataFolder(), "i18n/config.yml");
    }
}
