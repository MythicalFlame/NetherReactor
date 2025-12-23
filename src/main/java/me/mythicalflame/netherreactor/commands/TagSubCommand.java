package me.mythicalflame.netherreactor.commands;

import com.mojang.brigadier.Command;
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
import me.mythicalflame.netherreactor.content.ModdedTag;
import me.mythicalflame.netherreactor.utilities.KeyPersistentDataType;
import me.mythicalflame.netherreactor.utilities.ModRegister;
import me.mythicalflame.netherreactor.utilities.NetherReactorAPI;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;
import static net.kyori.adventure.text.Component.text;

public class TagSubCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> generateCommand() {
        return
                Commands.literal("tag")
                        .executes(TagSubCommand::helpMessage)
                        .then(Commands.literal("inspect")
                                .then(Commands.literal("mainhand")
                                        .executes(TagSubCommand::tagInspectMainHandExecute)
                                )
                                .then(Commands.literal("lookup")
                                        .then(Commands.literal("material")
                                                .then(Commands.argument("material", StringArgumentType.word())
                                                        .suggests(TagSubCommand::getMaterialSuggestions)
                                                        .executes(TagSubCommand::tagInspectMaterialExecute)
                                                )
                                        )
                                        .then(Commands.literal("itemstack")
                                                .then(Commands.argument("stack", ArgumentTypes.itemStack())
                                                        .executes(TagSubCommand::tagInspectItemStackExecute))
                                        )
                                        .then(Commands.literal("custom")
                                                .then(Commands.argument("key", StringArgumentType.word())
                                                        .suggests(TagSubCommand::getItemSuggestions)
                                                        .executes(TagSubCommand::tagInspectCustomExecute))
                                        )
                                )
                        )
                        .then(Commands.literal("matches")
                                .then(Commands.argument("tag", StringArgumentType.word())
                                        .suggests(TagSubCommand::getTagSuggestions)
                                        .then(Commands.literal("mainhand")
                                                .executes(TagSubCommand::tagMatchesMainHandExecute)
                                        )
                                        .then(Commands.literal("lookup")
                                                .then(Commands.literal("material")
                                                        .then(Commands.argument("material", StringArgumentType.word())
                                                                .suggests(TagSubCommand::getMaterialSuggestions)
                                                                .executes(TagSubCommand::tagMatchesLookupMaterialExecute)
                                                        )
                                                )
                                                .then(Commands.literal("itemstack")
                                                        .then(Commands.argument("stack", ArgumentTypes.itemStack())
                                                                .executes(TagSubCommand::tagMatchesLookupItemStackExecute))
                                                )
                                                .then(Commands.literal("custom")
                                                        .then(Commands.argument("key", StringArgumentType.word())
                                                                .suggests(TagSubCommand::getItemSuggestions)
                                                                .executes(TagSubCommand::tagMatchesLookupCustomExecute))
                                                )
                                        )
                                )
                        );
    }

    /*
     *
     * INSPECT
     *
     */
    private static int tagInspectMainHandExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();

        if (!(executor instanceof Player player))
        {
            sender.sendMessage("<red>Only players may use the mainhand option!</red>");
            return Command.SINGLE_SUCCESS;
        }

        if (!sender.hasPermission("netherreactor.command.tag.inspect"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor tag inspect subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack mainhand = player.getInventory().getItemInMainHand();

        sendTags(sender, NetherReactorAPI.getApplicableTags(mainhand));

        return Command.SINGLE_SUCCESS;
    }

    private static int tagInspectMaterialExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.tag.inspect"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor tag inspect subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String materialInput = ctx.getArgument("material", String.class);
        Material material;
        try
        {
            material = Material.valueOf(materialInput.toUpperCase());
        }
        catch(IllegalArgumentException ignored)
        {
            sender.sendMessage(minimessage("<red>Could not find Material \"" + materialInput + "\"!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        sendTags(sender, NetherReactorAPI.getApplicableTags(material));

        return Command.SINGLE_SUCCESS;
    }

    private static int tagInspectItemStackExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.tag.inspect"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor tag inspect subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack stack = ctx.getArgument("stack", ItemStack.class);

        sendTags(sender, NetherReactorAPI.getApplicableTags(stack));

        return Command.SINGLE_SUCCESS;
    }

    private static int tagInspectCustomExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.tag.inspect"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor tag inspect subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String input = ctx.getArgument("key", String.class);
        Key keyCheck = stringToKey(input);
        if (keyCheck == null)
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        sendTags(sender, NetherReactorAPI.getApplicableTags(keyCheck));

        return Command.SINGLE_SUCCESS;
    }

    private static void sendTags(CommandSender sender, List<ModdedTag> tags)
    {
        TextComponent.Builder result = text();
        result.append(minimessage("<gold>Tags that match: </gold>"));
        if (tags.isEmpty())
        {
            result.append(minimessage("None"));
        }
        else
        {
            for (int i = 0; i < tags.size(); i++)
            {
                ModdedTag tag = tags.get(i);
                result.append(minimessage(tag.toString()));

                if (i < tags.size() - 1)
                {
                    result.append(minimessage(", "));
                }
            }
        }
        sender.sendMessage(result.build());
    }

    /*
     *
     * MATCHES
     *
     */
    private static int tagMatchesMainHandExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();

        if (!(executor instanceof Player player))
        {
            sender.sendMessage("<red>Only players may use the mainhand option!</red>");
            return Command.SINGLE_SUCCESS;
        }

        if (!sender.hasPermission("netherreactor.command.tag.matches"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor tag matches subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String input = ctx.getArgument("tag", String.class);
        Key key = stringToKey(input);
        if (key == null)
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        ModdedTag tag = ModRegister.getCachedTag(key);
        if (tag == null)
        {
            sender.sendMessage(minimessage("<red>Could not find tag \"" + input + "\"!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack mainhand = player.getInventory().getItemInMainHand();
        if (!mainhand.getPersistentDataContainer().has(NetherReactorModLoader.getItemKey()))
        {
            if (tag.isMember(mainhand.getType()))
            {
                sender.sendMessage(minimessage("<green>Key is in provided tag.</green>"));
            }
            else
            {
                sender.sendMessage(minimessage("<red>Key is not in provided tag.</red>"));
            }
            return Command.SINGLE_SUCCESS;
        }

        Key itemKey = mainhand.getPersistentDataContainer().get(NetherReactorModLoader.getItemKey(), KeyPersistentDataType.INSTANCE);
        ModdedItem itemFound = ModRegister.getCachedItem(itemKey);
        if (tag.isMember(itemFound))
        {
            sender.sendMessage(minimessage("<green>Key is in provided tag.</green>"));
        }
        else
        {
            sender.sendMessage(minimessage("<red>Key is not in provided tag.</red>"));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int tagMatchesLookupMaterialExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.tag.matches"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor tag matches subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String input = ctx.getArgument("tag", String.class);
        Key key = stringToKey(input);
        if (key == null)
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        ModdedTag tag = ModRegister.getCachedTag(key);
        if (tag == null)
        {
            sender.sendMessage(minimessage("<red>Could not find tag \"" + input + "\"!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String materialInput = ctx.getArgument("material", String.class);
        Material material;
        try
        {
            material = Material.valueOf(materialInput.toUpperCase());
        }
        catch(IllegalArgumentException ignored)
        {
            sender.sendMessage(minimessage("<red>Could not find Material \"" + materialInput + "\"!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        if (tag.isMember(material))
        {
            sender.sendMessage(minimessage("<green>Material is in provided tag.</green>"));
        }
        else
        {
            sender.sendMessage(minimessage("<red>Material is not in provided tag.</red>"));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int tagMatchesLookupItemStackExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.tag.matches"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor tag matches subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String input = ctx.getArgument("tag", String.class);
        Key key = stringToKey(input);
        if (key == null)
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        ModdedTag tag = ModRegister.getCachedTag(key);
        if (tag == null)
        {
            sender.sendMessage(minimessage("<red>Could not find tag \"" + input + "\"</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack stack = ctx.getArgument("stack", ItemStack.class);

        if (tag.isMember(stack))
        {
            sender.sendMessage(minimessage("<green>ItemStack is in provided tag.</green>"));
        }
        else
        {
            sender.sendMessage(minimessage("<red>ItemStack is not in provided tag.</red>"));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int tagMatchesLookupCustomExecute(CommandContext<CommandSourceStack> ctx)
    {
        CommandSender sender = ctx.getSource().getSender();

        if (!sender.hasPermission("netherreactor.command.tag.matches"))
        {
            sender.sendMessage(minimessage("<red>You do not have permission to use the netherreactor tag matches subcommand!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String input = ctx.getArgument("tag", String.class);
        Key key = stringToKey(input);
        if (key == null)
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        ModdedTag tag = ModRegister.getCachedTag(key);
        if (tag == null)
        {
            sender.sendMessage(minimessage("<red>Could not find tag \"" + input + "\"</red>"));
            return Command.SINGLE_SUCCESS;
        }

        String inputTwo = ctx.getArgument("key", String.class);
        Key keyCheck = stringToKey(inputTwo);
        if (keyCheck == null)
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        if (tag.isMember(keyCheck))
        {
            sender.sendMessage(minimessage("<green>Key is in provided tag.</green>"));
        }
        else
        {
            sender.sendMessage(minimessage("<red>Key is not in provided tag.</red>"));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int helpMessage(CommandContext<CommandSourceStack> ctx)
    {
        ctx.getSource().getSender().sendMessage(minimessage("<red>1. /netherreactor tag matches <tagKey> mainhand - Check if the item in your main hand matches a tag.\n2. /netherreactor tag matches <tagKey> lookup <material|itemstack|custom> <value> - Check if a specific value matches a tag.</red>"));
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> getTagSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder)
    {
        if (!ctx.getSource().getSender().hasPermission("netherreactor.command.mod.info.tags"))
        {
            return builder.buildFuture();
        }

        NetherReactorModLoader.getRegisteredMods().forEach(
                mod -> mod.getRegisteredTags().forEach(tag -> builder.suggest(tag.getKey().toString())));
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> getMaterialSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder)
    {
        Arrays.stream(Material.values()).forEach(material -> builder.suggest(material.toString()));
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

    private static Key stringToKey(String str)
    {
        String[] splitted = str.split(":");
        if (splitted.length != 2)
        {
            return null;
        }

        Key key;
        try
        {
            key = Key.key(splitted[0].toLowerCase(), splitted[1].toLowerCase());
        }
        catch (InvalidKeyException ignored)
        {
            return null;
        }

        return key;
    }
}
