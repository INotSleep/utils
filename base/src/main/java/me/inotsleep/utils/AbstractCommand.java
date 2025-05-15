package me.inotsleep.utils;

import org.bukkit.command.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand extends Command {
    public AbstractCommand(String prefix, String commandLabel) {
        super(commandLabel);
        AbstractBukkitPlugin.commandMap.register(prefix, this);
    }

    public AbstractCommand(String prefix, String commandLabel, String permission) {
        super(commandLabel);
        this.setPermission(permission);
        AbstractBukkitPlugin.commandMap.register(prefix, this);
    }

    public AbstractCommand(String prefix, String commandLabel, List<String> aliases) {
        super(commandLabel);
        this.setAliases(aliases);
        AbstractBukkitPlugin.commandMap.register(prefix, this);
    }

    public AbstractCommand(String prefix, String commandLabel, String permission, List<String> aliases) {
        super(commandLabel);
        this.setAliases(aliases);
        this.setPermission(permission);
        AbstractBukkitPlugin.commandMap.register(prefix, this);
    }

    public abstract void toExecute(CommandSender sender, String label, String[] args);

    @Override
    public boolean execute(CommandSender sender,String label, String[] args) {
        toExecute(sender, label, args);
        return true;
    }

    public abstract List<String> complete(CommandSender sender, String[] args);

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return filter(complete(sender, args), args);
    }

    private List<String> filter(List<String> list, String[] args) {
        if (list == null) return null;
        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();
        for (String arg : list) {
            if (arg.toLowerCase().startsWith(last.toLowerCase()))
                result.add(arg);
        }
        return result;
    }
}
