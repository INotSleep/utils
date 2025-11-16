package me.inotsleep.utils.hooks.holograms;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import java.util.List;

public interface Hologram {

    public void delete();
    public void edit(List<String> text, Location location);

    public void editLines(List<String> text);
    public void move(Location location);
}
