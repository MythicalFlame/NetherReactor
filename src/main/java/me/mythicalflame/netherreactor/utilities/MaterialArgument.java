package me.mythicalflame.netherreactor.utilities;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.concurrent.CompletableFuture;

public class MaterialArgument implements CustomArgumentType.Converted<Material, String>
{
    private static final DynamicCommandExceptionType ERROR_INVALID_MATERIAL = new DynamicCommandExceptionType(
            material -> MessageComponentSerializer.message().serialize(Component.text(material + " is not a valid material!")));

    @Override
    public Material convert(String nativeType) throws CommandSyntaxException
    {
        try
        {
            return Material.valueOf(nativeType.toUpperCase());
        }
        catch (IllegalArgumentException ignored)
        {
            throw ERROR_INVALID_MATERIAL.create(nativeType);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        for (Material material : Material.values())
        {
            String name = material.toString().toUpperCase();

            if (name.startsWith(builder.getRemaining().toUpperCase()))
            {
                builder.suggest(material.toString());
            }
        }

        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType()
    {
        return StringArgumentType.word();
    }
}
