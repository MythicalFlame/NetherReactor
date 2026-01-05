package me.mythicalflame.netherreactor.core;

import me.mythicalflame.netherreactor.internals.v1_21_8.ItemRegistryMutator_v1_21_8;
import org.bukkit.plugin.java.JavaPlugin;

public final class NetherReactorPlugin extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        new ItemRegistryMutator_v1_21_8().freezeItemRegistry();
    }
}
