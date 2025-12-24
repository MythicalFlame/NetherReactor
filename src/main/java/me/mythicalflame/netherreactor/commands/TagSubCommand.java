package me.mythicalflame.netherreactor.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import me.mythicalflame.netherreactor.content.ModdedTag;
import me.mythicalflame.netherreactor.utilities.MaterialArgument;
import me.mythicalflame.netherreactor.utilities.ModRegister;
import me.mythicalflame.netherreactor.utilities.NetherReactorAPI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.mythicalflame.netherreactor.utilities.Utilities.minimessage;
import static net.kyori.adventure.text.Component.text;

public class TagSubCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> generateCommand() {
        return
                Commands.literal("tag")
                        .executes(TagSubCommand::helpMessage)
                        .then(Commands.literal("matches")
                                .then(Commands.argument("tag", ArgumentTypes.key())
                                        .suggests(NetherReactorCommand::getTagSuggestions)
                                        .then(Commands.literal("mainhand")
                                                .executes(TagSubCommand::tagMatchesMainHandExecute)
                                        )
                                        .then(Commands.literal("lookup")
                                                .then(Commands.literal("material")
                                                        .then(Commands.argument("material", new MaterialArgument())
                                                                .executes(TagSubCommand::tagMatchesLookupMaterialExecute)
                                                        )
                                                )
                                                .then(Commands.literal("itemstack")
                                                        .then(Commands.argument("stack", ArgumentTypes.itemStack())
                                                                .executes(TagSubCommand::tagMatchesLookupItemStackExecute))
                                                )
                                                .then(Commands.literal("custom")
                                                        .then(Commands.argument("key", ArgumentTypes.key())
                                                                .suggests(NetherReactorCommand::getItemSuggestions)
                                                                .executes(TagSubCommand::tagMatchesLookupCustomExecute))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("inspect")
                                .then(Commands.literal("mainhand")
                                        .executes(TagSubCommand::tagInspectMainHandExecute)
                                )
                                .then(Commands.literal("lookup")
                                        .then(Commands.literal("material")
                                                .then(Commands.argument("material", new MaterialArgument())
                                                        .executes(TagSubCommand::tagInspectMaterialExecute)
                                                )
                                        )
                                        .then(Commands.literal("itemstack")
                                                .then(Commands.argument("stack", ArgumentTypes.itemStack())
                                                        .executes(TagSubCommand::tagInspectItemStackExecute))
                                        )
                                        .then(Commands.literal("custom")
                                                .then(Commands.argument("key", ArgumentTypes.key())
                                                        .suggests(NetherReactorCommand::getItemSuggestions)
                                                        .executes(TagSubCommand::tagInspectCustomExecute))
                                        )
                                )
                        );
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

        Key input = ctx.getArgument("tag", Key.class);
        if (input.namespace().equals("minecraft"))
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        ModdedTag tag = ModRegister.getCachedTag(input);
        if (tag == null)
        {
            sender.sendMessage(minimessage("<red>Could not find tag \"" + input + "\"!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        ItemStack mainhand = player.getInventory().getItemInMainHand();
        if (tag.isMember(mainhand))
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

        Key input = ctx.getArgument("tag", Key.class);
        if (input.namespace().equals("minecraft"))
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        ModdedTag tag = ModRegister.getCachedTag(input);
        if (tag == null)
        {
            sender.sendMessage(minimessage("<red>Could not find tag \"" + input + "\"!</red>"));
            return Command.SINGLE_SUCCESS;
        }

        Material material = ctx.getArgument("material", Material.class);

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

        Key input = ctx.getArgument("tag", Key.class);
        if (input.namespace().equals("minecraft"))
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        ModdedTag tag = ModRegister.getCachedTag(input);
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

        Key input = ctx.getArgument("tag", Key.class);
        if (input.namespace().equals("minecraft"))
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        ModdedTag tag = ModRegister.getCachedTag(input);
        if (tag == null)
        {
            sender.sendMessage(minimessage("<red>Could not find tag \"" + input + "\"</red>"));
            return Command.SINGLE_SUCCESS;
        }

        Key inputTwo = ctx.getArgument("key", Key.class);
        if (inputTwo.namespace().equals("minecraft"))
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        if (tag.isMember(inputTwo))
        {
            sender.sendMessage(minimessage("<green>Key is in provided tag.</green>"));
        }
        else
        {
            sender.sendMessage(minimessage("<red>Key is not in provided tag.</red>"));
        }

        return Command.SINGLE_SUCCESS;
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

        Material material = ctx.getArgument("material", Material.class);

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

        Key input = ctx.getArgument("key", Key.class);
        if (input.namespace().equals("minecraft"))
        {
            helpMessage(ctx);
            return Command.SINGLE_SUCCESS;
        }

        sendTags(sender, NetherReactorAPI.getApplicableTags(input));

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

    private static int helpMessage(CommandContext<CommandSourceStack> ctx)
    {
        ctx.getSource().getSender().sendMessage(minimessage("<red>1. /netherreactor tag matches <tagKey> mainhand - Check if the item in your main hand matches a tag.\n2. /netherreactor tag matches <tagKey> lookup <material|itemstack|custom> <value> - Check if a specific value matches a tag.\n3. /netherreactor tag inspect mainhand - Gives a list of tags that the item in your main hand are a part of.\n4. /netherreactor tag inspect lookup <material|itemstack|custom> <value> - Gives a list of tags that the specified value is a part of.</red>"));
        return Command.SINGLE_SUCCESS;
    }
}
