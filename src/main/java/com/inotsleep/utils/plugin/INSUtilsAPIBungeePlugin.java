package com.inotsleep.utils.plugin;

import com.inotsleep.utils.INSUtils;
import com.inotsleep.utils.i18n.I18n;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.util.ArrayList;
import java.util.List;

public class INSUtilsAPIBungeePlugin extends BungeePluginImpl implements INSUtils {
    List<BungeePlugin> bungeePlugins;

    private static INSUtilsAPIBungeePlugin instance;
    public static INSUtilsAPIBungeePlugin getInstance() {
        return instance;
    }

    @Override
    public void doEnable() {
        instance = this;
        INSUtilsAPICore.setInstance(this);

        ProxyServer.getInstance().getScheduler().runAsync(this, () -> {
            I18n.init(getInstance());

            I18n.getInstance().registerConsumer(getInstance());
        });
    }

    @Override
    public void doDisable() {
        I18n.getInstance().shutdown();
    }

    @Override
    public void register(BukkitPlugin plugin) {
        throw  new UnsupportedOperationException("Bukkit plugins cannot be loaded in BungeeCord environment");
    }

    @Override
    public void register(BungeePlugin plugin) {
        if (bungeePlugins == null) return;

        bungeePlugins.add(plugin);
        I18n.getInstance().registerConsumer(plugin);
    }

    @Override
    public List<BukkitPlugin> getBukkitPlugins() {
        return List.of();
    }

    @Override
    public List<BungeePlugin> getBungeePlugins() {
        return bungeePlugins;
    }
}
