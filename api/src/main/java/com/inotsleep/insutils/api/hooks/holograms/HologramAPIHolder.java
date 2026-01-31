package com.inotsleep.insutils.api.hooks.holograms;

import com.inotsleep.insutils.api.INSUtils;

import java.util.concurrent.atomic.AtomicReference;

public interface HologramAPIHolder {
    AtomicReference<HologramAPIHolder> instance = new AtomicReference<HologramAPIHolder>();
    
    static HologramAPIHolder getInstance() {
        return instance.get();
    }
    
    static void setInstance(HologramAPIHolder instance) {
        if (instance == null) {
            throw new NullPointerException("instance");
        }
        if (!HologramAPIHolder.instance.compareAndSet(null, instance)) {
            throw new IllegalStateException("HologramAPIHolder instance already set");
        }
    }

    HologramAPI getHologramAPI();
}
