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
            case "items" -> ItemsSubCommand.itemsSubCommand(sender, args);
            case "item" -> ItemSubCommand.itemSubCommand(sender, args);
            case "give" -> GiveSubCommand.giveSubCommand(sender, args);
            case "mod" -> ModSubCommand.modSubCommand(sender, args);
            case "mods" -> ModsSubCommand.modsSubCommand(sender, args);
            default -> helpMessage(sender);
        }

        return true;
    }

    //helper methods
    public void helpMessage(CommandSender sender)
    {
        TextComponent.Builder result = text();
        text().append(minimessage("<gold>NetherReactor commands help:</gold>\n"));
        for (SubCommand command : commandList)
        {
            text().append(minimessage("<dark_green>/netherreactor " + command.name() + " </dark_green>" + command.description() + "\n"));
        }
        sender.sendMessage(result.build());
    }
}
