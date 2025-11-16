package me.inotsleep.utils.hooks.holograms;

import me.inotsleep.utils.AbstractBukkitPlugin;
import me.inotsleep.utils.hooks.base.BaseHook;
import me.inotsleep.utils.logging.LoggingManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public interface HologramAPI extends BaseHook {
    AtomicReference<HologramAPI> instance = new AtomicReference<>(null);
    static void init(AbstractBukkitPlugin plugin) {
        LoggingManager.info("Initializing HologramAPI");

        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) instance.set(new HolographicDisplaysHologramAPI(plugin));
        else if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) instance.set(new DecentHologramsHologramAPI(plugin));
        else instance.set(new NullHologramAPI());
    }

    static void checkPlugin(AbstractBukkitPlugin plugin, Plugin toCheck) {
        if (instance.get() == null) return;
        if (toCheck.getName().equals("HolographicDisplays") || toCheck.getName().equals("DecentHolograms")) init(plugin);
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
