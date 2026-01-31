package me.mythicalflame.netherreactor.api.content;

import net.kyori.adventure.key.Key;

/**
 * A class representing a modded statistic. Can currently only represent untyped statistics.
 * <p><strong>WARNING: If you add a statistic to the server, players without the Java Edition client support mod will not be able to see their statistics (due to a ProtocolLib bug), and Bukkit will throw an error whenever a custom statistic is changed.</strong></p>
 */
public class ModdedStatistic
{
    /**
     * The key of the statistic.
     */
    private final Key KEY;

    /**
     * Constructs a ModdedStatistic.
     * <p><strong>WARNING: If you add a statistic to the server, players without the Java Edition client support mod will not be able to see their statistics (due to a ProtocolLib bug), and Bukkit will throw an error whenever a custom statistic is changed.</strong></p>
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
