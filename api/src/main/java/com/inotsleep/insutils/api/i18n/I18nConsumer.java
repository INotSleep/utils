package com.inotsleep.insutils.api.i18n;

import java.io.InputStream;
import java.util.List;

public interface I18nConsumer {
    String getSlug();

    LangEntry getEntry(String key, String lang);
    String getString(String key, String lang);
    List<String> getStringList(String key, String lang);

    LangEntry getEntry(String key);
    String getString(String key);
    List<String> getStringList(String key);

    InputStream getResource(String s);
}
