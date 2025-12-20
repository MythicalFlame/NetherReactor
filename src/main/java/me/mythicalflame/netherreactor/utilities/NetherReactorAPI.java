package me.mythicalflame.netherreactor.utilities;

import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.content.ModdedTag;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class NetherReactorAPI
{
    /**
     * Searches for a Mod given a namespace.
     *
     * @param namespace The namespace to search with.
     * @return The Mod found, or null if none were found.
     */
    @Nullable
    public static Mod getMod(@Nullable String namespace)
    {
        if (namespace == null)
        {
            return null;
        }

        return ModRegister.getCachedMod(namespace);
    }

    /**
     * Searches for a Mod given a ModdedItem object.
     *
     * @param moddedItem The ModdedItem to search with.
     * @return The Mod found, or null if none were found.
     */
    @Nullable
    public static Mod getMod(@Nullable ModdedItem moddedItem)
    {
        if (moddedItem == null)
        {
            return null;
        }

        String namespace = moddedItem.getKey().namespace();
        return ModRegister.getCachedMod(namespace);
    }

    /**
     * Searches for a ModdedItem given its namespace and ID.
     *
     * @param namespace The namespace of the item.
     * @param ID The ID of the item.
     * @return The ModdedItem found, or null if none were found.
     */
    @Nullable
    public static ModdedItem getModdedItem(@Nullable String namespace, @Nullable String ID)
    {
        if (namespace == null || ID == null)
        {
            return null;
        }

        return ModRegister.getCachedItem(Key.key(namespace.toLowerCase(), ID.toLowerCase()));
    }

    /**
     * Searches for a ModdedItem given its key.
     *
     * @param key The key of the item.
     * @return The ModdedItem found, or null if none were found.
     */
    @Nullable
    public static ModdedItem getModdedItem(@Nullable Key key)
    {
        if (key == null)
        {
            return null;
        }

        return ModRegister.getCachedItem(key);
    }

    /**
     * Searches for a ModdedItem given its ItemStack representation.
     *
     * @param stack The ItemStack representation of the ModdedItem.
     * @return The ModdedItem found, or null if none were found.
     */
    @Nullable
    public static ModdedItem getModdedItem(@Nullable ItemStack stack)
    {
        if (stack == null)
        {
            return null;
        }

        if (stack.getType() == Material.AIR)
        {
            return null;
        }

        if (!stack.getPersistentDataContainer().has(NetherReactorModLoader.getItemKey()))
        {
            return null;
        }

        Key stackContent = stack.getPersistentDataContainer().get(NetherReactorModLoader.getItemKey(), KeyPersistentDataType.INSTANCE);
        return ModRegister.getCachedItem(stackContent);
    }

    /**
     * Searches for a ModdedTag given its key.
     *
     * @param key The key of the tag.
     * @return The ModdedTag found, or null if none were found.
     */
    @Nullable
    public static ModdedTag getTag(@Nullable Key key)
    {
        return ModRegister.getCachedTag(key);
    }

    /**
     * Searches for a list of all ModdedTags that a Material is a member of.
     *
     * @param material The Material to search with.
     * @return The ModdedTags found
     */
    @Nonnull
    public static List<ModdedTag> getApplicableTags(@Nullable Material material)
    {
        List<ModdedTag> results = new ArrayList<>();

        if (material == null)
        {
            return results;
        }

        for (Mod mod : NetherReactorModLoader.getRegisteredMods())
        {
            for (ModdedTag tag : mod.getRegisteredTags())
            {
                if (tag.isMember(material))
                {
                    results.add(tag);
                }
            }
        }
        return results;
    }

    /**
     * Searches for a list of all ModdedTags that a ModdedItem is a member of.
     *
     * @param item The ModdedItem to search with.
     * @return The ModdedTags found
     */
    @Nonnull
    public static List<ModdedTag> getApplicableTags(@Nullable ModdedItem item)
    {
        List<ModdedTag> results = new ArrayList<>();

        if (item == null)
        {
            return results;
        }

        for (Mod mod : NetherReactorModLoader.getRegisteredMods())
        {
            for (ModdedTag tag : mod.getRegisteredTags())
            {
                if (tag.isMember(item))
                {
                    results.add(tag);
                }
            }
        }
        return results;
    }

    /**
     * Searches for a list of all ModdedTags that a Key is a member of.
     * Useful for finding the tags that a non-NetherReactor custom item (Nexo, ItemsAdder, etc.) belongs to.
     *
     * @param key The Key to search with.
     * @return The ModdedTags found
     */
    @Nonnull
    public static List<ModdedTag> getApplicableTags(@Nullable Key key)
    {
        List<ModdedTag> results = new ArrayList<>();

        if (key == null)
        {
            return results;
        }

        for (Mod mod : NetherReactorModLoader.getRegisteredMods())
        {
            for (ModdedTag tag : mod.getRegisteredTags())
            {
                if (tag.isMember(key))
                {
                    results.add(tag);
                }
            }
        }
        return results;
    }
}
