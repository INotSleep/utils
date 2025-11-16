package me.inotsleep.utils.hooks;

import me.inotsleep.utils.events.HookInitEvent;
import me.inotsleep.utils.hooks.base.BaseHook;
import org.bukkit.Bukkit;

public class Initializer {
    public static void tryToInitialize() {

    }

    public static void callInitEvent(BaseHook hook) {
        Bukkit.getPluginManager().callEvent(new HookInitEvent(hook));
    }
}
