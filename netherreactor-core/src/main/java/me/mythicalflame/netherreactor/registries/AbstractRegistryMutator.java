package me.mythicalflame.netherreactor.registries;

public interface AbstractRegistryMutator
{
    void unfreezeRegistry() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException;
}
