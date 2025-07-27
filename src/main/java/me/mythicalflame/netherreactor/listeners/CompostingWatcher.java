package me.mythicalflame.netherreactor.listeners;

import io.papermc.paper.event.entity.EntityCompostItemEvent;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.utilities.NetherReactorAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static org.bukkit.event.EventPriority.HIGHEST;

public final class CompostingWatcher implements Listener
{
    @EventHandler(priority = HIGHEST, ignoreCancelled = true)
    public void onCompost(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Block block = event.getClickedBlock();
            if (block.getType() == Material.COMPOSTER)
            {
                ModdedItem moddedItem = NetherReactorAPI.getModdedItem(event.getItem());
                if (moddedItem != null)
                {
                    int chance = moddedItem.getCompostingChance();
                    int rng = (int) (Math.random() * 100);
                    if (rng < chance)
                    {
                        new EntityCompostItemEvent(event.getPlayer(), block, event.getItem(), true).callEvent();
                    }
                }
            }
        }
    }
}
