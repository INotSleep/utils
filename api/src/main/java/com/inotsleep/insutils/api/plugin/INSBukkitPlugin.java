package com.inotsleep.insutils.api.plugin;

import com.inotsleep.insutils.api.i18n.I18n;
import com.inotsleep.insutils.api.i18n.I18nConsumer;
import com.inotsleep.insutils.api.i18n.LangEntry;
import org.bukkit.OfflinePlayer;

import java.util.List;

public interface INSBukkitPlugin extends I18nConsumer {
    String getName();

    LangEntry getEntry(String key, OfflinePlayer player) ;
    String getString(String key, OfflinePlayer player) ;
    List<String> getStringList(String key, OfflinePlayer player);
}
