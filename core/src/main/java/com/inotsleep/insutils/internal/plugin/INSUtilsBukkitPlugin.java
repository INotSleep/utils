package com.inotsleep.insutils.internal.plugin;

import com.inotsleep.insutils.internal.HandlerManager;
import com.inotsleep.insutils.spi.plugin.BukkitPlugin;
import com.inotsleep.insutils.api.plugin.INSBukkitPlugin;
import com.inotsleep.insutils.api.plugin.INSBungeePlugin;
import com.inotsleep.insutils.api.INSUtils;
import com.inotsleep.insutils.internal.hooks.Initializer;
import com.inotsleep.insutils.api.i18n.I18n;
import com.inotsleep.insutils.internal.i18n.I18nImpl;
import com.inotsleep.insutils.internal.listeners.EventListener;
import com.inotsleep.insutils.api.logging.LoggingManager;
import com.inotsleep.insutils.internal.logging.LoggingManagerImpl;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class INSUtilsBukkitPlugin extends BukkitPlugin implements INSUtils {
    private List<INSBukkitPlugin> bukkitPlugins;

    private static INSUtilsBukkitPlugin instance;
    public static INSUtilsBukkitPlugin getInstance() {
        return instance;
    }

    private final Executor executor = new ThreadPerTaskExecutor(
            new ThreadFactory() {
                private final AtomicInteger i = new AtomicInteger(1);

                @Override
                public Thread newThread(@NotNull Runnable r) {
                    Thread t = new Thread(r, "INSUtils-executor-" + i.getAndIncrement());
                    t.setDaemon(true);
                    t.setUncaughtExceptionHandler((th, ex) ->
                            LoggingManager.error("Uncaught exception: ", ex)
                    );
                    return t;
                }
            }
    );

    @Override
    public void doLoad() {
        instance = this;
        Initializer.tryToInitialize();

        INSUtils.setInstance(this);
        bukkitPlugins = new ArrayList<>();
        I18nImpl.init(getInstance());
        I18n.getInstance().registerConsumer(getInstance());

        executor.execute(() -> {
            I18n.getInstance().reload();
        });
    }

    @Override
    public void doEnable() {
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void doDisable() {
        I18n.getInstance().shutdown();
    }

    @Override
    public void register(INSBukkitPlugin plugin) {
        if (bukkitPlugins == null) return; // Assuming that registered plugin is this

        bukkitPlugins.add(plugin);
        I18n.getInstance().registerConsumer(plugin);
    }

    @Override
    public void register(INSBungeePlugin plugin) {
        throw new UnsupportedOperationException("Bungee plugins cannot be loaded in Bukkit environment");
    }

    @Override
    public List<INSBukkitPlugin> getBukkitPlugins() {
        return bukkitPlugins;
    }

    @Override
    public List<INSBungeePlugin> getBungeePlugins() {
        return List.of();
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    static {
        HandlerManager.loadHandlers();
    }
}
