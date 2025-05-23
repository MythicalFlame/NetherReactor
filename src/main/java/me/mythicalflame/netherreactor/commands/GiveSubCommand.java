package me.mythicalflame.netherreactor.commands;

import me.mythicalflame.netherreactor.items.ModdedItem;
import me.mythicalflame.netherreactor.utilities.NetherReactorAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveSubCommand
{
    public static void giveSubCommand(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("netherreactor.commandnetherreactor.give"))
        {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use the netherreactor give command!");
            return;
        }

        if (args.length < 3)
        {
            sender.sendMessage(ChatColor.DARK_GREEN + "/netherreactor give <playerName> <itemNamespace:itemID> [amount]");
            return;
        }

        /*get player*/
        Player receiver = Bukkit.getPlayer(args[1]);

        if (receiver == null)
        {
            sender.sendMessage(ChatColor.RED + "Could not find online player \"" + args[1] + "\"");
            return;
        }

        /*get item*/
        //index 0 = namespace, index 1 = ID
        String[] itemEntered = args[2].split(":");

        //ensure that itemEntered has 2 elements (correct input from command sender)
        if (itemEntered.length != 2)
        {
            sender.sendMessage(ChatColor.RED + "/netherreactor give <playerName> <itemNamespace:itemID> [amount]");
            return;
        }

        ModdedItem itemFound = NetherReactorAPI.getModdedItem(itemEntered[0], itemEntered[1]);

        if (itemFound == null)
        {
            sender.sendMessage(ChatColor.RED + "Could not find item \"" + itemEntered[0].toLowerCase() + ":" + itemEntered[1].toLowerCase() + "\"");
            return;
        }

        /*give item*/
        int itemAmount = 1;
        if (args.length > 3)
        {
            itemAmount = Integer.parseInt(args[3]);
        }

        ItemStack giveItem = itemFound.getItem();
        giveItem.setAmount(itemAmount);

        receiver.getInventory().addItem(giveItem);

        sender.sendMessage("Gave " + itemAmount + " [" + itemFound.getDisplayName() + "] to " + args[1]);

    }
}
