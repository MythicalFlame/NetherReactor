package me.mythicalflame.netherreactor.content;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

/**
 * A class representing a modded statistic. Can currently only represent untyped statistics.
 */
@NullMarked
public class ModdedStatistic
{
    /**
     * The key of the statistic.
     */
    private final Key KEY;

    /**
     * Constructs a ModdedStatistic.
     *
     * @param key The key of this statistic.
     */
    public ModdedStatistic(Key key)
    {
        this.KEY = key;
    }

    /**
     * Gets the key of the statistic.
     *
     * @return The key of this statistic.
     */
    public Key getKey()
    {
        return this.KEY;
    }
}
