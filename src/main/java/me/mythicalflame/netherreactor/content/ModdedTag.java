package me.mythicalflame.netherreactor.content;

import me.mythicalflame.netherreactor.utilities.ModRegister;
import me.mythicalflame.netherreactor.utilities.NetherReactorAPI;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * This class represents a custom tag.
 */
public abstract class ModdedTag
{
    /**
     * The non-null key containing the namespace and ID of the tag. The namespace must match your mod's namespace.
     */
    @Nonnull
    private final Key KEY;

    /**
     * Constructs a ModdedTag object.
     *
     * @param key The non-null key containing the namespace and ID of the tag. The namespace must match your mod's namespace.
     */
    public ModdedTag(@Nonnull Key key)
    {
        this.KEY = key;
    }

    /**
     * A method intended to be overriden that determines whether a Material is a member of the tag.
     * Intended for checking whether or not vanilla items are in the tag.
     *
     * @param material The Material to check for membership.
     * @return Whether or not the Material is a member of this tag.
     */
    public boolean isMember(@Nullable Material material)
    {
        return false;
    }

    /**
     * A method intended to be overriden that determines whether a ModdedItem is a member of the tag.
     * Intended for checking whether or not NetherReactor items are in the tag.
     *
     * @param item The item to check for membership.
     * @return Whether or not the item is a member of this tag.
     */
    public boolean isMember(@Nullable ModdedItem item)
    {
        if (item == null)
        {
            return false;
        }

        return isMember(item.getMaterial());
    }

    /**
     * A method intended to be overriden that determines whether a Key is a member of the tag.
     * Intended for checking whether or not other custom items (Nexo, ItemsAdder, etc.) are in the tag.
     *
     * @param key The Key to check for membership.
     * @return Whether or not the Key is a member of this tag.
     */
    public boolean isMember(@Nullable Key key)
    {
        if (key == null)
        {
            return false;
        }

        ModdedItem item = ModRegister.getCachedItem(key);
        if (item != null)
        {
            return isMember(key);
        }

        return false;
    }

    /**
     * A method intended to be overriden that determines whether an ItemStack is a member of the tag.
     * Intended for checking whether or not an item has specific characteristics (e.g. enchantments).
     *
     * @param stack The ItemStack to check for membership.
     * @return Whether or not the ItemStack is a member of this tag.
     */
    public boolean isMember(@Nullable ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }

        ModdedItem moddedItem = NetherReactorAPI.getModdedItem(stack);
        if (moddedItem != null)
        {
            return isMember(moddedItem);
        }
        else
        {
            return isMember(stack.getType());
        }
    }

    /**
     * Gets the namespace and ID of the tag as a NamespacedKey.
     *
     * @return A Key with the namespace and ID of this tag.
     */
    @Nonnull
    public Key getKey()
    {
        return KEY;
    }

    /**
     * Returns the String representation of the tag in the format "#namespace:id".
     *
     * @return The String representation of this tag.
     */
    @Override
    public String toString()
    {
        return KEY.namespace() + ":" + KEY.value();
    }

    /**
     * This method checks if two objects are equal.
     *
     * @param other The object to compare to.
     * @return Whether the two objects are equal.
     */
    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ModdedTag otherItem))
        {
            return false;
        }

        return KEY.equals(otherItem.KEY);
    }

    /**
     * Overrides Object#hashCode.
     * <p><a href="https://stackoverflow.com/a/16377941">Credit</a></p>
     *
     * @return The hashcode for this object.
     */
    @Override
    public int hashCode()
    {
        return Arrays.hashCode(new Object[]{KEY});
    }
}
