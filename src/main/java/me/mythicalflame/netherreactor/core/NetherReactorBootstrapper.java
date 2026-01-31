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
    public static final List<Mod> MODS = new ArrayList<>();
    private static boolean doItemsExist = false;
    private static boolean doEffectsExist = false;
    private static boolean doStatisticsExist = false;

    @Override
    public void bootstrap(BootstrapContext context)
    {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    if (doItemsExist)
                    {
                        RegistryManager.getItemMutator().registerItems(MODS);
                    }
                    if (doEffectsExist)
                    {
                        RegistryManager.getEffectMutator().registerEffects(MODS);
                    }
                    if (doStatisticsExist)
                    {
                        RegistryManager.getStatisticMutator().registerStatistics(MODS);
                    }
                }
        ));
    }

    public static void registerMod(Mod mod)
    {
        MODS.add(mod);

        if (!mod.getRegisteredItems().isEmpty())
        {
            doItemsExist = true;
        }
        if (!mod.getRegisteredEffects().isEmpty())
        {
            doEffectsExist = true;
        }
        if (!mod.getRegisteredStatistics().isEmpty())
        {
            doStatisticsExist = true;
        }
    }
}
