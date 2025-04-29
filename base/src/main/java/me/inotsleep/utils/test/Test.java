package me.inotsleep.utils.test;

import me.inotsleep.utils.AbstractPlugin;
import me.inotsleep.utils.util.VersionParser;
import org.bukkit.Bukkit;

public class Test extends AbstractPlugin<Test> {
    @Override
    public void doDisable() {

    }

    @Override
    public void doEnable() {
        System.out.println(VersionParser.stringToDataVersion(Bukkit.getMinecraftVersion()));
        new TestCommand();
    }
}
