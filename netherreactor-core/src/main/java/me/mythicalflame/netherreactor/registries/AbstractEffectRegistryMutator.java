package me.mythicalflame.netherreactor.registries;

import me.mythicalflame.netherreactor.content.Mod;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public interface AbstractEffectRegistryMutator
{
    void registerEffects(Collection<Mod> mods, ComponentLogger logger) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException;
}
