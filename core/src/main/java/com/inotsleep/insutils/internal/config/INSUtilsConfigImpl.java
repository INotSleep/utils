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

    @Path("item")
    public ItemStack stack = ItemStack.of(Material.DIAMOND);

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
        stack.editMeta(meta -> {
            meta.addEnchant(Enchantment.AQUA_AFFINITY, 1, true);
            meta.addEnchant(Enchantment.BREACH, 2, true);
            meta.addEnchant(Enchantment.CHANNELING, 2, true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            meta.itemName(MiniMessage.miniMessage().deserialize("<green>Hello</green> <red>Hi</red> <#dcdcdc>Hello</#dcdcdc>"));
            meta.lore(List.of(
                    MiniMessage.miniMessage().deserialize("<red>Hi</red> <green>Hello</green>"),
                    MiniMessage.miniMessage().deserialize("<#dcdcdc>World</#dcdcdc>")
            ));
        });
        stack = null;
    }
}
