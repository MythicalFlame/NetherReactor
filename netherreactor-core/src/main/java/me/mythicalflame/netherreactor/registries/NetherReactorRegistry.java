package me.mythicalflame.netherreactor.registries;

import me.mythicalflame.netherreactor.content.ModdedEffect;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.content.ModdedStatistic;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.HashSet;

public final class NetherReactorRegistry
{
    public static final class Items
    {
        private static final HashMap<Key, Pair<Integer, ModdedItem>> KEY_ITEMS = new HashMap<>();
        private static final HashMap<Integer, ModdedItem> ID_ITEMS = new HashMap<>();

        public static HashMap<Integer, ModdedItem> getItemsByIds()
        {
            return ID_ITEMS;
        }

        public static Pair<Integer, ModdedItem> getByKey(Key key)
        {
            return KEY_ITEMS.get(key);
        }

        public static ModdedItem getById(int id)
        {
            return ID_ITEMS.get(id);
        }

        public static void add(int id, ModdedItem item)
        {
            KEY_ITEMS.put(item.getItemProperties().getKey(), Pair.of(id, item));
            ID_ITEMS.put(id, item);
        }

        public static boolean isEmpty()
        {
            return ID_ITEMS.isEmpty();
        }
    }

    public static final class Effects
    {
        private static final HashMap<Key, Pair<Integer, ModdedEffect>> EFFECTS = new HashMap<>();

        public static HashMap<Key, Pair<Integer, ModdedEffect>> getEffects()
        {
            return EFFECTS;
        }

        public static Pair<Integer, ModdedEffect> get(Key key)
        {
            return EFFECTS.get(key);
        }

        public static void add(int id, ModdedEffect effect)
        {
            EFFECTS.put(effect.getKey(), Pair.of(id, effect));
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

        public static HashSet<String> getStatisticNames()
        {
            return STATISTIC_NAMES;
        }

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
            STATISTIC_NAMES.add("minecraft.custom:" + statistic.getKey().namespace() + "." + statistic.getKey().value());
        }

        public static boolean isEmpty()
        {
            return STATISTICS.isEmpty();
        }
    }
}
