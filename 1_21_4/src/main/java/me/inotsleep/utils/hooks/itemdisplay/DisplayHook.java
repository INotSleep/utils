package me.inotsleep.utils.hooks.itemdisplay;

import me.inotsleep.utils.hooks.Initializer;
import org.bukkit.Location;

public class DisplayHook implements BaseDisplayHook {
    public DisplayHook() {
        Initializer.callInitEvent(this);
    }

    @Override
    public BaseDisplayWrapper create(Location location, boolean isBlockDisplay) {
        DisplayWrapper dw = new DisplayWrapper();
        dw.spawn(
                location, isBlockDisplay
        );
        return dw;
    }
}
