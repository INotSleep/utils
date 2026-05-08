package com.inotsleep.insutils.internal.service;

import com.inotsleep.insutils.api.hooks.holograms.HologramAPIHolder;
import com.inotsleep.insutils.api.service.ServiceManager;
import com.inotsleep.insutils.internal.hooks.holograms.HologramAPIHolderImpl;
import com.inotsleep.insutils.spi.plugin.BukkitPlugin;

public final class BukkitServiceBootstrap {
    private BukkitServiceBootstrap() {
    }

    public static void register(BukkitPlugin plugin) {
        ServiceBootstrap.registerCommonServices();
        registerIfAbsent(HologramAPIHolder.class, new HologramAPIHolderImpl());
        HologramAPIHolderImpl.init(plugin);
    }

    private static <T> void registerIfAbsent(Class<T> type, T implementation) {
        if (ServiceManager.get(type) != null) {
            return;
        }
        ServiceManager.register(type, implementation);
    }
}
