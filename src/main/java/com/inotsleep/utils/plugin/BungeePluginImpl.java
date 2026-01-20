package com.inotsleep.utils.plugin;

import com.inotsleep.utils.INSUtils;
import com.inotsleep.utils.i18n.I18n;
import com.inotsleep.utils.i18n.config.LangEntry;
import com.inotsleep.utils.logging.LoggingManager;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

import java.util.List;

public abstract class BungeePluginImpl extends Plugin implements BungeePlugin {

    public static Metrics metrics;
    INSUtils insUtils;

    @Override
    public void onEnable() {
        LoggingManager.setLogger(getLogger());

        insUtils = INSUtilsAPICore.getInstance();

        if (insUtils != null) insUtils.register(this);
        doEnable();
    }

    @Override
    public void onDisable() {
        doDisable();
    }

    public abstract void doEnable();
    public abstract void doDisable();

    @Override
    public String getSlug() {
        return getDescription().getName().toLowerCase();
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

    public void setMetrics(int id) {
        metrics = new Metrics(this, id);
    }
}
