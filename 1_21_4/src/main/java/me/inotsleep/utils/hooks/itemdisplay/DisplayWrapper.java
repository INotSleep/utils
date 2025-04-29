package me.inotsleep.utils.hooks.itemdisplay;

import me.inotsleep.utils.Pair;
import me.inotsleep.utils.util_1_21_4.VectorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

public class DisplayWrapper implements BaseDisplayWrapper {
    private Display display;
    private boolean isBlockDisplay;

    @Override
    public void spawn(Location location, boolean isBlockDisplay) {
        if (display != null) return;
        display = location.getWorld().spawn(location, (Class<Display>) (isBlockDisplay ? BlockDisplay.class : ItemDisplay.class));
        this.isBlockDisplay = isBlockDisplay;
    }

    @Override
    public void remove() {
        if (display == null) return;
        display.remove();
    }

    @Override
    public void setScale(Vector scale) {
        if (display == null) return;
        Transformation original = display.getTransformation();
        display.setTransformation(new Transformation(original.getTranslation(), original.getLeftRotation(), VectorUtil.vectorToVector3f(scale), original.getRightRotation()));
    }

    @Override
    public Vector getScale() {
        if (display == null) return null;
        return VectorUtil.vector3fToVector(display.getTransformation().getScale());
    }

    @Override
    public void setLocation(Location location) {
        if (display == null) return;
        location = location.clone();
        location.setYaw(0);
        location.setPitch(0);

        display.teleport(location);
    }

    @Override
    public Location getLocation() {
        if (display == null) return null;
        return display.getLocation();
    }

    @Override
    public void setItem(ItemStack item) {
        if (display == null) return;
        if (isBlockDisplay) {
            if (!item.getType().isBlock()) return;
            ((BlockDisplay) display).setBlock(Bukkit.createBlockData(item.getType()));
        } else {
            if (!item.getType().isItem()) return;
            ((ItemDisplay) display).setItemStack(item);
        }
    }

    @Override
    public ItemStack getItem() {
        if (display == null) return null;
        if (isBlockDisplay) return new ItemStack(((BlockDisplay) display).getBlock().getMaterial());
        return ((ItemDisplay) display).getItemStack();
    }

    @Override
    public void setLeftRotation(Pair<Vector, Float> rotation) {
        if (display == null) return;
        Transformation original = display.getTransformation();
        display.setTransformation(new Transformation(original.getTranslation(), VectorUtil.toQuaterionf(rotation), original.getScale(), original.getRightRotation()));

    }

    @Override
    public Pair<Vector, Float> getLeftRotation() {
        if (display == null) return null;
        return VectorUtil.toVector(display.getTransformation().getLeftRotation());
    }

    @Override
    public void setRightRotation(Pair<Vector, Float> rotation) {
        if (display == null) return;
        Transformation original = display.getTransformation();
        display.setTransformation(new Transformation(original.getTranslation(), original.getLeftRotation(), original.getScale(), VectorUtil.toQuaterionf(rotation)));
    }

    @Override
    public Pair<Vector, Float> getRightRotation() {
        if (display == null) return null;
        return VectorUtil.toVector(display.getTransformation().getRightRotation());
    }
}
