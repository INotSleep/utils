package com.inotsleep.insutils.internal.config;

import com.inotsleep.insutils.api.INSUtils;
import com.inotsleep.insutils.api.config.Comment;
import com.inotsleep.insutils.api.config.INSUtilsConfig;
import com.inotsleep.insutils.api.config.Path;
import com.inotsleep.insutils.spi.config.Config;
import com.inotsleep.insutils.spi.config.SerializableObject;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class INSUtilsConfigImpl extends Config implements INSUtilsConfig {
    @Path("configuration")
    private Configuration configuration = new Configuration();

    @Override
    public INSUtilsConfig.Configuration getConfiguration() {
        return configuration;
    }

    public static class Configuration extends SerializableObject implements INSUtilsConfig.Configuration {
        @Comment("Determines whether or not to store an item as Base64 or \"readable\" style ")
        @Path("save-item-as-base64")
        public boolean saveItemAsBase64 = true;

        @Override
        public boolean getSaveItemAsBase64() {
            return saveItemAsBase64;
        }
    }

    public INSUtilsConfigImpl() {
        super(INSUtils.getInstance().getDataFolder(), "config.yml");
    }
}
