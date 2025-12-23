package me.mythicalflame.netherreactor.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;
import static net.kyori.adventure.text.Component.text;

public class NetherReactorCommand
{
    public static LiteralCommandNode<CommandSourceStack> generateCommand() {
        return
                Commands.literal("netherreactor")
                        .executes(NetherReactorCommand::helpMessage)
                        .then(AboutSubCommand.generateCommand())
                        .then(ModSubCommand.generateCommand())
                        .then(GiveSubCommand.generateCommand())
                        .then(ItemSubCommand.generateCommand())
                        .then(TagSubCommand.generateCommand())
                        .build();
    }

    private static final SubCommand[] commandList = {
            new SubCommand("about", "Gives details about the plugin."),
            new SubCommand("mod", "Displays mod information."),
            new SubCommand("item", "Displays information about an item."),
            new SubCommand("give", "Gives items and blocks."),
            new SubCommand("tag", "Checks if content belongs to tags.")};

    private static int helpMessage(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        TextComponent.Builder result = text();
        result.append(minimessage("<gold>NetherReactor command info:</gold>\n"));
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

        return Command.SINGLE_SUCCESS;
    }
}
