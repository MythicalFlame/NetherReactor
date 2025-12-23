package me.mythicalflame.netherreactor.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.mythicalflame.netherreactor.NetherReactorModLoader;
import org.bukkit.command.CommandSender;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;

public class AboutSubCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> generateCommand() {
        return Commands.literal("about").executes(AboutSubCommand::aboutExecute);
    }

    private static int aboutExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.about"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor about subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        sender.sendMessage(minimessage("<gold>NetherReactor Mod Loader</gold>"));
        sender.sendMessage(minimessage("<light_purple>Unofficial mod loader for PaperMC and its forks that allows for the registration of custom items.</light_purple>"));
        sender.sendMessage(minimessage("<light_purple>Built by <click:open_url:github.com/MythicalFlame><u>MythicalFlame</u></click></light_purple>"));
        sender.sendMessage("<red><u><click:open_url:mythicalflame.github.io/projects/netherreactor/index.html>Website</click></u></red> <gray><u><click:open_url:github.com/MythicalFlame/NetherReactor>GitHub</click></u></gray>");
        sender.sendMessage(minimessage("<light_purple>Plugin API version: " + NetherReactorModLoader.getCompatibleVersions()[0].toString() + "</light_purple>"));
        return Command.SINGLE_SUCCESS;
    }
}
