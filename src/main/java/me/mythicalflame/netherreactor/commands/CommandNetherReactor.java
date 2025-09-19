package me.mythicalflame.netherreactor.commands;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;
import static net.kyori.adventure.text.Component.text;

public final class CommandNetherReactor implements CommandExecutor
{
    private final SubCommand[] commandList = {
            new SubCommand("about", "Gives details about the plugin."),
            new SubCommand("give", "Gives items and blocks."),
            new SubCommand("items", "Displays all registered items."),
            new SubCommand("item", "Displays information about an item."),
            new SubCommand("mods", "Displays all registered mods."),
            new SubCommand("mod", "Displays information about a mod.")};

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args)
    {
        if (args.length == 0)
        {
            helpMessage(sender);
            return true;
        }

        switch (args[0].toLowerCase())
        {
            case "about" -> AboutCommand.aboutCommand(sender, args);
            case "items" -> ItemsCommand.itemsCommand(sender, args);
            case "item" -> ItemCommand.itemCommand(sender, args);
            case "give" -> GiveCommand.giveCommand(sender, args);
            case "mod" -> ModCommand.modCommand(sender, args);
            case "mods" -> ModsCommand.modsCommand(sender, args);
            default -> helpMessage(sender);
        }

        return true;
    }

    private void helpMessage(CommandSender sender)
    {
        TextComponent.Builder result = text();
        result.append(minimessage("<gold>NetherReactor commands help:</gold>\n"));
        for (int i = 0; i < commandList.length; ++i)
        {
            SubCommand command = commandList[i];
            result.append(minimessage("<dark_green>/netherreactor " + command.name() + " </dark_green>" + command.description()));
            if (i != commandList.length - 1)
            {
                result.append(minimessage("\n"));
            }
        }
        sender.sendMessage(result.build());
    }
}
