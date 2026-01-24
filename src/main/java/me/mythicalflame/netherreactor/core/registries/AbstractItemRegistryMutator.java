package me.mythicalflame.netherreactor.core.registries;

import me.mythicalflame.netherreactor.api.content.Mod;

import java.util.Collection;

public interface AbstractItemRegistryMutator extends AbstractRegistryMutator
{
    void registerItems(Collection<Mod> mods);
}
