package me.mythicalflame.netherreactor.content;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;
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
     * A method intended to be overriden that determines whether a material is a member of the tag.
     * Intended for checking whether or not vanilla items are in the tag.
     *
     * @param material The material to check for membership.
     * @return Whether or not the material is a member of this tag.
     */
    public boolean isMember(Material material)
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
    public boolean isMember(ModdedItem item)
    {
        return false;
    }

    /**
     * A method intended to be overriden that determines whether a key is a member of the tag.
     * Intended for checking whether or not other custom items (Nexo, ItemsAdder, etc.) are in the tag.
     *
     * @param key The key to check for membership.
     * @return Whether or not the key is a member of this tag.
     */
    public boolean isMember(Key key)
    {
        return false;
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
