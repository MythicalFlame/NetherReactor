package me.mythicalflame.netherreactor.commands;

import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.utilities.ModRegister;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;
import static net.kyori.adventure.text.Component.text;

public final class GiveCommand
{
    public static void giveCommand(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("netherreactor.command.give"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor give command!</red>"));
            return;
        }

        if (args.length < 3 || args.length > 4)
        {
            sender.sendMessage(minimessage("<red>Incorrect argument count! Try: /netherreactor give <playerName> <itemNamespace:itemID> [amount]</red>"));
            return;
        }

        Player receiver = Bukkit.getPlayer(args[1]);

        if (receiver == null)
        {
            sender.sendMessage(minimessage("<red>Could not find online player \"" + args[1] + "\"!</red>"));
            return;
        }

        ModdedItem itemFound = ModRegister.getCachedItem(args[2]);

        if (itemFound == null)
        {
            sender.sendMessage(minimessage("<red>Could not find item \"" + args[2] + "\"!</red>"));
            return;
        }

        int itemAmount = 1;
        if (args.length == 4)
        {
            try
            {
                itemAmount = Integer.parseInt(args[3]);

                if (itemAmount < 1)
                {
                    sender.sendMessage("<red>Could not turn " + args[3] + " into a positive integer.</red>");
                }
            }
            catch (NumberFormatException e)
            {
                sender.sendMessage("<red>Could not turn " + args[3] + " into a positive integer.</red>");
            }
        }

        ItemStack giveItem = itemFound.getItemStack(itemAmount);

        receiver.getInventory().addItem(giveItem);

        TextComponent.Builder result = text();
        result.append(minimessage("Gave " + itemAmount + " ["));
        result.append(itemFound.getItemName());
        result.append(minimessage("] to " + args[1]));
        sender.sendMessage(result);
    }
}
