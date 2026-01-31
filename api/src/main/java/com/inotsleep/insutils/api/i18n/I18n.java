package com.inotsleep.insutils.api.i18n;

import com.inotsleep.insutils.api.hooks.holograms.HologramAPIHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public interface I18n {
    AtomicReference<I18n> instance = new AtomicReference<>();

    static void setInstance(I18n instance) {
        if (instance == null) {
            throw new NullPointerException("instance");
        }
        if (!I18n.instance.compareAndSet(null, instance)) {
            throw new IllegalStateException("I18n instance already set");
        }
    }

    static I18n getInstance() {
        return instance.get();
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
