package me.inotsleep.utils.hooks.holograms;

import me.inotsleep.utils.AbstractPlugin;
import me.inotsleep.utils.hooks.base.BaseHook;
import me.inotsleep.utils.logging.LoggingManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public interface HologramAPI extends BaseHook {
    AtomicReference<HologramAPI> instance = new AtomicReference<>(null);
    static void init() {
        LoggingManager.info("Initializing HologramAPI");

        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) instance.set(new HolographicDisplaysHologramAPI(AbstractPlugin.getAbstractInstance()));
        else if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) instance.set(new DecentHologramsHologramAPI(AbstractPlugin.getAbstractInstance()));
        else instance.set(new NullHologramAPI());
    }

    static void checkPlugin(Plugin plugin) {
        if (instance.get() == null) return;
        if (plugin.getName().equals("HolographicDisplays") || plugin.getName().equals("DecentHolograms")) init();
    }

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
