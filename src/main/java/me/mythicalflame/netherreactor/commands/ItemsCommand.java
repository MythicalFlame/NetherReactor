package me.mythicalflame.netherreactor.commands;

import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.content.Mod;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;
import static net.kyori.adventure.text.Component.text;

public final class ItemsCommand
{
    public static void itemsCommand(CommandSender sender, String[] args)
    {
        if (!sender.hasPermission("netherreactor.command.items"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor items command!</red>"));
            return;
        }

        if (args.length != 1)
        {
            sender.sendMessage(minimessage("<red>Incorrect argument count! Try: /netherreactor items</red>"));
            return;
        }

        TextComponent.Builder result = text();
        result.append(minimessage("<gold>NetherReactor Registered Items: </gold>"));

        boolean hasItems = false;
        for (int i = 0; i < NetherReactorModLoader.getRegisteredMods().size(); i++)
        {
            Mod mod = NetherReactorModLoader.getRegisteredMods().get(i);

            for (int j = 0; j < mod.getRegisteredItems().size(); j++)
            {
                hasItems = true;
                ModdedItem item = mod.getRegisteredItems().get(j);
                result.append(minimessage(item.getNamespace() + ":" + item.getID()));

                if (i < NetherReactorModLoader.getRegisteredMods().size() - 1 || j < mod.getRegisteredItems().size() - 1)
                {
                    result.append(minimessage(", "));
                }
            }
        }

        if (!hasItems)
        {
            result.append(minimessage("None"));
        }

        sender.sendMessage(result.build());
    }
}
