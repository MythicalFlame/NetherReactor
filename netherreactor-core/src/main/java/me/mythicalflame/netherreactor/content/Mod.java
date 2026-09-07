package me.mythicalflame.netherreactor.content;

import org.jspecify.annotations.NullMarked;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a set of content to add to the game.
 */
@NullMarked
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
    /**
     * A list of ModdedEffects this mod will use.
     */
    private final ArrayList<ModdedEffect> EFFECTS = new ArrayList<>();
    /**
     * A list of ModdedItems this mod will use.
     */
    private final ArrayList<ModdedItem> ITEMS = new ArrayList<>();
    /**
     * A list of ModdedBlocks this mod will use.
     */
    private final ArrayList<ModdedBlock> BLOCKS = new ArrayList<>();
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
     * Adds a ModdedItem to the mod.
     *
     * @param item The item to add.
     */
    public void addItem(ModdedItem item)
    {
        ITEMS.add(item);
    }

    /**
     * Adds a ModdedBlock to the mod.
     *
     * @param block The block to add.
     */
    public void addBlock(ModdedBlock block)
    {
        BLOCKS.add(block);
    }

    /**
     * Adds a ModdedStatistic to the mod.
     *
     * @param statistic The statistic to add.
     */
    public void addStatistic(ModdedStatistic statistic)
    {
        STATISTICS.add(statistic);
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
     * Gets a list of registered ModdedItems.
     *
     * @return An immutable List of ModdedItems.
     */
    public List<ModdedItem> getRegisteredItems()
    {
        return Collections.unmodifiableList(ITEMS);
    }

    /**
     * Gets a list of registered ModdedBlocks.
     *
     * @return An immutable List of ModdedBlocks.
     */
    public List<ModdedBlock> getRegisteredBlocks()
    {
        return Collections.unmodifiableList(BLOCKS);
    }

    /**
     * Gets a list of registered ModdedStatistics.
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
