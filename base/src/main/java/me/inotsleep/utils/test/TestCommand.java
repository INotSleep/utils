package me.inotsleep.utils.test;

import me.inotsleep.utils.AbstractCommand;
import me.inotsleep.utils.Pair;
import me.inotsleep.utils.hooks.itemdisplay.BaseDisplayHook;
import me.inotsleep.utils.hooks.itemdisplay.BaseDisplayWrapper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

public class TestCommand extends AbstractCommand {
    public TestCommand() {
        super("test", "test");
    }

    @Override
    public void toExecute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location loc = player.getLocation().subtract(player.getLocation());

            BaseDisplayWrapper display = BaseDisplayHook.instance.get().create(loc, false);
            display.setItem(new ItemStack(Material.DIAMOND));
            display.setScale(new Vector(2, 2, 2));
            display.setLeftRotation(new Pair<>(new Vector(1, 2, 3), 1f));
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
