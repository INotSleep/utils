package com.inotsleep.utils.plugin;

import com.inotsleep.utils.AbstractBungeePlugin;

public class INSUtilsAPIBungeePlugin extends AbstractBungeePlugin implements INSUtilsPlugin {
    @Override
    public void doEnable() {
        INSUtilsAPICore.setInstance(this);
    }

    @Override
    public void doDisable() {

    }
}
