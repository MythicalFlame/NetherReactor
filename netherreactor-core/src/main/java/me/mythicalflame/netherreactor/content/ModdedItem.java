package me.mythicalflame.netherreactor.content;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
         * The item to transform into after being used as a crafting ingredient.
         * If this is null, the item disappears when used as a crafting ingredient.
         * The item to transform into must be added to the mod before this item.
         * As a result, you cannot have recursive craft remainders or have an item transform into itself.
         */
        private @Nullable Key CRAFT_REMAINDER = null;
        /**
         * The length of time in ticks this item burns for as a fuel. A time of 0 means it is not a fuel.
         */
        private int FUEL_TIME = 0;
        /**
         * The chance of the item being composted. A chance of 0 means uncompostable.
         */
        private float COMPOSTING_CHANCE = 0.0f;

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
         * Gets the item to transform into after being used as a crafting ingredient.
         *
         * @return The item that this transforms into after being used as a crafting ingredient.
         */
        public @Nullable Key getCraftRemainder()
        {
            return this.CRAFT_REMAINDER;
        }

        /**
         * Gets the length of time in ticks this item burns for as a fuel. A time of 0 means it is not a fuel.
         *
         * @return The length of time in ticks this item burns for as a fuel.
         */
        public int getFuelTime()
        {
            return this.FUEL_TIME;
        }

        /**
         * Gets the chance of the item being composted. A value of 0 means it cannot be composted.
         *
         * @return The chance of this item being composted.
         */
        public float getCompostingChance()
        {
            return this.COMPOSTING_CHANCE;
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

        /**
         * Sets the item to transform into after being used as a crafting ingredient.
         *
         * @param remainder The item to transform into.
         *                  If this is null, the item disappears.
         *                  The item to transform into must be added to the mod before this item.
         *                  As a result, if you want recursion or an item that transforms into itself, you must use events.
         * @return The same ItemProperties.
         */
        public @Nonnull ItemProperties setCraftRemainder(@Nullable Key remainder)
        {
            if (KEY.equals(remainder))
            {
                throw new IllegalArgumentException("Cannot create an item that has itself as a craft remainder!");
            }
            this.CRAFT_REMAINDER = remainder;
            return this;
        }

        /**
         * Sets the length of time the item burns as a fuel.
         *
         * @param time The length of time, in ticks. A time of 0 disables using this item as fuel.
         * @return The same ItemProperties.
         */
        public @Nonnull ItemProperties setFuelTime(int time)
        {
            if (time < 0)
            {
                throw new IllegalArgumentException("Cannot create an item with a fuel burn time of " + time + "!");
            }
            this.FUEL_TIME = time;
            return this;
        }

        /**
         * Sets the chance of the item being composted.
         *
         * @param chance The chance of being composted. Must be between 0.0f and 1.0f inclusive. A chance of 0 disables composting.
         * @return The same ItemProperties.
         */
        public @Nonnull ItemProperties setCompostingChance(float chance)
        {
            if (chance < 0.0f || chance > 1.0f)
            {
                throw new IllegalArgumentException("Cannot create an item with a composting chance of " + chance + "!");
            }
            this.COMPOSTING_CHANCE = chance;
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
