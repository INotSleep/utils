package com.inotsleep.utils.plugin;

import com.inotsleep.utils.i18n.I18nConsumer;
import com.inotsleep.utils.logging.LoggingManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

import java.io.InputStream;

public interface BungeePlugin extends I18nConsumer {
    InputStream getResourceAsStream(String location);

    PluginDescription getDescription();
}
