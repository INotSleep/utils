package com.inotsleep.insutils.spi.plugin;

import com.inotsleep.insutils.api.INSUtils;
import com.inotsleep.insutils.api.i18n.I18n;
import com.inotsleep.insutils.api.i18n.LangEntry;
import com.inotsleep.insutils.api.logging.LoggingManager;
import com.inotsleep.insutils.api.plugin.INSBungeePlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.bukkit.OfflinePlayer;

import java.io.InputStream;
import java.util.List;

public abstract class BungeePlugin extends Plugin implements INSBungeePlugin {

    public static Metrics metrics;
    INSUtils insUtils;

    @Override
    public void onLoad() {
        LoggingManager.getInstance().setLogger(getLogger());

        insUtils = INSUtils.getInstance();
        if (insUtils != null) insUtils.register(this);

        doLoad();
    }

    @Override
    public void onEnable() {
        doEnable();
    }

    @Override
    public void onDisable() {
        doDisable();
    }

    public abstract void doLoad();
    public abstract void doEnable();
    public abstract void doDisable();

    @Override
    public String getSlug() {
        return getDescription().getName().toLowerCase();
    }

    @Override
    public String getName() {
        return getDescription().getName();
    }

    public LangEntry getEntry(String key, String lang) {
        I18n i18n = I18n.getInstance();

        return i18n.getEntry(key, lang, getSlug());
    }

    public String getString(String key, String lang) {
        I18n i18n = I18n.getInstance();

        return i18n.getString(key, lang, getSlug());
    }

    public List<String> getStringList(String key, String lang) {
        I18n i18n = I18n.getInstance();

        return i18n.getStringList(key, lang, getSlug());
    }

    public LangEntry getEntry(String key) {
        I18n i18n = I18n.getInstance();

        return i18n.getEntry(key, I18n.getInstance().getDefaultLang(), getSlug());
    }

    public String getString(String key) {
        I18n i18n = I18n.getInstance();

        return i18n.getString(key, I18n.getInstance().getDefaultLang(), getSlug());
    }

    public List<String> getStringList(String key) {
        I18n i18n = I18n.getInstance();

        return i18n.getStringList(key, I18n.getInstance().getDefaultLang(), getSlug());
    }

    public LangEntry getEntry(String key, ProxiedPlayer player) {
        I18n i18n = I18n.getInstance();

        return i18n.getEntry(key, I18n.getInstance().getPlayerLang(player.getUniqueId()), getSlug());
    }

    public String getString(String key, ProxiedPlayer player) {
        I18n i18n = I18n.getInstance();

        return i18n.getString(key, I18n.getInstance().getPlayerLang(player.getUniqueId()), getSlug());
    }

    public List<String> getStringList(String key, ProxiedPlayer player) {
        I18n i18n = I18n.getInstance();

        return i18n.getStringList(key, I18n.getInstance().getPlayerLang(player.getUniqueId()), getSlug());
    }

    public void setMetrics(int id) {
        metrics = new Metrics(this, id);
    }

    public InputStream getResource(String location) {
        return getResourceAsStream(location);
    }
}
