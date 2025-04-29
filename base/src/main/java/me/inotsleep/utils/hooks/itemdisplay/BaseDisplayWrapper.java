package me.inotsleep.utils.hooks.itemdisplay;

import me.inotsleep.utils.Pair;
import org.bukkit.Location;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public interface BaseDisplayWrapper {

    void spawn(Location location, boolean isBlockDisplay);

    void remove();

    void setScale(Vector scale);
    Vector getScale();

    void setLocation(Location location);
    Location getLocation();

    void setItem(ItemStack item);
    ItemStack getItem();

    void setLeftRotation(Pair<Vector, Float> rotation);
    Pair<Vector, Float> getLeftRotation();

    void setRightRotation(Pair<Vector, Float> rotation);
    Pair<Vector, Float> getRightRotation();
}
