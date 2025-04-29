package me.inotsleep.utils.hooks;

import me.inotsleep.utils.events.HookInitEvent;
import me.inotsleep.utils.hooks.base.BaseHook;
import me.inotsleep.utils.hooks.itemdisplay.BaseDisplayHook;
import me.inotsleep.utils.util.VersionParser;
import org.bukkit.Bukkit;

public class Initializer {
    public static void tryToInitialize() {
        initializeItemDisplay();
    }

    private static void initializeItemDisplay() {
        int version = VersionParser.stringToDataVersion(Bukkit.getMinecraftVersion());
        BaseDisplayHook.init(version >= 3337);
    }

    public static void callInitEvent(BaseHook hook) {
        Bukkit.getPluginManager().callEvent(new HookInitEvent(hook));
    }
}
