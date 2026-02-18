package me.mythicalflame.netherreactor.core.registries;

import me.mythicalflame.netherreactor.internals.v1_21_8.EffectRegistryMutator_v1_21_8;
import me.mythicalflame.netherreactor.internals.v1_21_8.InternalInterface_v1_21_8;
import me.mythicalflame.netherreactor.internals.v1_21_8.ItemRegistryMutator_v1_21_8;
import me.mythicalflame.netherreactor.internals.v1_21_8.StatisticRegistryMutator_v1_21_8;

public final class InternalsManager
{
    private static AbstractInternalInterface internalInterface = null;
    private static AbstractItemRegistryMutator itemMutator = null;
    private static AbstractEffectRegistryMutator effectMutator = null;
    private static AbstractStatisticRegistryMutator statisticMutator = null;

    private InternalsManager() {}

    public static AbstractInternalInterface getInternalInterface()
    {
        if (internalInterface == null)
        {
            internalInterface = new InternalInterface_v1_21_8();
        }

        return internalInterface;
    }

    public static AbstractItemRegistryMutator getItemMutator()
    {
        if (itemMutator == null)
        {
            itemMutator = new ItemRegistryMutator_v1_21_8();
        }

        return itemMutator;
    }

    public static AbstractEffectRegistryMutator getEffectMutator()
    {
        if (effectMutator == null)
        {
            effectMutator = new EffectRegistryMutator_v1_21_8();
        }

        return effectMutator;
    }

    public static AbstractStatisticRegistryMutator getStatisticMutator()
    {
        if (statisticMutator == null)
        {
            statisticMutator = new StatisticRegistryMutator_v1_21_8();
        }

        return statisticMutator;
    }
}
