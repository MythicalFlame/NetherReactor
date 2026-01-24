package me.mythicalflame.netherreactor.api;

import me.mythicalflame.netherreactor.core.registries.RegistryManager;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

public final class NetherReactorAPI
{
    private NetherReactorAPI() {}

    public static void awardStatistic(Player player, Key key, int amount)
    {
        RegistryManager.getStatisticMutator().awardStatistic(player, key, amount);
    }
}
