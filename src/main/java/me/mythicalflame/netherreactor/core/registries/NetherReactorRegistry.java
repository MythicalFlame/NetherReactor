package me.mythicalflame.netherreactor.core.registries;

import me.mythicalflame.netherreactor.api.content.ModdedEffect;
import me.mythicalflame.netherreactor.api.content.ModdedStatistic;
import net.kyori.adventure.key.Key;

import java.util.HashMap;

public final class NetherReactorRegistry
{
    //TODO items
    public static final class Effects
    {
        private static final HashMap<Key, ModdedEffect> EFFECTS = new HashMap<>();

        public static ModdedEffect get(Key key)
        {
            return EFFECTS.get(key);
        }

        public static void add(ModdedEffect effect)
        {
            EFFECTS.put(effect.getKey(), effect);
        }
    }

    public static final class Statistics
    {
        private static final HashMap<Key, ModdedStatistic> STATISTICS = new HashMap<>();

        public static ModdedStatistic get(Key key)
        {
            return STATISTICS.get(key);
        }

        public static void add(ModdedStatistic statistic)
        {
            STATISTICS.put(statistic.getKey(), statistic);
        }
    }
}
