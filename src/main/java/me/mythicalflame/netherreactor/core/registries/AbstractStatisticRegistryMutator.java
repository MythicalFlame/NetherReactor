package me.mythicalflame.netherreactor.core.registries;

import me.mythicalflame.netherreactor.api.content.Mod;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface AbstractStatisticRegistryMutator extends AbstractRegistryMutator
{
    void registerStatistics(Collection<Mod> mods);
    void awardStatistic(Player player, Key key, int amount);
}
