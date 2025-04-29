package me.inotsleep.utils.hooks.holograms;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import org.bukkit.Location;

import java.util.List;

public class DecentHologramsHologram implements Hologram {
    eu.decentsoftware.holograms.api.holograms.Hologram hologram;

    public DecentHologramsHologram(eu.decentsoftware.holograms.api.holograms.Hologram hologram) {
        this.hologram = hologram;
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
        hologram.getPages().forEach(hologramPage -> hologram.removePage(hologramPage.getIndex()));
        HologramPage page = hologram.addPage();

        text.forEach(s -> page.addLine(new HologramLine(page, page.getNextLineLocation(), s)));
    }

    @Override
    public void move(Location location) {
        hologram.setLocation(location);
    }
}
