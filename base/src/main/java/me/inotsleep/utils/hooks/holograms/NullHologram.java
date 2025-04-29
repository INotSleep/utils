package me.inotsleep.utils.hooks.holograms;

import me.inotsleep.utils.AbstractPlugin;
import me.inotsleep.utils.LoggerFactory;
import org.bukkit.Location;

import java.util.List;

public class NullHologram implements Hologram {
    @Override
    public void delete() {
        LoggerFactory.getLogger().warning("No hologram plugin is loaded but plugin tryed to use HologramAPI");
    }

    @Override
    public void edit(List<String> text, Location location) {
        LoggerFactory.getLogger().warning("No hologram plugin is loaded but plugin tryed to use HologramAPI");
    }

    @Override
    public void editLines(List<String> text) {
        LoggerFactory.getLogger().warning("No hologram plugin is loaded but plugin tryed to use HologramAPI");
    }

    @Override
    public void move(Location location) {
        LoggerFactory.getLogger().warning("No hologram plugin is loaded but plugin tryed to use HologramAPI");
    }
}
