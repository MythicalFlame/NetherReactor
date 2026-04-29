package me.mythicalflame.netherreactor.registries;

import me.mythicalflame.netherreactor.content.Mod;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public interface AbstractStatisticRegistryMutator
{
    void registerStatistics(Collection<Mod> mods, ComponentLogger logger) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
    void awardStatistic(Player player, Key key, int amount);
}
