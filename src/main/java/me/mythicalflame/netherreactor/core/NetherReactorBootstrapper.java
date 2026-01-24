package me.mythicalflame.netherreactor.core;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.mythicalflame.netherreactor.api.content.Mod;
import me.mythicalflame.netherreactor.core.registries.RegistryManager;

import java.util.ArrayList;
import java.util.List;

public class NetherReactorBootstrapper implements PluginBootstrap
{
    public static final List<Mod> mods = new ArrayList<>();

    @Override
    public void bootstrap(BootstrapContext context)
    {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    RegistryManager.getItemMutator().registerItems(mods);
                    RegistryManager.getEffectMutator().registerEffects(mods);
                    RegistryManager.getStatisticMutator().registerStatistics(mods);
                }
        ));
    }

    public static void registerMod(Mod mod)
    {
        mods.add(mod);
    }
}
