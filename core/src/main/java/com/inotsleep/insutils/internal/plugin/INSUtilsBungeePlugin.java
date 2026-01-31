package com.inotsleep.insutils.internal.plugin;

import com.inotsleep.insutils.internal.HandlerManager;
import com.inotsleep.insutils.spi.plugin.BungeePlugin;
import com.inotsleep.insutils.api.plugin.INSBukkitPlugin;
import com.inotsleep.insutils.api.plugin.INSBungeePlugin;
import com.inotsleep.insutils.api.INSUtils;
import com.inotsleep.insutils.api.i18n.I18n;
import com.inotsleep.insutils.api.logging.LoggingManager;
import com.inotsleep.insutils.internal.logging.LoggingManagerImpl;
import com.inotsleep.insutils.internal.i18n.I18nImpl;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class INSUtilsBungeePlugin extends BungeePlugin implements INSUtils {
    List<INSBungeePlugin> bungeePlugins;

    private static INSUtilsBungeePlugin instance;
    public static INSUtilsBungeePlugin getInstance() {
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
        INSUtils.setInstance(this);
        bungeePlugins = new ArrayList<>();

        I18nImpl.init(getInstance());
        I18n.getInstance().registerConsumer(getInstance());

        executor.execute(() -> {
            I18n.getInstance().reload();
        });
    }

    @Override
    public void doEnable() {
    }

    @Override
    public void doDisable() {
        I18n.getInstance().shutdown();
    }

    @Override
    public void register(INSBukkitPlugin plugin) {
        throw  new UnsupportedOperationException("Bukkit plugins cannot be loaded in BungeeCord environment");
    }

    @Override
    public void register(INSBungeePlugin plugin) {
        if (bungeePlugins == null) return;

        bungeePlugins.add(plugin);
        I18n.getInstance().registerConsumer(plugin);
    }

    @Override
    public List<INSBukkitPlugin> getBukkitPlugins() {
        return List.of();
    }

    @Override
    public List<INSBungeePlugin> getBungeePlugins() {
        return bungeePlugins;
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    static {
        HandlerManager.loadHandlers();
    }
}
