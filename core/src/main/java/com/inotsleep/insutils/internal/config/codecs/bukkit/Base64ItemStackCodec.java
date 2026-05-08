package com.inotsleep.insutils.internal.config.codecs.bukkit;

import com.inotsleep.insutils.api.config.TypeKey;
import com.inotsleep.insutils.spi.config.codecs.StringBackedCodec;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;

public class Base64ItemStackCodec extends StringBackedCodec<ItemStack> {
    @Override
    public String serializeToString(ItemStack value) {
        return Base64.getEncoder().encodeToString(value.serializeAsBytes());
    }

    @Override
    public ItemStack deserializeFromString(String node) {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(node));
    }

    @Override
    public TypeKey<ItemStack> key() {
        return TypeKey.of(ItemStack.class);
    }
}
