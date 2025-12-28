package com.inotsleep.utils.plugin;

import com.inotsleep.utils.BukkitPlugin;

public class INSUtilsAPIBukkitPlugin extends BukkitPlugin implements INSUtilsPlugin {
    @Override
    public void doDisable() {

    }

    @Override
    public void doEnable() {
        INSUtilsAPICore.setInstance(this);
    }
}
