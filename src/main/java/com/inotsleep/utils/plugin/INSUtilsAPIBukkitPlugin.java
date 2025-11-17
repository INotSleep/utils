package com.inotsleep.utils.plugin;

import com.inotsleep.utils.AbstractBukkitPlugin;

public class INSUtilsAPIBukkitPlugin extends AbstractBukkitPlugin implements INSUtilsPlugin {
    @Override
    public void doDisable() {

    }

    @Override
    public void doEnable() {
        INSUtilsAPICore.setInstance(this);
    }
}
