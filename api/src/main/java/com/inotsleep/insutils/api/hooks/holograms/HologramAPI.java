package com.inotsleep.insutils.api.hooks.holograms;

import com.inotsleep.insutils.api.hooks.Hook;
import org.bukkit.Location;

import java.util.List;

public interface HologramAPI extends Hook {

    Hologram createHologram(List<String> text, Location location);

    default void deleteHologram(Hologram hologram) {
        hologram.delete();
    }
    default void editHologram(Hologram hologram, List<String> text, Location location) {
        hologram.edit(text, location);
    }

    default String getName() {
        return "Holograms Hook";
    }
}
