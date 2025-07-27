package me.mythicalflame.netherreactor.creative;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Represents a creative tab, for use in the NetherReactor creative inventory.
 */
public abstract class CreativeTab
{
    /**
     * The namespace of the creative tab. This must match your mod's namespace.
     */
    @Nonnull
    private final String NAMESPACE;
    /**
     * The ID of the creative tab.
     */
    @Nonnull
    private final String ID;
    /**
     * The priority of the creative tab. Lower priorities show up first in the creative inventory. Custom creative tabs should use positive priorities, to avoid messing with vanilla categories.
     */
    private final int PRIORITY;
    /**
     * The icon of the creative tab.
     */
    @Nonnull
    private final ItemStack ICON;
    /**
     * The component to use as the name of the creative tab.
     */
    @Nonnull
    private final Component NAME;

    /**
     * Constructs a creative tab object.
     *
     * @param namespace The namespace of this creative tab. This must match your mod's namespace.
     * @param ID The ID of this creative tab.
     * @param priority The priority of this creative tab. This should be positive.
     * @param icon The icon of this creative tab.
     * @param name The component used as the name for this creative tab.
     */
    public CreativeTab(@Nonnull String namespace, @Nonnull String ID, int priority, @Nonnull ItemStack icon, @Nonnull Component name)
    {
        this.NAMESPACE = namespace.toLowerCase();
        this.ID = ID.toLowerCase();
        this.PRIORITY = priority;
        this.ICON = icon;
        this.NAME = name;
    }

    /**
     * Gets the namespace of the creative tab.
     *
     * @return The namespace of this creative tab. Must match your mod's namespace.
     */
    @Nonnull
    public String getNamespace()
    {
        return NAMESPACE;
    }

    /**
     * Gets the ID of the creative tab.
     *
     * @return The ID of this creative tab.
     */
    @Nonnull
    public String getID()
    {
        return ID;
    }

    /**
     * Gets the priority of the creative tab.
     *
     * @return The priority of this creative tab. Vanilla creative tab priorities are negative, while custom ones should be positive.
     */
    public int getPriority()
    {
        return PRIORITY;
    }

    /**
     * Gets the icon of the creative tab.
     *
     * @return The icon of this creative tab.
     */
    @Nonnull
    public ItemStack getIcon()
    {
        return ICON;
    }

    /**
     * Gets the name of the creative tab.
     *
     * @return The component used as the name of this creative tab.
     */
    @Nonnull
    public Component getName()
    {
        return NAME;
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
        if (!(other instanceof CreativeTab otherTab))
        {
            return false;
        }

        return NAMESPACE.equals(otherTab.NAMESPACE) && ID.equals(otherTab.ID);
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
        return Arrays.hashCode(new Object[]{NAMESPACE, ID});
    }
}
