package me.mythicalflame.netherreactor.modules.vanilla;

import com.github.retrooper.packetevents.protocol.potion.PotionType;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import com.github.retrooper.packetevents.protocol.potion.StaticPotionType;
import com.github.retrooper.packetevents.protocol.stats.Statistic;
import com.github.retrooper.packetevents.protocol.stats.Statistics;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
import me.mythicalflame.netherreactor.registries.NetherReactorRegistry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PacketEventsInjector
{
    public static boolean inject()
    {
        boolean result;
        result = injectEffects();
        result |= injectStatistics();
        return result;
    }

    private static boolean injectEffects()
    {
        try
        {
            Field registryField = PotionTypes.class.getDeclaredField("REGISTRY");
            registryField.setAccessible(true);
            Field typeNamesField = VersionedRegistry.class.getDeclaredField("typeNames");
            typeNamesField.setAccessible(true);
            Field typeIdsField = VersionedRegistry.class.getDeclaredField("typeIds");
            typeIdsField.setAccessible(true);
            VersionedRegistry<PotionType> registry = (VersionedRegistry<PotionType>) registryField.get(null);

            HashMap<Key, StaticPotionType> injectedPotions = new HashMap<>(NetherReactorRegistry.Effects.getEffects().size());
            NetherReactorRegistry.Effects.getEffects().forEach((key, effect) -> {
                injectedPotions.put(key, new StaticPotionType(new CustomTypesBuilderData(new ResourceLocation(key), effect.getLeft())));
            });

            Map<String, PotionType>[] typeNames = (Map<String, PotionType>[]) typeNamesField.get(registry);
            for (Map<String, PotionType> typeNamesEntry : typeNames)
            {
                NetherReactorRegistry.Effects.getEffects().forEach((key, effect) ->
                        typeNamesEntry.put(key.toString(), injectedPotions.get(key)));
            }
            typeNamesField.set(registry, typeNames);

            Map<Integer, PotionType>[] typeIds = (Map<Integer, PotionType>[]) typeIdsField.get(registry);
            for (Map<Integer, PotionType> typeIdsEntry : typeIds)
            {
                NetherReactorRegistry.Effects.getEffects().forEach((key, effect) ->
                        typeIdsEntry.put(effect.getLeft(), injectedPotions.get(key)));
            }
            typeIdsField.set(registry, typeIds);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean injectStatistics()
    {
        try
        {
            Field statisticMapField = Statistics.class.getDeclaredField("STATISTIC_MAP");
            statisticMapField.setAccessible(true);
            Map<String, Statistic> statisticMap = (Map<String, Statistic>) statisticMapField.get(null);

            NetherReactorRegistry.Statistics.getStatisticNames().forEach(name -> statisticMap.put(name, new Statistic() {
                @Override
                public String getId() {
                    return name;
                }

                @Override
                public Component display() {
                    return Component.empty();
                }

                @Override
                public boolean equals(Object obj) {
                    if (obj instanceof Statistic) {
                        return ((Statistic) obj).getId().equals(this.getId());
                    }
                    return false;
                }
            }));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
