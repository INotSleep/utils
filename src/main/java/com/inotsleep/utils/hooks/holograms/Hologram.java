package com.inotsleep.utils.hooks.holograms;

import org.bukkit.Location;

import java.util.List;

public interface Hologram {

    void delete();
    void edit(List<String> text, Location location);

    void editLines(List<String> text);
    void move(Location location);
}
