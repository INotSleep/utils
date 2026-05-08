package com.inotsleep.insutils.internal.config.codecs.bukkit;

import com.google.gson.*;
import com.inotsleep.insutils.api.config.CommentType;
import com.inotsleep.insutils.api.config.TypeKey;
import com.inotsleep.insutils.api.config.codecs.Codec;
import com.inotsleep.insutils.api.yaml.YamlCommentLine;
import com.inotsleep.insutils.api.yaml.YamlNode;
import com.inotsleep.insutils.api.yaml.YamlNodes;
import com.inotsleep.insutils.api.yaml.YamlScalarNode;
import com.inotsleep.insutils.api.yaml.YamlScalarType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemStackCodec implements Codec<ItemStack> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Override
    public YamlNode serialize(ItemStack value) {
        UnsafeValues unsafe = Bukkit.getUnsafe();
        JsonObject object = unsafe.serializeItemAsJson(value);

        JsonElement componentsElement = object.get("components");
        if (componentsElement != null) {
            JsonObject components = componentsElement.getAsJsonObject();

            JsonElement itemNameElement = components.get("minecraft:item_name");
            if (itemNameElement != null) {
                Component component = JSONComponentSerializer.json().deserialize(gson.toJson(itemNameElement));
                components.remove("minecraft:item_name");
                components.add("minecraft:item_name", new JsonPrimitive(MiniMessage.miniMessage().serialize(component)));
            }

            JsonElement loreElement = components.get("minecraft:lore");
            if (loreElement != null) {
                List<JsonElement> lore = loreElement.getAsJsonArray().asList();
                components.remove("minecraft:lore");

                JsonArray array = new JsonArray();
                for (JsonElement element : lore) {
                    Component component = JSONComponentSerializer.json().deserialize(gson.toJson(element));
                    array.add(new JsonPrimitive(MiniMessage.miniMessage().serialize(component)));
                }
                components.add("minecraft:lore", array);
            }
        }

        YamlScalarNode node = YamlNodes.scalar(gson.toJson(object), YamlScalarType.LITERAL);
        node.setInLineComments(List.of(new YamlCommentLine(
                CommentType.IN_LINE,
                " In case of server version change, items may break!"
        )));
        return node;
    }

    @Override
    public ItemStack deserialize(YamlNode node) {
        UnsafeValues unsafe = Bukkit.getUnsafe();

        if (node instanceof YamlScalarNode scalarNode) {
            Component name;
            List<Component> lore = new ArrayList<>();
            JsonObject object = JsonParser.parseString(scalarNode.getValue()).getAsJsonObject();

            JsonElement componentsElement = object.get("components");
            if (componentsElement != null) {
                JsonObject components = componentsElement.getAsJsonObject();

                if (components.has("minecraft:item_name")) {
                    name = MiniMessage.miniMessage().deserialize(components.get("minecraft:item_name").getAsString());
                    components.remove("minecraft:item_name");
                } else {
                    name = null;
                }

                if (components.has("minecraft:lore")) {
                    JsonArray array = components.get("minecraft:lore").getAsJsonArray();
                    for (JsonElement element : array) {
                        lore.add(MiniMessage.miniMessage().deserialize(element.getAsString()));
                    }
                }
            } else {
                name = null;
            }

            ItemStack stack = unsafe.deserializeItemFromJson(object);
            stack.editMeta(meta -> {
                if (name != null) {
                    meta.itemName(name);
                }
                meta.lore(lore);
            });
            return stack;
        }

        throw new IllegalArgumentException("Unsupported node type " + node.getClass());
    }

    @Override
    public TypeKey<ItemStack> key() {
        return TypeKey.of(ItemStack.class);
    }
}
