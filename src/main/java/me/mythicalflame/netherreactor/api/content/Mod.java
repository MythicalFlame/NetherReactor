package me.mythicalflame.netherreactor.api.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a set of content to add to the game.
 */
public class Mod
{
    /**
     * The namespace of the mod. May only contain lowercase letters, digits, underscores, periods, and hyphens.
     */
    private final String NAMESPACE;
    /**
     * The version of the mod as a string. The suggested format is "major.minor.patch", such as "1.5.3", but you may also use any string like "beta".
     */
    private final String VERSION;
    private final ArrayList<ModdedItem> ITEMS = new ArrayList<>();
    /**
     * A list of ModdedEffects this mod will use.
     */
    private final ArrayList<ModdedEffect> EFFECTS = new ArrayList<>();
    /**
     * A list of ModdedStatistics this mod will use.
     */
    private final ArrayList<ModdedStatistic> STATISTICS = new ArrayList<>();

    /**
     * Constructs a Mod object.
     *
     * @param namespace The namespace of this mod. May only contain lowercase letters, digits, underscores, periods, and hyphens.
     * @param version The version of this mod as a string. The suggested format is "major.minor.patch", such as "1.5.3", but you may also use any string like "beta".
     */
    public Mod(String namespace, String version)
    {
        if (!namespace.matches("^[a-z0-9_.-]*$"))
        {
            throw new IllegalArgumentException("Mod namespace \"" + namespace + "\" contains illegal characters!");
        }

        this.NAMESPACE = namespace;
        this.VERSION = version;
    }

    public void addItem(ModdedItem item)
    {
        ITEMS.add(item);
    }

    /**
     * Adds a ModdedEffect to the mod.
     *
     * @param effect The effect to add.
     */
    public void addEffect(ModdedEffect effect)
    {
        EFFECTS.add(effect);
    }

    /**
     * Adds a ModdedStatistic to the mod.
     * <p><strong>WARNING: If you add a statistic to the server, players without the Java Edition client support mod will not be able to see their statistics (due to a ProtocolLib bug), and Bukkit will throw an error whenever a custom statistic is changed.</strong></p>
     *
     * @param statistic The statistic to add.
     */
    public void addStatistic(ModdedStatistic statistic)
    {
        STATISTICS.add(statistic);
    }

    public List<ModdedItem> getRegisteredItems()
    {
        return Collections.unmodifiableList(ITEMS);
    }

    /**
     * Gets a list of registered ModdedEffects.
     *
     * @return An immutable List of ModdedEffects.
     */
    public List<ModdedEffect> getRegisteredEffects()
    {
        return Collections.unmodifiableList(EFFECTS);
    }

    /**
     * Gets a list of registered ModdedStatistics.
     * <p><strong>WARNING: If you add a statistic to the server, players without the Java Edition client support mod will not be able to see their statistics (due to a ProtocolLib bug), and Bukkit will throw an error whenever a custom statistic is changed.</strong></p>
     *
     * @return An immutable List of ModdedStatistics.
     */
    public List<ModdedStatistic> getRegisteredStatistics()
    {
        return Collections.unmodifiableList(STATISTICS);
    }

    /**
     * Returns a string in the form "NAMESPACE:VERSION" for the mod.
     *
     * @return The string representing this mod.
     */
    @Override
    public String toString()
    {
        return NAMESPACE + ":" + VERSION;
    }
}
