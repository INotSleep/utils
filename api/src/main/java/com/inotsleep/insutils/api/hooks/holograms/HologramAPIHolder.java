package com.inotsleep.insutils.api.hooks.holograms;

import com.inotsleep.insutils.api.service.ServiceManager;

public interface HologramAPIHolder {
    static HologramAPIHolder getInstance() {
        return ServiceManager.require(HologramAPIHolder.class);
    }

    HologramAPI getHologramAPI();
}
