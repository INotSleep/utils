package com.inotsleep.insutils.internal.config.codecs;

import com.inotsleep.insutils.api.config.ConfigHandle;
import com.inotsleep.insutils.internal.config.codecs.bukkit.ItemStackCodec;

public class BukkitCodecs {
    public static void registerConfigCodecs() {
        ConfigHandle.getInstance().registerCodec(new ItemStackCodec());
    }
}
