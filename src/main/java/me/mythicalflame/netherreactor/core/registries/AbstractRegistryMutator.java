package me.mythicalflame.netherreactor.core.registries;

public interface AbstractRegistryMutator
{
    void unfreezeRegistry() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException;
}
