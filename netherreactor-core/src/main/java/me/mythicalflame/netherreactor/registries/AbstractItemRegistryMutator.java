package me.mythicalflame.netherreactor.registries;

import me.mythicalflame.netherreactor.content.Mod;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface AbstractItemRegistryMutator
{
    void registerItems(Collection<Mod> mods);
    Key getMaterialKey(ItemStack stack);
}
