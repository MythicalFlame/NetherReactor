package me.mythicalflame.netherreactor.internals.v1_21_8;

import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.content.ModdedStatistic;
import me.mythicalflame.netherreactor.registries.AbstractStatisticRegistryMutator;
import me.mythicalflame.netherreactor.registries.NetherReactorRegistry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.NoSuchElementException;
import java.util.Set;

public final class StatisticRegistryMutator_v1_21_8 implements AbstractStatisticRegistryMutator
{
    private MappedRegistry<ResourceLocation> STATISTICS;
    private static final HashMap<Key, ResourceLocation> REGISTERED_MINECRAFT_STATS = new HashMap<>();

    public void unfreezeRegistry() throws NoSuchFieldException, IllegalAccessException
    {
        STATISTICS = (MappedRegistry<ResourceLocation>) BuiltInRegistries.CUSTOM_STAT;

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(STATISTICS, false);
    }

    @Override
    public void registerStatistics(Collection<Mod> mods, ComponentLogger logger) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        unfreezeRegistry();

        for (Mod mod : mods)
        {
            for (ModdedStatistic moddedStatistic : mod.getRegisteredStatistics())
            {
                Key moddedStatisticKey = moddedStatistic.getKey();

                Field allTagsField = MappedRegistry.class.getDeclaredField("allTags");
                allTagsField.setAccessible(true);

                Field unregisteredIntrusiveHolders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
                unregisteredIntrusiveHolders.setAccessible(true);
                unregisteredIntrusiveHolders.set(STATISTICS, new IdentityHashMap<>());

                ResourceLocation minecraftLocation = ResourceLocation.fromNamespaceAndPath(moddedStatisticKey.namespace(), moddedStatisticKey.value());
                ResourceKey<ResourceLocation> resourceKey = ResourceKey.create(Registries.CUSTOM_STAT, minecraftLocation);

                allTagsField.set(STATISTICS, InternalInterface_v1_21_8.getUnboundMethod().invoke(null));

                STATISTICS.createIntrusiveHolder(minecraftLocation);
                Holder<ResourceLocation> holder = STATISTICS.register(resourceKey, minecraftLocation, RegistrationInfo.BUILT_IN);

                Set<TagKey<ResourceLocation>> tags = new HashSet<>();
                Holder.direct(minecraftLocation).tags().forEach(tags::add);

                Method bindMethod = Holder.Reference.class.getDeclaredMethod("bindTags", Collection.class);
                bindMethod.setAccessible(true);
                bindMethod.invoke(holder, tags);

                unregisteredIntrusiveHolders.set(STATISTICS, null);

                REGISTERED_MINECRAFT_STATS.put(moddedStatisticKey, minecraftLocation);

                NetherReactorRegistry.Statistics.add(moddedStatistic);

                logger.info("Registered statistic {} successfully!", moddedStatisticKey);
            }
        }

        STATISTICS.freeze();
    }

    @Override
    public void awardStatistic(org.bukkit.entity.Player paperPlayer, Key key, int amount)
    {
        ResourceLocation found = REGISTERED_MINECRAFT_STATS.get(key);
        if (found == null)
        {
            throw new NoSuchElementException("Cannot find custom statistic \"" + key + "\"!");
        }

        ((CraftPlayer) paperPlayer).getHandle().awardStat(found, amount);
    }
}
