package me.mythicalflame.netherreactor.content;

import me.mythicalflame.netherreactor.creative.CreativeTab;
import me.mythicalflame.netherreactor.utilities.Version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * This class represents a game modification.
 */
public final class Mod
{
    //TODO assets registry
    /**
     * The namespace that the mod belongs to. This should be unique such that no other mod tries to use this namespace.
     */
    @Nonnull
    private final String namespace;
    /**
     * The display name of the mod.
     */
    @Nonnull
    private final String displayName;
    /**
     * The API version that this mod is based off of.
     */
    @Nonnull
    private final Version APIVersion;
    /**
     * The optional version of this mod.
     */
    @Nullable
    private final Version modVersion;
    /**
     * The custom items that the mod uses.
     */
    @Nonnull
    private final ArrayList<ModdedItem> registeredItems = new ArrayList<>();
    /**
     * The custom tags that the mod uses.
     */
    @Nonnull
    private final ArrayList<ModdedTag> registeredTags = new ArrayList<>();
    /**
     * The creative tabs that the mod uses.
     */
    @Nonnull
    private final ArrayList<CreativeTab> creativeTabs = new ArrayList<>();

    /**
     * Constructs a mod without a specific mod version.
     *
     * @param namespace The namespace that this mod belongs to. This should be unique such that no other mod tries to use this namespace.
     * @param displayName The display name of this mod.
     * @param APIVersion The API version that this mod is based off of.
     */
    public Mod(@Nonnull String namespace, @Nonnull String displayName, @Nonnull Version APIVersion)
    {
        this.namespace = namespace.toLowerCase();
        this.displayName = displayName;
        this.APIVersion = APIVersion;
        this.modVersion = null;
    }

    /**
     * Constructs a mod with a specific mod version.
     *
     * @param namespace The namespace that this mod belongs to. This should be unique such that no other mod tries to use this namespace.
     * @param displayName The display name of this mod.
     * @param APIVersion The API version that this mod is based off of.
     * @param modVersion The version of this mod. Optional field.
     */
    public Mod(@Nonnull String namespace, @Nonnull String displayName, @Nonnull Version APIVersion, @Nullable Version modVersion)
    {
        this.namespace = namespace.toLowerCase();
        this.displayName = displayName;
        this.APIVersion = APIVersion;
        this.modVersion = modVersion;
    }

    /**
     * Gets the namespace of the mod.
     *
     * @return The namespace that this mod belongs to.
     */
    @Nonnull
    public String getNamespace() { return namespace; }

    /**
     * Gets the display name of the mod.
     *
     * @return The display name of this mod.
     */
    @Nonnull
    public String getDisplayName() { return displayName; }

    /**
     * Gets the API version of the mod.
     *
     * @return The API version this mod is based off of.
     */
    @Nonnull
    public Version getAPIVersion()
    {
        return APIVersion;
    }

    /**
     * Gets the version of the mod.
     *
     * @return The version of this mod.
     */
    @Nullable
    public Version getModVersion()
    {
        return modVersion;
    }

    /**
     * Gets the items registered with this mod.
     *
     * @return An ArrayList of the ModdedItems registered in this mod.
     */
    @Nonnull
    public ArrayList<ModdedItem> getRegisteredItems() { return registeredItems; }

    /**
     * Gets the tags registered with this mod.
     *
     * @return An ArrayList of the Tags registered in this mod.
     */
    @Nonnull
    public ArrayList<ModdedTag> getRegisteredTags() { return registeredTags; }

    /**
     * Gets the creative tabs registered with this mod.
     *
     * @return An ArrayList of the CreativeTabs registered in this mod.
     */
    @Nonnull
    public ArrayList<CreativeTab> getCreativeTabs()
    {
        return creativeTabs;
    }

    /**
     * Overrides Object#toString.
     *
     * @return Returns the display name with the namespace in parentheses, and the mod version if one was specified.
     */
    @Override
    public String toString()
    {
        if (modVersion == null)
        {
            return displayName + " (" + namespace + ")";
        }
        else
        {
            return displayName + " (" + namespace + ") " + modVersion;
        }
    }

    /**
     * Registers a custom item with the mod.
     *
     * @param item The custom item to register.
     *
     * @throws IllegalArgumentException If the namespace of this mod and the item do not match.
     */
    public void registerItem(@Nonnull ModdedItem item)
    {
        if (!namespace.equals(item.getKey().namespace()))
        {
            throw new IllegalArgumentException("Attempted to register an item to a mod, but the namespaces do not match (" + item.getKey().namespace() + " vs. " + namespace + ").");
        }
        registeredItems.add(item);
    }

    /**
     * Registers a custom tag with the mod.
     *
     * @param tag The custom tag to register.
     *
     * @throws IllegalArgumentException If the namespace of this mod and the tag do not match.
     */
    public void registerTag(@Nonnull ModdedTag tag)
    {
        if (!namespace.equals(tag.getKey().namespace()))
        {
            throw new IllegalArgumentException("Attempted to register a tag to a mod, but the namespaces do not match (" + tag.getKey().namespace() + " vs. " + namespace + ").");
        }
        registeredTags.add(tag);
    }

    /**
     * Registers a creative tab with the mod.
     *
     * @param tab The creative tab to register.
     *
     * @throws IllegalArgumentException If the namespace of this mod and the creative tab do not match.
     */
    public void registerCreativeTab(@Nonnull CreativeTab tab)
    {
        if (!namespace.equals(tab.getKey().namespace()))
        {
            throw new IllegalArgumentException("Attempted to register a creative tab to a mod, but the namespaces do not match (" + tab.getKey().namespace() + " vs. " + namespace + ").");
        }

        creativeTabs.add(tab);
    }
}