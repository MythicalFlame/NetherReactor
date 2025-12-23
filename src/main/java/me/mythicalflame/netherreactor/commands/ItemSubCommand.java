package me.mythicalflame.netherreactor.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.utilities.KeyPersistentDataType;
import me.mythicalflame.netherreactor.utilities.ModRegister;
import me.mythicalflame.netherreactor.utilities.NetherReactorAPI;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;

public class ItemSubCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> generateCommand() {
        return
                Commands.literal("item")
                        .executes(ItemSubCommand::helpMessage)
                        .then(Commands.literal("inspect")
                                .then(Commands.literal("mainhand")
                                        .executes(ItemSubCommand::itemInspectExecuteMainHand)
                                )
                                .then(Commands.literal("lookup")
                                        .then(Commands.argument("key", StringArgumentType.word())
                                                .suggests(ItemSubCommand::getItemSuggestions)
                                                .executes(ItemSubCommand::itemInspectExecuteArgument)
                                        )
                                )
                        );
    }

    private static int itemInspectExecuteMainHand(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();

        if (!(executor instanceof Player player))
        {
            sender.sendMessage("<red>Only players may use the mainhand option!</red>");
            return Command.SINGLE_SUCCESS;
        }

        if (!sender.hasPermission("netherreactor.command.item.inspect"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor item inspect subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack mainhand = player.getInventory().getItemInMainHand();
        if (!mainhand.getPersistentDataContainer().has(NetherReactorModLoader.getItemKey()))
        {
            sender.sendMessage(minimessage("<yellow>The item in your main hand is not a NetherReactor custom item.</yellow>"));
            return Command.SINGLE_SUCCESS;
        }

        Key key = mainhand.getPersistentDataContainer().get(NetherReactorModLoader.getItemKey(), KeyPersistentDataType.INSTANCE);
        ModdedItem itemFound = ModRegister.getCachedItem(key);
        if (itemFound == null)
        {
            sender.sendMessage(minimessage("<yellow>The item in your main hand is not a NetherReactor custom item.</yellow>"));
        }

        return itemInspectExecute(ctx, itemFound);
    }

    private static int itemInspectExecuteArgument(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.item.inspect"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor item inspect subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String input = ctx.getArgument("key", String.class);
        String[] splitted = input.split(":");
        if (splitted.length != 2)
        {
            return helpMessage(ctx);
        }

        Key key;
        try
        {
            key = Key.key(splitted[0].toLowerCase(), splitted[1].toLowerCase());
        }
        catch (InvalidKeyException ignored)
        {
            return helpMessage(ctx);
        }

        ModdedItem itemFound = ModRegister.getCachedItem(key);
        if (itemFound == null)
        {
            sender.sendMessage(minimessage("<red>Could not find item \"" + input + "\"!</red>"));
        }

        return itemInspectExecute(ctx, itemFound);
    }

    private static int itemInspectExecute(CommandContext<CommandSourceStack> ctx, ModdedItem item)
    {
        CommandSender sender = ctx.getSource().getSender();

        sender.sendMessage(minimessage("<gold>NetherReactor Item Inspection Results:</gold>\nMod: " + NetherReactorAPI.getMod(item).getDisplayName() + "\nName: " + item.getItemName() + "\nTechnical Name: " + item.getKey().toString() + "\nMaterial: " + item.getMaterial()));

        return Command.SINGLE_SUCCESS;
    }

    private static int helpMessage(CommandContext<CommandSourceStack> ctx)
    {
        ctx.getSource().getSender().sendMessage(minimessage("<red>1. /netherreactor item inspect mainhand - Shows details about the custom item in your main hand.\n2. /netherreactor item inspect lookup <itemNamespace:itemID> - Shows information about a specific item.</red>"));
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> getModSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder)
    {
        if (!ctx.getSource().getSender().hasPermission("netherreactor.command.mod.list"))
        {
            return builder.buildFuture();
        }

        NetherReactorModLoader.getRegisteredMods().forEach(mod -> builder.suggest(mod.getNamespace()));
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> getItemSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder)
    {
        if (!ctx.getSource().getSender().hasPermission("netherreactor.command.mod.info.items"))
        {
            return builder.buildFuture();
        }

        NetherReactorModLoader.getRegisteredMods().forEach(
                mod -> mod.getRegisteredItems().forEach(item -> builder.suggest(item.getKey().toString())));
        return builder.buildFuture();
    }
}
