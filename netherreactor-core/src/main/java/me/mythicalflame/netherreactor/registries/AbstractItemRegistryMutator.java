package me.mythicalflame.netherreactor.registries;

import me.mythicalflame.netherreactor.content.Mod;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public interface AbstractItemRegistryMutator
{
    void registerItems(Collection<Mod> mods, ComponentLogger logger) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException;
    Key getMaterialKey(ItemStack stack);
}
