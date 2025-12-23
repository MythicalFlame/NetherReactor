package me.mythicalflame.netherreactor.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.utilities.ModRegister;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;

public final class GiveSubCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> generateCommand() {
        return
                Commands.literal("give")
                        .executes(GiveSubCommand::helpMessage)
                        .then(Commands.argument("target", ArgumentTypes.player())
                                .then(Commands.argument("key", StringArgumentType.word())
                                        .suggests(GiveSubCommand::getItemSuggestions)
                                        .executes(ctx -> giveExecute(ctx, 1))
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                                .executes(GiveSubCommand::giveExecuteArgument))));
    }

    private static int giveExecuteArgument(CommandContext<CommandSourceStack> ctx)
    {
        int amount = ctx.getArgument("amount", Integer.class);
        return giveExecute(ctx, amount);
    }

    private static int giveExecute(CommandContext<CommandSourceStack> ctx, int amount)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.give"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor give subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        Player receiver = ctx.getArgument("target", Player.class);

        String input = ctx.getArgument("key", String.class);
        String[] splitted = input.split(":");
        if (splitted.length != 2)
        {
            return helpMessage(ctx);
        }

        ModdedItem itemFound;
        try
        {
            itemFound = ModRegister.getCachedItem(Key.key(splitted[0].toLowerCase(), splitted[1].toLowerCase()));
        }
        catch (InvalidKeyException ignored)
        {
            return helpMessage(ctx);
        }

        if (itemFound == null)
        {
            sender.sendMessage(minimessage("<red>Could not find item \"" + input + "\"!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack giveItem = itemFound.getItemStack(amount);
        receiver.getInventory().addItem(giveItem);
        sender.sendMessage(minimessage("Gave " + amount + " [" + itemFound.getItemName() + "] to " + receiver.getName()));

        return Command.SINGLE_SUCCESS;
    }

    private static int helpMessage(CommandContext<CommandSourceStack> ctx)
    {
        ctx.getSource().getSender().sendMessage(minimessage("<red>/netherreactor give <playerName> <itemNamespace:itemID> [amount] - Gives an item to the specified player.</red>"));
        return Command.SINGLE_SUCCESS;
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
