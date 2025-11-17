package com.inotsleep.utils.particle;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Util {
    public static void drawParticle(Object drawer, double x, double y, double z, double xo, double yo, double zo, Particle particle, Material data, int r, int g, int b) {
        if (World.class.isAssignableFrom(drawer.getClass())) drawParticle((World) drawer, x, y, z, xo, yo, zo, particle, data, r, g, b);
        else if (List.class.isAssignableFrom(drawer.getClass())) drawParticle((List<Object>) drawer, x, y, z, xo, yo, zo, particle, data, r, g, b);
        else if (Player.class.isAssignableFrom(drawer.getClass())) drawParticle((Player) drawer, x, y, z, xo, yo, zo, particle, data, r, g, b);
    }

    private static void drawParticle(Player player, double x, double y, double z, double xo, double yo, double zo, Particle particle, Material data, int r, int g, int b) {
        player.spawnParticle(particle, x, y, z, 1, xo, yo, zo, getParticleData(particle, data, r, g, b));
    }

    private static void drawParticle(World world, double x, double y, double z, double xo, double yo, double zo, Particle particle, Material data, int r, int g, int b) {
        world.spawnParticle(particle, x, y, z, 1, xo, yo, zo, getParticleData(particle, data, r, g, b));
    }

    private static void drawParticle(List<Object> players, double x, double y, double z, double xo, double yo, double zo, Particle particle, Material data, int r, int g, int b) {
        players.forEach(p -> drawParticle(p, x, y, z, xo, yo, zo, particle, data, r, g, b));
    }

    private static Object getParticleData(Particle particle, Material data, int r, int g, int b) {
        if (particle.getDataType() == Void.class) {
            return null;
        } else if (particle.getDataType() == Particle.DustOptions.class) {
            return new Particle.DustOptions(Color.fromRGB(r, g, b), 1);
        } else if (particle.getDataType() == ItemStack.class) {
            return new ItemStack(data);
        } else if (particle.getDataType() == BlockData.class) {
            return Bukkit.createBlockData(data);
        }

        return null;
    }
}
