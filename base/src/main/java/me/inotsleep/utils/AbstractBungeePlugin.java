package me.inotsleep.utils;

import me.inotsleep.utils.logging.LoggingManager;
import net.md_5.bungee.api.plugin.Plugin;

public abstract class AbstractBungeePlugin<T extends AbstractBungeePlugin<T>> extends Plugin {
    @Override
    public void onEnable() {
        LoggingManager.setLogger(getLogger());

        doEnable();
    }

    @Override
    public void onDisable() {
        doDisable();
    }

    public abstract void doEnable();
    public abstract void doDisable();
}
