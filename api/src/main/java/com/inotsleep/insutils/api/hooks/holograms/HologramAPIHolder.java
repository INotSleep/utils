package com.inotsleep.insutils.api.hooks.holograms;

import com.inotsleep.insutils.api.INSUtils;

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

public interface HologramAPIHolder {
    AtomicReference<HologramAPIHolder> instance = new AtomicReference<HologramAPIHolder>();
    
    static HologramAPIHolder getInstance() {
        return ServiceLoader
                .load(HologramAPIHolder.class)
                .findFirst()
                .orElseThrow();
    }

    HologramAPI getHologramAPI();
}
