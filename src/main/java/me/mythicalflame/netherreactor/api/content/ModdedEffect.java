package me.mythicalflame.netherreactor.api.content;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
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
     * The hex color of the particles for the effect.
     */
    private final int COLOR;
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
     * Gets the hex color of the particles for the effect.
     *
     * @return The hex color of the particles for this effect.
     */
    public int getColor()
    {
        return this.COLOR;
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
     * @return The same ModdedEffect (for chaining).
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
     * @param key The key (NOT the attribute) for this modifier.
     * @param value The amount for this modifier.
     * @param operation The operation for this modifier.
     * @return The same ModdedEffect (for chaining).
     */
    public ModdedEffect addAttributeModifier(Key attribute, NamespacedKey key, double value, AttributeModifier.Operation operation)
    {
        ATTRIBUTES.add(Map.entry(attribute, new AttributeModifier(key, value, operation)));
        return this;
    }
}
