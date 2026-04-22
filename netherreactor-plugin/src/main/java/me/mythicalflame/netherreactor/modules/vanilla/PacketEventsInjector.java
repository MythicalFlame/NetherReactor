package me.mythicalflame.netherreactor.modules.vanilla;

import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.item.type.StaticItemType;
import com.github.retrooper.packetevents.protocol.potion.PotionType;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import com.github.retrooper.packetevents.protocol.potion.StaticPotionType;
import com.github.retrooper.packetevents.protocol.stats.Statistic;
import com.github.retrooper.packetevents.protocol.stats.Statistics;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.mythicalflame.netherreactor.registries.NetherReactorRegistry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PacketEventsInjector
{
    public static boolean inject(boolean ignoreItems, boolean ignoreEffects)
    {
        boolean result = false;
        if (!ignoreItems)
        {
            result = injectItems();
        }
        if (!ignoreEffects)
        {
            result |= injectEffects();
        }
        //result |= injectStatistics();
        return result;
    }

    private static boolean injectItems()
    {
        try
        {
            Field registryField = ItemTypes.class.getDeclaredField("REGISTRY");
            registryField.setAccessible(true);
            Field typeNamesField = VersionedRegistry.class.getDeclaredField("typeNames");
            typeNamesField.setAccessible(true);
            Field typeIdsField = VersionedRegistry.class.getDeclaredField("typeIds");
            typeIdsField.setAccessible(true);
            VersionedRegistry<ItemType> registry = (VersionedRegistry<ItemType>) registryField.get(null);

            HashMap<Key, StaticItemType> injectedItems = new HashMap<>(NetherReactorRegistry.Items.getItemsByIds().size());
            NetherReactorRegistry.Items.getItemsByIds().forEach((id, item) -> {
                int maxAmount = 64;
                Object maxStackSizeFound = item.getItemProperties().getComponents().get(Key.key("minecraft", "max_stack_size"));
                if (maxStackSizeFound != null)
                {
                    maxAmount = (int) maxStackSizeFound;
                }
                int maxDurability = 1;
                Object maxDurabilityFound = item.getItemProperties().getComponents().get(Key.key("minecraft", "max_damage"));
                if (maxDurabilityFound != null)
                {
                    maxDurability = (int) maxDurabilityFound;
                }
                injectedItems.put(item.getItemProperties().getKey(), new StaticItemType(
                    new CustomTypesBuilderData(new ResourceLocation(item.getItemProperties().getKey()), id),
                    maxAmount,
                    maxDurability,
                    null,
                    null,
                    new HashSet<>()));
            });

            Map<String, ItemType>[] typeNames = (Map<String, ItemType>[]) typeNamesField.get(registry);
            for (Map<String, ItemType> typeNamesEntry : typeNames)
            {
                NetherReactorRegistry.Items.getItemsByIds().forEach((id, item) ->
                        typeNamesEntry.put(item.getItemProperties().getKey().toString(), injectedItems.get(item.getItemProperties().getKey())));
            }
            typeNamesField.set(registry, typeNames);

            Map<Integer, ItemType>[] typeIds = (Map<Integer, ItemType>[]) typeIdsField.get(registry);
            for (Map<Integer, ItemType> typeIdsEntry : typeIds)
            {
                NetherReactorRegistry.Items.getItemsByIds().forEach((id, item) ->
                        typeIdsEntry.put(id, injectedItems.get(item.getItemProperties().getKey())));
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
            NetherReactorRegistry.Effects.getEffects().forEach((key, effect) ->
                    injectedPotions.put(key, new StaticPotionType(new CustomTypesBuilderData(new ResourceLocation(key), effect.getLeft()))));

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
