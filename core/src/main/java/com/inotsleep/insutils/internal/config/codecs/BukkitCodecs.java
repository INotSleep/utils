package com.inotsleep.insutils.internal.config.codecs;

import com.inotsleep.insutils.api.INSUtils;
import com.inotsleep.insutils.api.config.ConfigHandle;
import com.inotsleep.insutils.internal.config.codecs.bukkit.Base64ItemStackCodec;
import com.inotsleep.insutils.internal.config.codecs.bukkit.ItemStackCodec;

public class BukkitCodecs {
    public static void registerConfigCodecs() {
         if (INSUtils.getInstance().getINSUtilsConfig().getConfiguration().getSaveItemAsBase64())
             ConfigHandle.getInstance().registerCodec(new Base64ItemStackCodec());
             else ConfigHandle.getInstance().registerCodec(new ItemStackCodec());
    }
}
