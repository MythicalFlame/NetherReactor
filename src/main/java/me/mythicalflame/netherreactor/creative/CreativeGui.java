package me.mythicalflame.netherreactor.creative;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.advancedslot.AdvancedSlotManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;

public final class CreativeGui extends Gui
{
    private final AdvancedSlotManager asm = new AdvancedSlotManager(this);

    public CreativeGui(Player player)
    {
        super(player, "creative-gui", minimessage("Creative Inventory"), 6);
    }

    @Override
    public void onOpen(InventoryOpenEvent event)
    {
        mainMenu();
    }

    private void mainMenu()
    {
        fillGui(Material.AIR);

    }
}
