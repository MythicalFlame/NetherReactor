package me.mythicalflame.netherreactor.utilities;

import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.content.ModdedItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.Collection;

public final class NetherReactorAPI
{
    /**
     * Searches for a ModdedItem given its properties.
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

        return ModRegister.getCachedItem(namespace + ":" + ID);
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

        if (!stack.getPersistentDataContainer().has(NetherReactorModLoader.getContentKey()))
        {
            return null;
        }

        String stackContent = stack.getPersistentDataContainer().get(NetherReactorModLoader.getContentKey(), PersistentDataType.STRING);
        return ModRegister.getCachedItem(stackContent);
    }

    /**
     * Searches for a ModdedItem within a collection.
     *
     * @param stack The ItemStack representation of the ModdedItem.
     * @param collection The collection to search.
     * @return The ModdedItem found, or null if none were found.
     */
    @Nullable
    public static ModdedItem getModdedItem(@Nullable ItemStack stack, @Nullable Collection<ModdedItem> collection)
    {
        if (stack == null || collection == null)
        {
            return null;
        }

        if (stack.getType() == Material.AIR)
        {
            return null;
        }

        if (!stack.getPersistentDataContainer().has(NetherReactorModLoader.getContentKey()))
        {
            return null;
        }

        String stackContent = stack.getPersistentDataContainer().get(NetherReactorModLoader.getContentKey(), PersistentDataType.STRING);
        ModdedItem item = ModRegister.getCachedItem(stackContent);
        if (item == null)
        {
            return null;
        }
        if (collection.contains(item))
        {
            return item;
        }
        else
        {
            return null;
        }
    }

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

        String namespace = moddedItem.getNamespace();
        return ModRegister.getCachedMod(namespace);
    }
}
