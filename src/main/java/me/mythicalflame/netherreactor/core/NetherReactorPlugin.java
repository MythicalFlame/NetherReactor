package me.mythicalflame.netherreactor.core;

import me.mythicalflame.netherreactor.core.modules.enderreactor.EnderReactorModule;
import me.mythicalflame.netherreactor.core.modules.vanilla.VanillaSupportModule;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class NetherReactorPlugin extends JavaPlugin
{
    private static Logger LOGGER;

    @Override
    public void onEnable()
    {
        LOGGER = getLogger();

        VanillaSupportModule.activate(this);
        EnderReactorModule.activate(this);
    }

    public static Logger getLoggerStatic()
    {
        return LOGGER;
    }
}
