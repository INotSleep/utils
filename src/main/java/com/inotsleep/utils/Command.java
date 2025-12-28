package com.inotsleep.utils;

import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Command extends org.bukkit.command.Command {
    public Command(String prefix, String commandLabel) {
        super(commandLabel);
        BukkitPlugin.commandMap.register(prefix, this);
    }

    public Command(String prefix, String commandLabel, String permission) {
        super(commandLabel);
        this.setPermission(permission);
        BukkitPlugin.commandMap.register(prefix, this);
    }

    public Command(String prefix, String commandLabel, List<String> aliases) {
        super(commandLabel);
        this.setAliases(aliases);
        BukkitPlugin.commandMap.register(prefix, this);
    }

    public Command(String prefix, String commandLabel, String permission, List<String> aliases) {
        super(commandLabel);
        this.setAliases(aliases);
        this.setPermission(permission);
        BukkitPlugin.commandMap.register(prefix, this);
    }

    public abstract void toExecute(CommandSender sender, String label, String[] args);

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String @NotNull [] args) {
        toExecute(sender, label, args);
        return true;
    }

    public abstract List<String> complete(CommandSender sender, String[] args);

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String @NotNull [] args) {
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
