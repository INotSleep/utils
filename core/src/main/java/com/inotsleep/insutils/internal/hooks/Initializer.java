package com.inotsleep.insutils.internal.hooks;

import com.inotsleep.insutils.api.hooks.Hook;
import com.inotsleep.insutils.api.events.HookInitEvent;
import org.bukkit.Bukkit;

public class Initializer {
    public static void tryToInitialize() {

    }

    public static void callInitEvent(Hook hook) {
        Bukkit.getPluginManager().callEvent(new HookInitEvent(hook));
    }
}
