package me.mythicalflame.netherreactor.core;

import me.mythicalflame.netherreactor.core.modules.vanilla.VanillaSupportModule;
import org.bukkit.plugin.java.JavaPlugin;

public final class NetherReactorPlugin extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        VanillaSupportModule.activate(this);
        //EnderReactorModule.activate(this);
    }
}
