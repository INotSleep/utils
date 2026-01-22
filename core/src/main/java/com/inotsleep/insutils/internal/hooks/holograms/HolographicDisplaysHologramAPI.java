package com.inotsleep.insutils.internal.hooks.holograms;

import com.inotsleep.insutils.api.hooks.holograms.Hologram;
import com.inotsleep.insutils.api.hooks.holograms.HologramAPI;
import com.inotsleep.insutils.spi.plugin.BukkitPlugin;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import com.inotsleep.insutils.internal.hooks.Initializer;
import org.bukkit.Location;

import java.util.List;

public class HolographicDisplaysHologramAPI implements HologramAPI {
    public HolographicDisplaysAPI api;

    public HolographicDisplaysHologramAPI(BukkitPlugin plugin) {
        Initializer.callInitEvent(this);
        api = HolographicDisplaysAPI.get(plugin);
    }

    @Override
    public Hologram createHologram(List<String> text, Location location) {
        return new HolographicDisplaysHologram(api.createHologram(location), text);
    }

    @Override
    public String getPluginName() {
        return "HolographicDisplays";
    }
}
