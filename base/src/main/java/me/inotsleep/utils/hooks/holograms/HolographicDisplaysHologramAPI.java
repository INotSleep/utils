package me.inotsleep.utils.hooks.holograms;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.inotsleep.utils.AbstractPlugin;
import me.inotsleep.utils.events.HookInitEvent;
import me.inotsleep.utils.hooks.Initializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class HolographicDisplaysHologramAPI implements HologramAPI {
    public HolographicDisplaysAPI api;

    public HolographicDisplaysHologramAPI(AbstractPlugin<?> plugin) {
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
