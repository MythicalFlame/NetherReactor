package me.mythicalflame.netherreactor.core.registries;

import me.mythicalflame.netherreactor.api.content.Mod;

import java.util.Collection;

public interface AbstractEffectRegistryMutator extends AbstractRegistryMutator
{
    void registerEffects(Collection<Mod> mods);
}
