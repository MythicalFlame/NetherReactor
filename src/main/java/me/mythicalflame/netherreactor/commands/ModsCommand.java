package me.mythicalflame.netherreactor.commands;

import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.content.Mod;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;
import static net.kyori.adventure.text.Component.text;

public final class ModsCommand
{
    public static void modsCommand(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("netherreactor.command.mods"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor mods command!</red>"));
            return;
        }

        if (args.length != 1)
        {
            sender.sendMessage(minimessage("<red>Incorrect argument count! Try: /netherreactor mods</red>"));
            return;
        }

        TextComponent.Builder result = text();
        result.append(minimessage("<gold>NetherReactor Registered Mods: </gold>"));
        if (NetherReactorModLoader.getRegisteredMods().isEmpty())
        {
            result.append(minimessage("None"));
        }
        else
        {
            for (int i = 0; i < NetherReactorModLoader.getRegisteredMods().size(); i++)
            {
                Mod mod = NetherReactorModLoader.getRegisteredMods().get(i);
                result.append(minimessage(mod.toString()));

                if (i < NetherReactorModLoader.getRegisteredMods().size() - 1)
                {
                    result.append(minimessage(", "));
                }
            }
        }

        sender.sendMessage(result.build());
    }
}
