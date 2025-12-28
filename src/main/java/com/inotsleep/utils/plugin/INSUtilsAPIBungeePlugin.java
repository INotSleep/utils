package com.inotsleep.utils.plugin;

import com.inotsleep.utils.BungeePlugin;

public class INSUtilsAPIBungeePlugin extends BungeePlugin implements INSUtilsPlugin {
    @Override
    public void doEnable() {
        INSUtilsAPICore.setInstance(this);
    }

    @Override
    public void doDisable() {

    }
}
