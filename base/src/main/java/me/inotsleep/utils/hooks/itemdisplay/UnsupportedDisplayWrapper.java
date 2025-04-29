package me.inotsleep.utils.hooks.itemdisplay;

import me.inotsleep.utils.LoggerFactory;
import me.inotsleep.utils.Pair;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class UnsupportedDisplayWrapper implements BaseDisplayWrapper {
    public UnsupportedDisplayWrapper() {
        LoggerFactory.getLogger().warning("Plugin tried to create Item/BlockDisplay while your server not supporting this! Consider to update to 1.19.4 or never!");
    }

    @Override
    public void spawn(Location location, boolean isBlockDisplay) {

    }

    @Override
    public void remove() {

    }

    @Override
    public void setScale(Vector scale) {

    }

    @Override
    public Vector getScale() {
        return null;
    }

    @Override
    public void setLocation(Location location) {

    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void setItem(ItemStack item) {

    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    @Override
    public void setLeftRotation(Pair<Vector, Float> rotation) {

    }

    @Override
    public Pair<Vector, Float> getLeftRotation() {
        return null;
    }

    @Override
    public void setRightRotation(Pair<Vector, Float> rotation) {

    }

    @Override
    public Pair<Vector, Float> getRightRotation() {
        return null;
    }
}
