package com.inotsleep.utils.events;

import com.inotsleep.utils.hooks.Hook;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HookInitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Hook hook;

    public HookInitEvent(Hook hook) {
        this.hook = hook;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Hook getHook() {
        return hook;
    }
}
