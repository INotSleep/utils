package com.inotsleep.utils.hooks;

import com.inotsleep.utils.events.HookInitEvent;
import org.bukkit.Bukkit;

public class Initializer {
    public static void tryToInitialize() {

    }

    public static void callInitEvent(Hook hook) {
        Bukkit.getPluginManager().callEvent(new HookInitEvent(hook));
    }
}
