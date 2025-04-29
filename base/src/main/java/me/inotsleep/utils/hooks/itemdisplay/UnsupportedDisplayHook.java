package me.inotsleep.utils.hooks.itemdisplay;

import org.bukkit.Location;

public class UnsupportedDisplayHook implements BaseDisplayHook {
    @Override
    public BaseDisplayWrapper create(Location location, boolean isBlockDisplay) {
        return new UnsupportedDisplayWrapper();
    }
}
