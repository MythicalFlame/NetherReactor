package me.mythicalflame.netherreactor.content;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A class representing a modded item.
 */
public class ModdedItem
{
    /**
     * The properties of the item.
     */
    @Nonnull
    private final ItemProperties ITEM_PROPERTIES;
    /**
     * The vanilla module settings for the item.
     */
    @Nonnull
    private final VanillaModuleSettings VANILLA_SETTINGS;

    /**
     * Constructs a ModdedItem with a stick disguise.
     *
     * @param itemProperties The properties of this item.
     */
    public ModdedItem(@Nonnull ItemProperties itemProperties)
    {
        this.ITEM_PROPERTIES = itemProperties;
        this.VANILLA_SETTINGS = new VanillaModuleSettings(Material.STICK);
    }

    /**
     * Constructs a ModdedItem.
     *
     * @param itemProperties The properties of this item.
     * @param vanillaSettings The vanilla module settings for this item.
     */
    public ModdedItem(@Nonnull ItemProperties itemProperties, @Nonnull VanillaModuleSettings vanillaSettings)
    {
        this.ITEM_PROPERTIES = itemProperties;
        this.VANILLA_SETTINGS = vanillaSettings;
    }

    /**
     * Gets the properties of the item.
     *
     * @return The properties of this item.
     */
    public @Nonnull ItemProperties getItemProperties()
    {
        return this.ITEM_PROPERTIES;
    }

    /**
     * Gets the vanilla module settings for the item.
     *
     * @return The vanilla module settings for this item.
     */
    public @Nonnull VanillaModuleSettings getVanillaSettings()
    {
        return this.VANILLA_SETTINGS;
    }

    /**
     * Holds the properties of a ModdedItem.
     */
    public static class ItemProperties
    {
        /**
         * The key of the item.
         */
        private final @Nonnull Key KEY;
        /**
         * The components of the item.
         */
        private final @Nonnull HashMap<Key, Object> COMPONENTS = new HashMap<>();

        /**
         * Creates an item properties container.
         *
         * @param key The key for this item.
         */
        public ItemProperties(@Nonnull Key key)
        {
            this.KEY = key;
        }

        /**
         * Gets the key of the item.
         *
         * @return The key of this item.
         */
        public @Nonnull Key getKey()
        {
            return this.KEY;
        }

        /**
         * Gets the components of the item.
         *
         * @return The components of this item.
         */
        public @Nonnull Map<Key, Object> getComponents()
        {
            return Collections.unmodifiableMap(this.COMPONENTS);
        }

        /**
         * Adds a valued component to the item.
         *
         * @param type The type of the component as a key.
         * @param value The value of the component. The type MUST match what Paper's API uses.
         * @return The same ItemProperties.
         */
        public @Nonnull ItemProperties setValuedComponent(@Nonnull Key type, @Nonnull Object value)
        {
            this.COMPONENTS.put(type, value);
            return this;
        }

        /**
         * Adds a non-valued component to the item.
         *
         * @param type The type of the component as a key.
         * @return The same ItemProperties.
         */
        public @Nonnull ItemProperties setNonValuedComponent(@Nonnull Key type)
        {
            this.COMPONENTS.put(type, null);
            return this;
        }
    }

    /**
     * Controls vanilla module behaviour for a ModdedItem.
     */
    public static class VanillaModuleSettings
    {
        /**
         * The Bukkit material to disguise as. Must not be air.
         */
        @Nonnull
        private final Material DISGUISE;

        /**
         * Creates a vanilla module settings container.
         *
         * @param material The Bukkit material to disguise as. Must not be air.
         */
        public VanillaModuleSettings(@Nonnull Material material)
        {
            if (material == Material.AIR)
            {
                throw new IllegalArgumentException("Tried to create an item with an air disguise!");
            }

            this.DISGUISE = material;
        }

        /**
         * Gets the disguise of the item.
         *
         * @return The disguise of this item.
         */
        public @Nonnull Material getDisguise()
        {
            return this.DISGUISE;
        }
    }
}
