package me.mythicalflame.netherreactor.content;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;

/**
 * A tag implementation with built-in sets that allow other plugins to add to your tag.
 */
public abstract class ExpandableTag extends ModdedTag
{
    /**
     * A set of Materials that are a member of the tag.
     */
    @Nonnull
    private final HashSet<Material> MATERIAL_SET = new HashSet<>();
    /**
     * A set of ModdedItems that are a member of the tag.
     */
    @Nonnull
    private final HashSet<ModdedItem> MODDED_ITEM_SET = new HashSet<>();
    /**
     * A set of Keys that are a member of the tag.
     */
    @Nonnull
    private final HashSet<Key> KEY_SET = new HashSet<>();

    /**
     * Constructs a ModdedTag object.
     *
     * @param key The non-null key containing the namespace and ID of the tag. The namespace must match your mod's namespace.
     */
    public ExpandableTag(@Nonnull Key key)
    {
        super(key);
    }

    /**
     * Intended for checking whether or not vanilla items are in the tag.
     *
     * @param material The material to check for membership.
     * @return Whether or not the material is a member of this tag.
     */
    public boolean isMember(@Nullable Material material)
    {
        return MATERIAL_SET.contains(material);
    }

    /**
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

        return MODDED_ITEM_SET.contains(item) || isMember(item.getMaterial());
    }

    /**
     * Intended for checking whether or not other custom items (Nexo, ItemsAdder, etc.) are in the tag.
     *
     * @param key The key to check for membership.
     * @return Whether or not the key is a member of this tag.
     */
    public boolean isMember(@Nullable Key key)
    {
        return KEY_SET.contains(key) || super.isMember(key);
    }

    /**
     * Adds a Material as a member of the tag.
     *
     * @param material The Material to add to this tag.
     */
    public void addMaterial(@Nonnull Material material)
    {
        MATERIAL_SET.add(material);
    }

    /**
     * Adds a ModdedItem as a member of the tag.
     *
     * @param item The ModdedItem to add to this tag.
     */
    public void addModdedItem(@Nonnull ModdedItem item)
    {
        MODDED_ITEM_SET.add(item);
    }
    /**
     * Adds a Key as a member of the tag.
     *
     * @param key The Key to add to this tag.
     */
    public void addKey(@Nonnull Key key)
    {
        KEY_SET.add(key);
    }
}
