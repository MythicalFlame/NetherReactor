package me.mythicalflame.netherreactor.api;

import me.mythicalflame.netherreactor.core.registries.InternalsManager;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

public final class NetherReactorAPI
{
    private NetherReactorAPI() {}

    public static void awardStatistic(Player player, Key key, int amount)
    {
        InternalsManager.getStatisticMutator().awardStatistic(player, key, amount);
    }
}
