package me.mythicalflame.netherreactor.utilities;

import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.content.ModdedItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
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
    public static ModdedItem getModdedItem(String namespace, String ID)
    {
        return ModRegister.getCachedItem(namespace + ":" + ID);
    }

    /**
     * Searches for a ModdedItem given its ItemStack representation.
     *
     * @param stack The ItemStack representation of the ModdedItem.
     * @return The ModdedItem found, or null if none were found.
     */
    @Nullable
    public static ModdedItem getModdedItem(@Nonnull ItemStack stack)
    {
        if (stack.getType() == Material.AIR)
        {
            return null;
        }

        if (stack.getPersistentDataContainer().has(NetherReactorModLoader.getContentKey()))
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
    public static ModdedItem getModdedItem(@Nonnull ItemStack stack, @Nonnull Collection<ModdedItem> collection)
    {
        if (stack.getType() == Material.AIR)
        {
            return null;
        }

        if (stack.getPersistentDataContainer().has(NetherReactorModLoader.getContentKey()))
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
    public static Mod getMod(String namespace)
    {
        return ModRegister.getCachedMod(namespace);
    }

    /**
     * Searches for a Mod given a ModdedItem object.
     *
     * @param moddedItem The ModdedItem to search with.
     * @return The Mod found, or null if none were found.
     */
    @Nullable
    public static Mod getMod(@Nonnull ModdedItem moddedItem)
    {
        String namespace = moddedItem.getNamespace();
        return ModRegister.getCachedMod(namespace);
    }
}
