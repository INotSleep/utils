package com.inotsleep.utils.plugin;

import com.inotsleep.utils.INSUtils;
import com.inotsleep.utils.i18n.I18n;
import com.inotsleep.utils.i18n.config.LangEntry;
import com.inotsleep.utils.logging.LoggingManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class BukkitPlugin extends JavaPlugin implements INSBukkitPlugin {
    public static CommandMap commandMap;
    public static Metrics metrics;

    private INSUtils insUtils;

    @Override
    public void onLoad() {
        LoggingManager.setLogger(getLogger());

        commandMap = Bukkit.getCommandMap();
        insUtils = INSUtils.getInstance();

        if (insUtils != null) insUtils.register(this);
        doLoad();
    }

    @Override
    public void onEnable() {
        doEnable();
    }

    public INSUtils getInsUtils() {
        return insUtils;
    }

    @Override
    public void onDisable() {
        doDisable();
    }

    public abstract void doLoad();
    public abstract void doEnable();
    public abstract void doDisable();

    public void setMetrics(int id) {
        metrics = new Metrics(this, id);
    }

    public String getSlug() {
        return getName().toLowerCase();
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

    public CommandMap getCommandMap() {
        return commandMap;
    }
}
