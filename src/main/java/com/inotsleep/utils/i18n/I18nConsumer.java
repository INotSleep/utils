package com.inotsleep.utils.i18n;

import com.inotsleep.utils.i18n.config.LangEntry;

import java.util.List;

public interface I18nConsumer {
    String getSlug();

    LangEntry getEntry(String key, String lang);
    String getString(String key, String lang);
    List<String> getStringList(String key, String lang);
}
