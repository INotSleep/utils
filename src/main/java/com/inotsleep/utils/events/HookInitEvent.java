package com.inotsleep.utils.events;

import com.inotsleep.utils.hooks.base.BaseHook;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HookInitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final BaseHook hook;

    public HookInitEvent(BaseHook hook) {
        this.hook = hook;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public BaseHook getHook() {
        return hook;
    }
}
