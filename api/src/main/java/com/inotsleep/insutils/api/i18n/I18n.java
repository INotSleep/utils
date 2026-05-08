package com.inotsleep.insutils.api.i18n;

import com.inotsleep.insutils.api.hooks.holograms.HologramAPIHolder;
import com.inotsleep.insutils.api.service.ServiceManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface I18n {
    static void setInstance(I18n instance) {
        ServiceManager.register(I18n.class, instance);
    }

    static I18n getInstance() {
        return ServiceManager.get(I18n.class);
    }

    void shutdown();

    void registerConsumer(I18nConsumer consumer);

    @Nullable LangEntry getEntry(String key, String lang, String plugin);
    @NotNull String getString(String key, String lang, String plugin);
    @NotNull List<String> getStringList(String key, String lang, String plugin);

    String getDefaultLang();
    String getPlayerLang(UUID player);

    @ApiStatus.Experimental
    void reload();
}
