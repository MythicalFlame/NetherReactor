package me.mythicalflame.netherreactor.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.utilities.KeyPersistentDataType;
import me.mythicalflame.netherreactor.utilities.ModRegister;
import me.mythicalflame.netherreactor.utilities.NetherReactorAPI;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
                                        .then(Commands.argument("key", ArgumentTypes.key())
                                                .suggests(NetherReactorCommand::getItemSuggestions)
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
            return Command.SINGLE_SUCCESS;
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

        return itemInspectExecute(ctx, itemFound);
    }

    private static int itemInspectExecute(CommandContext<CommandSourceStack> ctx, ModdedItem item)
    {
        CommandSender sender = ctx.getSource().getSender();

        sender.sendMessage(minimessage("<gold>NetherReactor Item Inspection Results:</gold>\nMod: " + NetherReactorAPI.getMod(item).getDisplayName() + "\nName: ").append(item.getItemName()).append(minimessage("\nTechnical Name: " + item.getKey().toString() + "\nMaterial: " + item.getMaterial())));

        return Command.SINGLE_SUCCESS;
    }

    private static int helpMessage(CommandContext<CommandSourceStack> ctx)
    {
        ctx.getSource().getSender().sendMessage(minimessage("<red>1. /netherreactor item inspect mainhand - Shows details about the custom item in your main hand.\n2. /netherreactor item inspect lookup <itemNamespace:itemID> - Shows information about a specific item.</red>"));
        return Command.SINGLE_SUCCESS;
    }
}
