package me.mythicalflame.netherreactor.commands;

import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.utilities.ModRegister;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.command.CommandSender;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;

public final class ItemCommand
{
    public static void itemCommand(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("netherreactor.command.item"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor item command!</red>"));
            return;
        }

        if (args.length != 2)
        {
            sender.sendMessage(minimessage("<red>/netherreactor item <itemNamespace:itemID></red>"));
            return;
        }

        String[] input = args[1].split(":");
        if (input.length != 2)
        {
            sender.sendMessage(minimessage("<red>/netherreactor item <itemNamespace:itemID></red>"));
            return;
        }

        ModdedItem itemFound;
        try
        {
            itemFound = ModRegister.getCachedItem(Key.key(input[0].toLowerCase(), input[1].toLowerCase()));
        }
        catch (InvalidKeyException ignored)
        {
            sender.sendMessage(minimessage("<red>/netherreactor item <itemNamespace:itemID></red>"));
            return;
        }

        if (itemFound == null)
        {
            sender.sendMessage(minimessage("<red>Could not find item \"" + args[1] + "\"!</red>"));
        }
        else
        {
            //TODO
            sender.sendMessage(minimessage("TODO"));
            throw new NotImplementedException("ITEM SUBCOMMAND NOT FULLY IMPLEMENTED AFTER REWORK");
            /*
            int customModelData = itemFound.hasCustomModelData() ? itemFound.getCustomModelData() : -1;
            sender.sendMessage(ChatColor.GOLD + "NetherReactor Item Inspection Results:\n" + ChatColor.RESET + "Mod : " + Objects.requireNonNull(NetherReactorAPI.getMod(itemFound)).getDisplayName() + "\nName: " + itemFound.getDisplayName() + "\nNamespace: " + itemFound.getNamespace() + "\nID: " + itemFound.getID() + "\nMaterial: " + itemFound.getMaterial() + "\nCustom Model Data: " + (customModelData == -1 ? "None" : customModelData));
            */
        }
    }
}
