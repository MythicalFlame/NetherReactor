package me.mythicalflame.netherreactor.commands;

import me.mythicalflame.netherreactor.NetherReactorModLoader;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;
import static net.kyori.adventure.text.Component.text;

public class AboutCommand
{
    public static void aboutCommand(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("netherreactor.command.about"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor about command!</red>"));
            return;
        }

        if (args.length != 1)
        {
            sender.sendMessage(minimessage("<red>Incorrect argument count! Try: /netherreactor about</red>"));
            return;
        }

        //TODO: embed links
        TextComponent.Builder result = text();
        result.append(minimessage("<gold>NetherReactor Mod Loader</gold>\n"));
        result.append(minimessage("<light_purple>Mod loader for PaperMC and its forks that allows for the registration of custom items.</light_purple>\n"));
        result.append(minimessage("Built by MythicalFlame\n"));
        //github link component
        result.append(minimessage("Plugin API version: " + NetherReactorModLoader.getCompatibleVersions()[0].toString()));
        sender.sendMessage(result.build());
    }
}
