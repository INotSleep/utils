package com.inotsleep.utils.hooks.holograms;

import org.bukkit.Location;

import java.util.List;

public class HolographicDisplaysHologram implements Hologram {
    me.filoghost.holographicdisplays.api.hologram.Hologram hologram;

    public HolographicDisplaysHologram(me.filoghost.holographicdisplays.api.hologram.Hologram hologram, List<String> text) {
        this.hologram = hologram;
        text.forEach(hologram.getLines()::appendText);
    }

    @Override
    public void delete() {
        hologram.delete();
    }

    @Override
    public void edit(List<String> text, Location location) {
        move(location);
        editLines(text);
    }

    @Override
    public void editLines(List<String> text) {
        hologram.getLines().clear();
        text.forEach(hologram.getLines()::appendText);
    }

    @Override
    public void move(Location location) {
        hologram.setPosition(location);
    }
}
