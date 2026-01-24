package me.mythicalflame.netherreactor.core.modules.enderreactor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ModdedLeaveListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event)
    {
        EnderReactorModule.removePlayer(event.getPlayer());
    }
}
