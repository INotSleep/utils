package com.inotsleep.insutils.api.plugin;

import com.inotsleep.insutils.api.i18n.I18nConsumer;
import com.inotsleep.insutils.api.i18n.LangEntry;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public interface INSBungeePlugin extends I18nConsumer {
    String getName();

    LangEntry getEntry(String key, ProxiedPlayer player);
    String getString(String key, ProxiedPlayer player);
    List<String> getStringList(String key, ProxiedPlayer player);
}
