package me.mythicalflame.netherreactor;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.mythicalflame.netherreactor.commands.NetherReactorCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class NetherReactorBootstrap implements PluginBootstrap
{
    @Override
    public void bootstrap(BootstrapContext context)
    {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(NetherReactorCommand.generateCommand(), "NetherReactor Mod Loader main command.", List.of("nr"));
        });
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context)
    {
        return new NetherReactorModLoader();
    }
}
