package me.mythicalflame.netherreactor.registries;

import me.mythicalflame.netherreactor.content.Mod;

import java.util.Collection;

public interface AbstractItemRegistryMutator extends AbstractRegistryMutator
{
    void registerItems(Collection<Mod> mods);
}
