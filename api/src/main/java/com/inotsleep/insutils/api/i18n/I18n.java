package com.inotsleep.insutils.api.i18n;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public interface I18n {
    AtomicReference<I18n> instance = new AtomicReference<>();

    static void setInstance(I18n i18n) {
        instance.set(i18n);
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
