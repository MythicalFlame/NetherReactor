package me.mythicalflame.netherreactor.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.utilities.ModRegister;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;

public final class GiveSubCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> generateCommand() {
        return
                Commands.literal("give")
                        .executes(GiveSubCommand::helpMessage)
                        .then(Commands.argument("target", ArgumentTypes.player())
                                .then(Commands.argument("key", ArgumentTypes.key())
                                        .suggests(NetherReactorCommand::getItemSuggestions)
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

        Player receiver;
        try
        {
            receiver = ctx.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
        }
        catch (CommandSyntaxException ignored)
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        Key input = ctx.getArgument("key", Key.class);
        if (input.namespace().equals("minecraft"))
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        ModdedItem itemFound = ModRegister.getCachedItem(input);

        if (itemFound == null)
        {
            sender.sendMessage(minimessage("<red>Could not find item \"" + input + "\"!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack giveItem = itemFound.getItemStack(amount);
        receiver.getInventory().addItem(giveItem);
        sender.sendMessage(minimessage("Gave " + amount + " [").append(itemFound.getItemName()).append(minimessage("] to " + receiver.getName())));

        return Command.SINGLE_SUCCESS;
    }

    private static int helpMessage(CommandContext<CommandSourceStack> ctx)
    {
        ctx.getSource().getSender().sendMessage(minimessage("<red>/netherreactor give <playerName> <itemNamespace:itemID> [amount] - Gives an item to the specified player.</red>"));
        return Command.SINGLE_SUCCESS;
    }
}
