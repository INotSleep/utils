package com.inotsleep.insutils.internal.config.codecs;

import com.inotsleep.insutils.api.INSUtils;
import com.inotsleep.insutils.api.config.Codec;
import com.inotsleep.insutils.api.config.TypeKey;
import com.inotsleep.insutils.api.logging.LoggingManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NBTSerializer;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.*;
import net.querz.nbt.tag.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.comments.CommentType;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.nodes.*;

import java.io.IOException;
import java.util.*;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemStackCodec implements Codec<ItemStack> {
    private static final List<String> TAG_TYPES = List.of(
            "end",
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "byte_array",
            "string",
            "list",
            "compound",
            "int_array",
            "long_array"
    );

    @Override
    public Node serialize(ItemStack value) {
        if (value == null) return null;

        if (INSUtils.getInstance().getINSUtilsConfig().getConfiguration().getSaveItemAsBase64()) {
            return applyCautionComment(serializeAsBase64(value));
        }

        NBTDeserializer nbtDeserializer = new NBTDeserializer(true);
        NamedTag itemStackNbt;
        try {
            itemStackNbt = nbtDeserializer.fromBytes(value.serializeAsBytes());
        } catch (IOException e) {
            LoggingManager.error("Could not serialize ItemStack.", e);
            return null;
        }

        Node node = serializeTag(itemStackNbt.getTag());

        return applyCautionComment(node);
    }

    @Override
    public ItemStack deserialize(Node node) {
        if (node == null) return null;

        if (INSUtils.getInstance().getINSUtilsConfig().getConfiguration().getSaveItemAsBase64()) {
            return deserializeFromBase64(node);
        }


        ExtractedText extracted = new ExtractedText();

        Tag<?> root;
        try {
            root = deserializeTag(node, extracted);
        } catch (RuntimeException ex) {
            LoggingManager.error("Could not deserialize ItemStack YAML -> NBT tree.", ex);
            return null;
        }

        if (!(root instanceof CompoundTag)) {
            LoggingManager.error("ItemStack root NBT tag must be a CompoundTag, got: " + root.getClass().getSimpleName(), null);
            return null;
        }

        try {
            NamedTag namedTag = new NamedTag("", root);

            NBTSerializer serializer = new NBTSerializer(true);
            byte[] bytes = serializer.toBytes(namedTag);

            ItemStack stack = ItemStack.deserializeBytes(bytes);

            applyExtractedText(stack, extracted);

            return stack;
        } catch (IOException ex) {
            LoggingManager.error("Could not serialize NBT tree to bytes for ItemStack deserialization.", ex);
            return null;
        } catch (RuntimeException ex) {
            LoggingManager.error("Could not deserialize ItemStack from NBT bytes.", ex);
            return null;
        }
    }

    private Node applyCautionComment(Node node) {
        node.setInLineComments(List.of(new CommentLine(
                Optional.empty(),
                Optional.empty(),
                " In case of server version change, items may break",
                CommentType.IN_LINE
        )));

        return node;
    }

    private Node serializeAsBase64(ItemStack stack) {
        return new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.STR, Base64.getEncoder().encodeToString(stack.serializeAsBytes()), ScalarStyle.DOUBLE_QUOTED);
    }

    private ItemStack deserializeFromBase64(Node node) {
        if (node instanceof ScalarNode scalarNode) {
            byte[] data = Base64.getDecoder().decode(scalarNode.getValue());
            return ItemStack.deserializeBytes(data);
        } else throw new RuntimeException("Unexpected node type: " + node.getClass().getSimpleName());
    }

    @Override
    public TypeKey<ItemStack> key() {
        return new TypeKey<>() {};
    }

    private Node serializeTag(Tag<?> tag) {
        return switch (tag.getID()) {
            case 0 -> new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.STR, "end", ScalarStyle.DOUBLE_QUOTED);
            case 1, 2, 3, 4, 5, 6 -> serializeNumberTag((NumberTag<?>) tag);
            case 7, 11, 12 -> serializeArrayTag((ArrayTag<?>) tag);
            case 8 -> new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.STR, ((StringTag) tag).getValue(), ScalarStyle.DOUBLE_QUOTED);
            case 9 -> serializeListTag((ListTag<?>) tag);
            case 10 -> serializeCompound((CompoundTag) tag);
            default -> throw new IllegalStateException("Unexpected value: " + tag.getID());
        };
    }

    private Node wrapTypedNode(Node node, String type) {
        return new MappingNode(
                org.snakeyaml.engine.v2.nodes.Tag.MAP,
                List.of(
                        new NodeTuple(
                                new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.STR, "type", ScalarStyle.DOUBLE_QUOTED),
                                new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.STR, type, ScalarStyle.DOUBLE_QUOTED)
                        ),
                        new NodeTuple(
                                new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.STR, "value", ScalarStyle.DOUBLE_QUOTED),
                                node
                        )
                ),
                FlowStyle.AUTO
        );
    }

    private Node serializeNumberTag(NumberTag<?> tag) {
        String tagType = TAG_TYPES.get(tag.getID());
        Number value = switch (tag.getID()) {
            case 1 -> tag.asByte();
            case 2 -> tag.asShort();
            case 3 -> tag.asInt();
            case 4 -> tag.asLong();
            case 5 -> tag.asFloat();
            case 6 -> tag.asDouble();
            default -> throw new IllegalStateException("Unexpected value: " + tag.getID());
        };

        return new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.STR, value.toString() + tagType.toUpperCase().charAt(0), ScalarStyle.DOUBLE_QUOTED);
    }

    private static SequenceNode sequenceOfInts(int length, IntFunction<String> valueAt) {
        List<Node> items = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            items.add(new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.INT, valueAt.apply(i), ScalarStyle.DOUBLE_QUOTED));
        }
        return new SequenceNode(org.snakeyaml.engine.v2.nodes.Tag.SEQ, items, FlowStyle.AUTO);
    }

    private Node serializeArrayTag(ArrayTag<?> tag) {
        String tagType = TAG_TYPES.get(tag.getID());

        SequenceNode list = switch(tag.getID()) {
            case 7 -> sequenceOfInts(tag.length(), (i) -> String.valueOf(((ByteArrayTag) tag).getValue()[i]));
            case 11 -> sequenceOfInts(tag.length(), (i) -> String.valueOf(((IntArrayTag) tag).getValue()[i]));
            case 12 -> sequenceOfInts(tag.length(), (i) -> String.valueOf(((LongArrayTag) tag).getValue()[i]));
            default -> throw new IllegalStateException("Unexpected value: " + tag.getID());
        };

        return wrapTypedNode(list, tagType);
    }

    private Node serializeListTag(ListTag<?> tag) {
        String tagType = TAG_TYPES.get(tag.getID());
        List<Node> values = new ArrayList<>();

        for (int i = 0; i < tag.size(); i++) {
            values.add(serializeTag(tag.get(i)));
        }

        return wrapTypedNode(
                new SequenceNode(org.snakeyaml.engine.v2.nodes.Tag.SEQ, values, FlowStyle.AUTO),
                tagType
        );
    }

    private Node serializeCompound(CompoundTag tag) {
        String tagType = TAG_TYPES.get(tag.getID());
        List<NodeTuple> items = new ArrayList<>();

        for (Map.Entry<String, Tag<?>> entry : tag.entrySet()) {
            String key = entry.getKey();
            Tag<?> valueTag = entry.getValue();
            Node node;
            if (Objects.equals(key, "minecraft:item_name") || Objects.equals(key, "minecraft:custom_name")) {
                node = handleItemName(valueTag);
            } else if (Objects.equals(key, "minecraft:lore")) {
                node = handleLore(valueTag);
            } else {
                node = serializeTag(valueTag);
            }

            items.add(
                    new NodeTuple(
                            new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.STR, key, ScalarStyle.DOUBLE_QUOTED),
                            node
                    )
            );
        }

        return wrapTypedNode(
                new MappingNode(
                        org.snakeyaml.engine.v2.nodes.Tag.MAP,
                        items,
                        FlowStyle.AUTO
                ),
                tagType
        );

    }

    private Node handleItemName(Tag<?> tag) {
        String miniMessage = toMiniMessage(tag);
        return new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.STR, miniMessage, ScalarStyle.DOUBLE_QUOTED);
    }

    private Node handleLore(Tag<?> tag) {
        if (!(tag instanceof ListTag<?> listTag)) {
            return new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.STR, toMiniMessage(tag), ScalarStyle.DOUBLE_QUOTED);
        }

        List<Node> lines = new ArrayList<>(listTag.size());
        for (int i = 0; i < listTag.size(); i++) {
            Tag<?> line = listTag.get(i);
            lines.add(new ScalarNode(org.snakeyaml.engine.v2.nodes.Tag.STR, toMiniMessage(line), ScalarStyle.DOUBLE_QUOTED));
        }

        return new SequenceNode(org.snakeyaml.engine.v2.nodes.Tag.SEQ, lines, FlowStyle.AUTO);
    }

    private String toMiniMessage(Tag<?> tag) {
        switch (tag) {
            case null -> {
                return "";
            }
            case StringTag st -> {
                return escapeMiniMessageText(st.getValue());
            }
            case NumberTag<?> nt -> {
                return escapeMiniMessageText(numberToPlainString(nt));
            }
            default -> {
            }
        }

        if (!(tag instanceof CompoundTag ct)) {
            return escapeMiniMessageText(String.valueOf(tag));
        }

        if (ct.size() == 1 && ct.containsKey("") && ct.get("") instanceof StringTag st) {
            return escapeMiniMessageText(st.getValue());
        }

        String content = buildComponentCore(ct);
        return applyStyleAndEvents(ct, content);
    }

    private String buildComponentCore(CompoundTag ct) {
        String base = buildComponentBase(ct);

        Tag<?> extraTag = ct.get("extra");
        if (extraTag instanceof ListTag<?> extraList) {
            StringBuilder sb = new StringBuilder(base);
            for (int i = 0; i < extraList.size(); i++) {
                sb.append(toMiniMessage(extraList.get(i)));
            }
            return sb.toString();
        }

        return base;
    }

    private String buildComponentBase(CompoundTag ct) {
        String text = getString(ct, "text");
        if (text != null) {
            return escapeMiniMessageText(text);
        }

        String translateKey = getString(ct, "translate");
        if (translateKey != null) {
            String fallback = getString(ct, "fallback");
            List<Tag<?>> with = getListElements(ct.get("with"));

            StringBuilder sb = new StringBuilder();
            if (fallback != null) {
                sb.append("<lang_or:").append(translateKey).append(":").append(quoteMiniMessageArg(escapeMiniMessageText(fallback)));
                for (Tag<?> arg : with) {
                    sb.append(":").append(quoteMiniMessageArg(toMiniMessage(arg)));
                }
            } else {
                sb.append("<lang:").append(translateKey);
                for (Tag<?> arg : with) {
                    sb.append(":").append(quoteMiniMessageArg(toMiniMessage(arg)));
                }
            }
            sb.append(">");
            return sb.toString();
        }

        String keybind = getString(ct, "keybind");
        if (keybind != null) {
            return "<key:" + keybind + ">";
        }

        CompoundTag score = getCompound(ct, "score");
        if (score != null) {
            String name = getString(score, "name");
            String objective = getString(score, "objective");

            if (name == null) name = "";
            if (objective == null) objective = "";

            return "<score:" + safeArgPart(name) + ":" + safeArgPart(objective) + "/>";
        }

        String selector = getString(ct, "selector");
        if (selector != null) {
            Tag<?> sepTag = ct.get("separator");
            if (sepTag != null) {
                String sep = toMiniMessage(sepTag);
                return "<selector:" + safeArgPart(selector) + ":" + quoteMiniMessageArg(sep) + ">";
            }
            return "<selector:" + safeArgPart(selector) + ">";
        }

        String nbtPath = getString(ct, "nbt");
        if (nbtPath != null) {
            String type;
            String id;

            String block = getString(ct, "block");
            String entity = getString(ct, "entity");
            String storage = getString(ct, "storage");

            if (block != null) {
                type = "block";
                id = block;
            } else if (entity != null) {
                type = "entity";
                id = entity;
            } else if (storage != null) {
                type = "storage";
                id = storage;
            } else {
                type = "storage";
                id = "minecraft:unknown";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<nbt:").append(type).append(":")
                    .append(quoteIfNeeded(id)).append(":")
                    .append(quoteIfNeeded(nbtPath));

            Tag<?> sepTag = ct.get("separator");
            if (sepTag != null) {
                sb.append(":").append(quoteMiniMessageArg(toMiniMessage(sepTag)));
            }

            Boolean interpret = getBoolean(ct, "interpret");
            if (Boolean.TRUE.equals(interpret)) {
                sb.append(":interpret");
            }

            sb.append("/>");
            return sb.toString();
        }

        return "";
    }


    private String applyStyleAndEvents(CompoundTag ct, String content) {
        List<String> open = new ArrayList<>();
        List<String> close = new ArrayList<>();

        CompoundTag click = getCompound(ct, "clickEvent");
        if (click != null) {
            String action = getString(click, "action");
            String value = getString(click, "value");
            if (action != null && value != null) {
                open.add("<click:" + action.toLowerCase() + ":" + quoteMiniMessageArg(value) + ">");
                close.add("</click>");
            }
        }

        CompoundTag hover = getCompound(ct, "hoverEvent");
        if (hover != null) {
            String action = getString(hover, "action");
            if (action != null) {
                String mm = buildHoverTag(action.toLowerCase(), hover);
                open.add(mm);
                close.add("</hover>");
            }
        }

        String insertion = getString(ct, "insertion");
        if (insertion != null) {
            open.add("<insert:" + quoteMiniMessageArg(insertion) + ">");
            close.add("</insert>");
        }

        String font = getString(ct, "font");
        if (font != null) {
            open.add("<font:" + font + ">");
            close.add("</font>");
        }

        String shadow = shadowColorToMiniMessage(ct.get("shadow_color"));
        if (shadow != null) {
            open.add("<shadow:" + shadow + ">");
            close.add("</shadow>");
        }

        String color = getString(ct, "color");
        if (color != null && !color.isEmpty() && !Objects.equals(color.toLowerCase(), "reset")) {
            String c = color.toLowerCase();
            open.add("<" + c + ">");
            close.add("</" + c + ">");
        }

        appendDecoration(open, close, ct, "bold", "b");
        appendDecoration(open, close, ct, "italic", "i");
        appendDecoration(open, close, ct, "underlined", "u");
        appendDecoration(open, close, ct, "strikethrough", "st");
        appendDecoration(open, close, ct, "obfuscated", "obf");

        StringBuilder out = new StringBuilder();
        for (String s : open) out.append(s);
        out.append(content);
        for (int i = close.size() - 1; i >= 0; i--) out.append(close.get(i));
        return out.toString();
    }

    private String buildHoverTag(String action, CompoundTag hover) {
        Tag<?> contents = hover.containsKey("contents") ? hover.get("contents") : hover.get("value");

        if (Objects.equals(action, "show_text")) {
            String inner = toMiniMessage(normalizeHoverText(contents));
            return "<hover:show_text:" + quoteMiniMessageArg(inner) + ">";
        }

        if (Objects.equals(action, "show_item")) {
            String value = hoverShowItemValue(contents);
            if (value == null) {
                value = "";
            }
            return "<hover:show_item:" + quoteMiniMessageArg(value) + ">";
        }

        if (Objects.equals(action, "show_entity")) {
            String value = hoverShowEntityValue(contents);
            if (value == null) {
                value = "";
            }
            return "<hover:show_entity:" + quoteMiniMessageArg(value) + ">";
        }

        String fallback = toMiniMessage(contents);
        return "<hover:show_text:" + quoteMiniMessageArg(fallback) + ">";
    }

    private Tag<?> normalizeHoverText(Tag<?> contents) {
        if (contents instanceof ListTag<?> list) {
            CompoundTag wrapper = new CompoundTag();
            ListTag<CompoundTag> extra = new ListTag<>(CompoundTag.class);
            for (int i = 0; i < list.size(); i++) {
                extra.add((CompoundTag) list.get(i));
            }
            wrapper.putString("text", "");
            wrapper.put("extra", extra);
            return wrapper;
        }
        return contents;
    }

    private String hoverShowItemValue(Tag<?> contents) {
        if (contents instanceof StringTag st) {
            return st.getValue();
        }

        if (contents instanceof CompoundTag ct) {
            String id = getString(ct, "id");
            if (id == null) {
                id = getString(ct, "type");
            }

            String countStr = null;
            Tag<?> countTag = ct.get("count");
            if (countTag instanceof NumberTag<?> nt) {
                countStr = Integer.toString(nt.asInt());
            }

            String tag = getString(ct, "tag");
            if (tag == null && ct.containsKey("tag")) {
                tag = String.valueOf(ct.get("tag"));
            }

            if (id == null) {
                id = "minecraft:air";
            }

            StringBuilder sb = new StringBuilder();
            sb.append(id);
            if (countStr != null) {
                sb.append(":").append(countStr);
                if (tag != null) {
                    sb.append(":").append(tag);
                }
            }
            return sb.toString();
        }

        return String.valueOf(contents);
    }

    private String hoverShowEntityValue(Tag<?> contents) {
        if (contents instanceof StringTag st) {
            return st.getValue();
        }

        if (contents instanceof CompoundTag ct) {
            String type = getString(ct, "type");
            String uuid = getString(ct, "id");
            if (uuid == null) {
                uuid = getString(ct, "uuid");
            }

            Tag<?> nameTag = ct.get("name");
            String name = (nameTag != null) ? toMiniMessage(nameTag) : null;

            if (type == null) type = "minecraft:pig";
            if (uuid == null) uuid = "00000000-0000-0000-0000-000000000000";

            StringBuilder sb = new StringBuilder();
            sb.append(type).append(":").append(uuid);
            if (name != null && !name.isEmpty()) {
                sb.append(":").append(name);
            }
            return sb.toString();
        }

        return String.valueOf(contents);
    }

    private void appendDecoration(List<String> open, List<String> close, CompoundTag ct, String nbtKey, String mmTag) {
        Boolean val = getBoolean(ct, nbtKey);
        if (val == null) {
            return;
        }

        if (val) {
            open.add("<" + mmTag + ">");
        } else {
            open.add("<" + mmTag + ":false>");
        }
        close.add("</" + mmTag + ">");
    }


    private CompoundTag getCompound(CompoundTag ct, String key) {
        Tag<?> t = ct.get(key);
        return (t instanceof CompoundTag c) ? c : null;
    }

    private String getString(CompoundTag ct, String key) {
        Tag<?> t = ct.get(key);
        return (t instanceof StringTag st) ? st.getValue() : null;
    }

    private Boolean getBoolean(CompoundTag ct, String key) {
        if (!ct.containsKey(key)) {
            return null;
        }
        Tag<?> t = ct.get(key);
        if (t instanceof NumberTag<?> nt) {
            return nt.asByte() != 0;
        }
        return null;
    }

    private List<Tag<?>> getListElements(Tag<?> t) {
        if (!(t instanceof ListTag<?> list)) {
            return List.of();
        }
        List<Tag<?>> out = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            out.add((Tag<?>) list.get(i));
        }
        return out;
    }

    private String numberToPlainString(NumberTag<?> nt) {
        return switch (nt.getID()) {
            case 1 -> Byte.toString(nt.asByte());
            case 2 -> Short.toString(nt.asShort());
            case 3 -> Integer.toString(nt.asInt());
            case 4 -> Long.toString(nt.asLong());
            case 5 -> Float.toString(nt.asFloat());
            case 6 -> Double.toString(nt.asDouble());
            default -> String.valueOf(nt);
        };
    }

    private String escapeMiniMessageText(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        String out = s.replace("\\", "\\\\");
        out = out.replace("<", "\\<");
        return out;
    }

    private String quoteMiniMessageArg(String raw) {
        if (raw == null) {
            raw = "";
        }

        boolean hasSingle = raw.indexOf('\'') >= 0;
        boolean hasDouble = raw.indexOf('"') >= 0;

        char q;
        if (!hasSingle) {
            q = '\'';
        } else if (!hasDouble) {
            q = '"';
        } else {
            q = '\'';
        }

        String esc = raw.replace("\\", "\\\\");
        if (q == '\'') {
            esc = esc.replace("'", "\\'");
        } else {
            esc = esc.replace("\"", "\\\"");
        }
        return q + esc + q;
    }


    private String quoteIfNeeded(String s) {
        if (s == null) {
            return "''";
        }
        boolean needs = s.indexOf(':') >= 0 || s.indexOf(' ') >= 0 || s.indexOf('\'') >= 0 || s.indexOf('"') >= 0;
        return needs ? quoteMiniMessageArg(s) : s;
    }

    private String safeArgPart(String s) {
        if (s == null) {
            return "";
        }
        boolean needs = s.indexOf(' ') >= 0 || s.indexOf('\'') >= 0 || s.indexOf('"') >= 0;
        return needs ? quoteMiniMessageArg(s) : s;
    }

    private String shadowColorToMiniMessage(Tag<?> t) {
        switch (t) {
            case null -> {
                return null;
            }
            case StringTag st -> {
                String v = st.getValue();
                if (v == null || v.isEmpty()) {
                    return null;
                }
                return v;
            }
            case NumberTag<?> nt -> {
                int argb = nt.asInt();
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >>> 16) & 0xFF;
                int g = (argb >>> 8) & 0xFF;
                int b = (argb) & 0xFF;
                return String.format("#%02X%02X%02X%02X", r, g, b, a);
            }
            default -> {
            }
        }
        return null;
    }

    private void applyExtractedText(ItemStack stack, ExtractedText extracted) {
        if (stack == null || extracted == null) {
            return;
        }

        boolean hasName = extracted.itemNameMiniMessage != null;
        boolean hasLore = extracted.loreMiniMessage != null && !extracted.loreMiniMessage.isEmpty();

        if (!hasName && !hasLore) {
            return;
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }

        MiniMessage mm = MiniMessage.miniMessage();

        if (hasName) {
            Component display = mm.deserialize(extracted.itemNameMiniMessage);
            meta.displayName(display);
        }

        if (hasLore) {
            List<Component> lore = new ArrayList<>(extracted.loreMiniMessage.size());
            for (String line : extracted.loreMiniMessage) {
                lore.add(mm.deserialize(line));
            }
            meta.lore(lore);
        }

        stack.setItemMeta(meta);
    }


    private static final Pattern NUMBER_SUFFIX = Pattern.compile(
            "^[+-]?\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?([bBsSiIlLfFdD])$"
    );

    private Tag<?> deserializeTag(Node node, ExtractedText extracted) {
        if (node instanceof MappingNode mapping && isTypedWrapper(mapping)) {
            String type = requireScalarString(getMappingValue(mapping, "type"), "type");
            Node valueNode = getMappingValue(mapping, "value");

            return switch (type) {
                case "byte_array" -> new ByteArrayTag(readByteArray(valueNode));
                case "int_array" -> new IntArrayTag(readIntArray(valueNode));
                case "long_array" -> new LongArrayTag(readLongArray(valueNode));

                case "list" -> readListTag(valueNode, extracted);

                case "compound" -> readCompoundTag(valueNode, extracted);

                default -> throw new IllegalStateException("Unknown tag type: " + type);
            };
        }

        if (node instanceof ScalarNode scalar) {
            String s = scalar.getValue();

            Tag<?> number = tryParseNumberTag(s);
            if (number != null) {
                return number;
            }

            return new StringTag(s);
        }

        if (node instanceof SequenceNode seq) {
            return readListTag(seq, extracted);
        }

        throw new IllegalStateException("Unsupported YAML node type: " + node.getClass().getName());
    }

    private Tag<?> tryParseNumberTag(String s) {
        Matcher m = NUMBER_SUFFIX.matcher(s);
        if (!m.matches()) {
            return null;
        }

        char suffix = Character.toUpperCase(m.group(1).charAt(0));
        String raw = s.substring(0, s.length() - 1);

        return switch (suffix) {
            case 'B' -> new ByteTag(Byte.parseByte(raw));
            case 'S' -> new ShortTag(Short.parseShort(raw));
            case 'I' -> new IntTag(Integer.parseInt(raw));
            case 'L' -> new LongTag(Long.parseLong(raw));
            case 'F' -> new FloatTag(Float.parseFloat(raw));
            case 'D' -> new DoubleTag(Double.parseDouble(raw));
            default -> null;
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ListTag readListTag(Node valueNode, ExtractedText extracted) {
        SequenceNode seq = requireSequence(valueNode, "list.value");

        if (seq.getValue().isEmpty()) {
            return new ListTag<>(EndTag.class);
        }

        Tag<?> first = deserializeTag(seq.getValue().getFirst(), extracted);
        Class firstClass = first.getClass();

        ListTag list = new ListTag<>(firstClass);
        list.add(first);

        for (int i = 1; i < seq.getValue().size(); i++) {
            Tag<?> t = deserializeTag(seq.getValue().get(i), extracted);
            if (!firstClass.equals(t.getClass())) {
                throw new IllegalStateException("NBT ListTag elements must be same type. Expected "
                        + firstClass.getSimpleName() + " but got " + t.getClass().getSimpleName());
            }
            list.add(t);
        }

        return list;
    }

    private CompoundTag readCompoundTag(Node valueNode, ExtractedText extracted) {
        MappingNode map = requireMapping(valueNode, "compound.value");
        CompoundTag compound = new CompoundTag();

        for (NodeTuple tuple : map.getValue()) {
            String key = requireScalarString(tuple.getKeyNode(), "compound.key");

            if ("minecraft:item_name".equals(key)) {
                extracted.itemNameMiniMessage = readMiniMessageString(tuple.getValueNode(), "minecraft:item_name");
                continue;
            }

            if ("minecraft:lore".equals(key)) {
                extracted.loreMiniMessage = readMiniMessageStringList(tuple.getValueNode(), "minecraft:lore");
                continue;
            }

            Tag<?> val = deserializeTag(tuple.getValueNode(), extracted);
            compound.put(key, val);
        }

        return compound;
    }

    private byte[] readByteArray(Node node) {
        SequenceNode seq = requireSequence(node, "byte_array.value");
        byte[] out = new byte[seq.getValue().size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) parseLong(requireScalarString(seq.getValue().get(i), "byte_array[" + i + "]"));
        }
        return out;
    }

    private int[] readIntArray(Node node) {
        SequenceNode seq = requireSequence(node, "int_array.value");
        int[] out = new int[seq.getValue().size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = (int) parseLong(requireScalarString(seq.getValue().get(i), "int_array[" + i + "]"));
        }
        return out;
    }

    private long[] readLongArray(Node node) {
        SequenceNode seq = requireSequence(node, "long_array.value");
        long[] out = new long[seq.getValue().size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = parseLong(requireScalarString(seq.getValue().get(i), "long_array[" + i + "]"));
        }
        return out;
    }

    private String readMiniMessageString(Node node, String path) {
        if (node instanceof MappingNode map && isTypedWrapper(map)) {
            Node v = getMappingValue(map, "value");
            return requireScalarString(v, path);
        }
        return requireScalarString(node, path);
    }

    private List<String> readMiniMessageStringList(Node node, String path) {
        if (node instanceof MappingNode map && isTypedWrapper(map)) {
            Node v = getMappingValue(map, "value");
            return readMiniMessageStringList(v, path + ".value");
        }

        SequenceNode seq = requireSequence(node, path);
        List<String> out = new ArrayList<>(seq.getValue().size());
        for (int i = 0; i < seq.getValue().size(); i++) {
            out.add(requireScalarString(seq.getValue().get(i), path + "[" + i + "]"));
        }
        return out;
    }

    private boolean isTypedWrapper(MappingNode map) {
        boolean hasType = false;
        boolean hasValue = false;

        for (NodeTuple t : map.getValue()) {
            if (t.getKeyNode() instanceof ScalarNode sk) {
                String k = sk.getValue();
                if ("type".equals(k)) {
                    hasType = true;
                } else if ("value".equals(k)) {
                    hasValue = true;
                }
            }
        }

        return hasType && hasValue;
    }

    private Node getMappingValue(MappingNode map, String key) {
        for (NodeTuple t : map.getValue()) {
            if (t.getKeyNode() instanceof ScalarNode sk && key.equals(sk.getValue())) {
                return t.getValueNode();
            }
        }
        throw new IllegalStateException("Missing key '" + key + "' in mapping.");
    }

    private String requireScalarString(Node node, String path) {
        if (node instanceof ScalarNode s) {
            return s.getValue();
        }
        throw new IllegalStateException("Expected scalar at " + path + ", got " + node.getClass().getSimpleName());
    }

    private SequenceNode requireSequence(Node node, String path) {
        if (node instanceof SequenceNode s) {
            return s;
        }
        throw new IllegalStateException("Expected sequence at " + path + ", got " + node.getClass().getSimpleName());
    }

    private MappingNode requireMapping(Node node, String path) {
        if (node instanceof MappingNode m) {
            return m;
        }
        throw new IllegalStateException("Expected mapping at " + path + ", got " + node.getClass().getSimpleName());
    }

    private long parseLong(String s) {
        Tag<?> maybe = tryParseNumberTag(s);
        if (maybe instanceof NumberTag<?> n) {
            return n.asLong();
        }
        return Long.parseLong(s);
    }


    private static final class ExtractedText {
        private String itemNameMiniMessage;
        private List<String> loreMiniMessage;
    }
}
