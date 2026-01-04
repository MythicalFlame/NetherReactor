package me.mythicalflame.netherreactor.core;

import me.mythicalflame.netherreactor.api.ModdedItem;

import java.util.Collection;

public interface AbstractItemRegistryMutator
{
    void registerItems(Collection<ModdedItem> items);
    void freezeItemRegistry();
}
