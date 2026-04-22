package me.mythicalflame.netherreactor;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Miscellaneous API methods for NetherReactor.
 */
public final class NetherReactorAPI
{
    private NetherReactorAPI() {}

    /**
     * Awards a custom statistic to a player.
     *
     * @param player The player to award the statistic to.
     * @param key The key of the statistic.
     * @param amount The value to add to the statistic.
     */
    public static void awardStatistic(@Nonnull Player player, @Nonnull Key key, int amount)
    {
        InternalsManager.getStatisticMutator().awardStatistic(player, key, amount);
    }
}
