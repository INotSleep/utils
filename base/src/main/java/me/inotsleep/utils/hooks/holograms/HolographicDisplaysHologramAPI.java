package me.inotsleep.utils.hooks.holograms;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.inotsleep.utils.AbstractBukkitPlugin;
import me.inotsleep.utils.hooks.Initializer;
import org.bukkit.Location;

import java.util.List;

public class HolographicDisplaysHologramAPI implements HologramAPI {
    public HolographicDisplaysAPI api;

    public HolographicDisplaysHologramAPI(AbstractBukkitPlugin<?> plugin) {
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
