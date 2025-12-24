package me.mythicalflame.netherreactor.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.content.ModdedTag;
import me.mythicalflame.netherreactor.creative.CreativeTab;
import me.mythicalflame.netherreactor.utilities.ModRegister;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;
import static net.kyori.adventure.text.Component.text;

public final class ModSubCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> generateCommand() {
        return
                Commands.literal("mod")
                        .executes(ModSubCommand::helpMessage)
                        .then(Commands.literal("info")
                                .then(Commands.argument("mod", StringArgumentType.word())
                                        .suggests(NetherReactorCommand::getModSuggestions)
                                        .then(Commands.literal("items")
                                                .executes(ModSubCommand::modInfoItemsExecute))
                                        .then(Commands.literal("tags")
                                                .executes(ModSubCommand::modInfoTagsExecute))
                                        .then(Commands.literal("creativetabs")
                                                .executes(ModSubCommand::modInfoCreativeTabsExecute))
                                )
                        )
                        .then(Commands.literal("list")
                                .executes(ModSubCommand::modListExecute)
                        );
    }

    private static int modInfoItemsExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.mod.info.items"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor mod info items subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String input = ctx.getArgument("mod", String.class);
        Mod mod = ModRegister.getCachedMod(input);

        if (mod == null)
        {
            sender.sendMessage(minimessage("<red>Could not find mod with namespace \"" + input + "\"</red>"));
            return Command.SINGLE_SUCCESS;
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

                result.append(minimessage(item.getKey().namespace() + ":" + item.getKey().value()));

                if (i < mod.getRegisteredItems().size() - 1)
                {
                    result.append(minimessage(", "));
                }
            }
        }

        sender.sendMessage(result.build());

        return Command.SINGLE_SUCCESS;
    }

    private static int modInfoCreativeTabsExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.mod.info.creativetabs"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor mod info creativetabs subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String input = ctx.getArgument("mod", String.class);
        Mod mod = ModRegister.getCachedMod(input);

        if (mod == null)
        {
            sender.sendMessage(minimessage("<red>Could not find mod with namespace \"" + input + "\"</red>"));
            return Command.SINGLE_SUCCESS;
        }

        TextComponent.Builder result = text();
        result.append(minimessage("<gold>NetherReactor Mod Inspection Results:</gold>\nName: " + mod.getDisplayName() + "\nNamespace: " + mod.getNamespace()));

        result.append(minimessage("\nRegistered Creative Tabs: "));
        if (mod.getRegisteredCreativeTabs().isEmpty())
        {
            result.append(minimessage("None"));
        }
        else
        {
            for (int i = 0; i < mod.getRegisteredCreativeTabs().size(); ++i)
            {
                CreativeTab tab = mod.getRegisteredCreativeTabs().get(i);

                result.append(tab.getName());

                if (i < mod.getRegisteredItems().size() - 1)
                {
                    result.append(minimessage(", "));
                }
            }
        }

        sender.sendMessage(result.build());

        return Command.SINGLE_SUCCESS;
    }

    private static int modInfoTagsExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.mod.info.tags"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor mod info tags subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String input = ctx.getArgument("mod", String.class);
        Mod mod = ModRegister.getCachedMod(input);

        if (mod == null)
        {
            sender.sendMessage(minimessage("<red>Could not find mod with namespace \"" + input + "\"</red>"));
            return Command.SINGLE_SUCCESS;
        }

        TextComponent.Builder result = text();
        result.append(minimessage("<gold>NetherReactor Mod Inspection Results:</gold>\nName: " + mod.getDisplayName() + "\nNamespace: " + mod.getNamespace()));

        result.append(minimessage("\nRegistered Tags: "));
        if (mod.getRegisteredTags().isEmpty())
        {
            result.append(minimessage("None"));
        }
        else
        {
            for (int i = 0; i < mod.getRegisteredTags().size(); ++i)
            {
                ModdedTag tag = mod.getRegisteredTags().get(i);

                result.append(minimessage(tag.toString()));

                if (i < mod.getRegisteredTags().size() - 1)
                {
                    result.append(minimessage(", "));
                }
            }
        }

        sender.sendMessage(result.build());

        return Command.SINGLE_SUCCESS;
    }

    private static int modListExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.mod.list"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor mod list subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
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

        return Command.SINGLE_SUCCESS;
    }

    private static int helpMessage(CommandContext<CommandSourceStack> ctx)
    {
        ctx.getSource().getSender().sendMessage(minimessage("<red>1. /netherreactor mod info <mod> <items|tags|creativetabs> - Lists specific information about a mod.\n2. /netherreactor mod list - Lists all registered mods.</red>"));
        return Command.SINGLE_SUCCESS;
    }
}
