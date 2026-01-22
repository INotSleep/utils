package com.inotsleep.insutils.api.hooks.holograms;

import java.util.concurrent.atomic.AtomicReference;

public interface HologramAPIHolder {
    AtomicReference<HologramAPIHolder> instance = new AtomicReference<HologramAPIHolder>();
    
    static HologramAPIHolder getInstance() {
        return instance.get();
    }
    
    static void setInstance(HologramAPIHolder instance) {
        HologramAPIHolder.instance.set(instance);
    }

    HologramAPI getHologramAPI();
}
