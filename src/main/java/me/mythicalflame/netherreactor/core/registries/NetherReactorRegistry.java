package me.mythicalflame.netherreactor.core.registries;

import me.mythicalflame.netherreactor.api.content.ModdedEffect;
import me.mythicalflame.netherreactor.api.content.ModdedStatistic;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.HashSet;

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

        public static boolean isEmpty()
        {
            return EFFECTS.isEmpty();
        }
    }

    public static final class Statistics
    {
        private static final HashMap<Key, ModdedStatistic> STATISTICS = new HashMap<>();
        private static final HashSet<String> STATISTIC_NAMES = new HashSet<>();

        public static ModdedStatistic get(Key key)
        {
            return STATISTICS.get(key);
        }

        public static boolean containsName(String name)
        {
            return STATISTIC_NAMES.contains(name);
        }

        public static void add(ModdedStatistic statistic)
        {
            STATISTICS.put(statistic.getKey(), statistic);
        }

        public static void addName(String name)
        {
            STATISTIC_NAMES.add(name);
        }

        public static boolean isEmpty()
        {
            return STATISTICS.isEmpty();
        }
    }
}
