package me.mythicalflame.netherreactor.commands;

import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.creative.CreativeTab;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.utilities.ModRegister;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;
import static net.kyori.adventure.text.Component.text;

public final class ModSubCommand
{
    public static void modSubCommand(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("netherreactor.command.netherreactor.mod"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor mod command!</red>"));
            return;
        }

        if (args.length != 2)
        {
            sender.sendMessage(minimessage("<red>Incorrect argument count! Try: /netherreactor mod <modNamespace></red>"));
            return;
        }

        Mod mod = ModRegister.getCachedMod(args[1]);

        if (mod == null)
        {
            sender.sendMessage(minimessage("<red>Could not find mod with namespace \"" + args[1] + "\"</red>"));
            return;
        }

        TextComponent.Builder result = text();
        result.append(minimessage("<gold>NetherReactor Mod Inspection Results:</gold>\nName: " + mod.getDisplayName() + "\nNamespace: " + mod.getNamespace() + "\nRegistered Items: "));
        if (mod.getRegisteredItems().isEmpty())
        {
            result.append(minimessage("None"));
        }
        else
        {
            for (int i = 0; i < mod.getRegisteredItems().size(); ++i)
            {
                ModdedItem item = mod.getRegisteredItems().get(i);

                result.append(minimessage(item.getNamespace() + ":" + item.getID()));

                if (i < mod.getRegisteredItems().size() - 1)
                {
                    result.append(minimessage(", "));
                }
            }
        }

        result.append(minimessage("\nRegistered Creative Tabs: "));
        if (mod.getCreativeTabs().isEmpty())
        {
            result.append(minimessage("None"));
        }
        else
        {
            for (int i = 0; i < mod.getCreativeTabs().size(); ++i)
            {
                CreativeTab tab = mod.getCreativeTabs().get(i);

                result.append(tab.getName());

                if (i < mod.getRegisteredItems().size() - 1)
                {
                    result.append(minimessage(", "));
                }
            }
        }

        sender.sendMessage(result.build());
    }
}
