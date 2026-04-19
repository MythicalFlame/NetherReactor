package me.mythicalflame.netherreactor.content;

import net.kyori.adventure.key.Key;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectTypeCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A class representing a modded effect.
 */
public class ModdedEffect
{
    /**
     * The key of the effect.
     */
    private final Key KEY;
    /**
     * The category of the effect.
     */
    private final PotionEffectTypeCategory CATEGORY;
    /**
     * The color of the particles for the effect.
     */
    private final Color COLOR;
    /**
     * The list of attribute modifiers for the effect.
     */
    private final List<Map.Entry<Key, AttributeModifier>> ATTRIBUTES = new ArrayList<>();

    /**
     * Constructs a ModdedEffect.
     *
     * @param key The key of this effect.
     * @param category The category of this effect.
     * @param color The hex color of the particles for this effect.
     */
    public ModdedEffect(Key key, PotionEffectTypeCategory category, int color)
    {
        this.KEY = key;
        this.CATEGORY = category;
        this.COLOR = Color.fromRGB(color);
    }

    /**
     * Constructs a ModdedEffect.
     *
     * @param key The key of this effect.
     * @param category The category of this effect.
     * @param color The color of the particles for this effect.
     */
    public ModdedEffect(Key key, PotionEffectTypeCategory category, Color color)
    {
        this.KEY = key;
        this.CATEGORY = category;
        this.COLOR = color;
    }

    /**
     * Gets the key of the effect.
     *
     * @return The key of this effect.
     */
    public Key getKey()
    {
        return this.KEY;
    }

    /**
     * Gets the category of the effect.
     *
     * @return The category of this effect.
     */
    public PotionEffectTypeCategory getCategory()
    {
        return this.CATEGORY;
    }

    /**
     * Gets the color of the particles for the effect.
     *
     * @return The color of the particles for this effect.
     */
    public Color getColor()
    {
        return this.COLOR;
    }

    /**
     * Gets the hex color of the particles for the effect.
     *
     * @return The hex color of the particles for this effect.
     */
    public int getColorInt()
    {
        return this.COLOR.asRGB();
    }

    /**
     * Gets the attribute modifiers for the effect.
     *
     * @return An immutable List of pairs of Keys (attributes) and AttributeModifiers.
     */
    public List<Map.Entry<Key, AttributeModifier>> getAttributes()
    {
        return Collections.unmodifiableList(ATTRIBUTES);
    }

    /**
     * Adds an attribute modifier to the effect.
     *
     * @param attribute The attribute key for this modifier.
     * @param modifier The modifier to add.
     * @return The same ModdedEffect.
     */
    public ModdedEffect addAttributeModifier(Key attribute, AttributeModifier modifier)
    {
        ATTRIBUTES.add(Map.entry(attribute, modifier));
        return this;
    }

    /**
     * Adds an attribute modifier to the effect.
     *
     * @param attribute The attribute key for this modifier.
     * @param key The key for this modifier.
     * @param value The amount for this modifier.
     * @param operation The operation for this modifier.
     * @return The same ModdedEffect.
     */
    public ModdedEffect addAttributeModifier(Key attribute, NamespacedKey key, double value, AttributeModifier.Operation operation)
    {
        ATTRIBUTES.add(Map.entry(attribute, new AttributeModifier(key, value, operation)));
        return this;
    }

    /**
     * Controls whether or not vanilla clients can see that this effect was removed.
     *
     * @param player The player that is receiving the effect.
     * @return Whether or not to display that this effect was removed.
     */
    public boolean displayRemoved(Player player)
    {
        return true;
    }

    /**
     * Controls whether or not vanilla clients can see that this effect was updated. If it returns false, they will not see that it was removed either.
     *
     * @param player The player that is receiving the effect.
     * @param amplifier The amplifier of the effect.
     * @param duration The duration of the effect in ticks.
     * @param ambient Whether or not the effect is ambient.
     * @param showParticles Whether or not the effect creates particles.
     * @param showIcon Whether or not the effect's icon shows.
     * @return Whether or not to display that this effect was updated.
     */
    public boolean displayUpdated(Player player, int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon)
    {
        return showParticles || showIcon;
    }
}
