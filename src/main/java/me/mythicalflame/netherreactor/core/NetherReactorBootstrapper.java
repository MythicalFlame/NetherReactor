package me.mythicalflame.netherreactor.core;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.mythicalflame.netherreactor.api.content.Mod;
import me.mythicalflame.netherreactor.core.registries.InternalsManager;

import java.util.ArrayList;
import java.util.List;

public class NetherReactorBootstrapper implements PluginBootstrap
{
    public static final List<Mod> MODS = new ArrayList<>();
    private static boolean doStatisticsExist = false;
    private static boolean doEffectsExist = false;
    private static boolean doItemsExist = false;

    @Override
    public void bootstrap(BootstrapContext context)
    {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    if (doStatisticsExist)
                    {
                        InternalsManager.getStatisticMutator().registerStatistics(MODS);
                    }
                    if (doEffectsExist)
                    {
                        InternalsManager.getEffectMutator().registerEffects(MODS);
                    }
                    if (doItemsExist)
                    {
                        InternalsManager.getItemMutator().registerItems(MODS);
                    }
                }
        ));
    }

    public static void registerMod(Mod mod)
    {
        MODS.add(mod);

        if (!mod.getRegisteredStatistics().isEmpty())
        {
            doStatisticsExist = true;
        }
        if (!mod.getRegisteredEffects().isEmpty())
        {
            doEffectsExist = true;
        }
        if (!mod.getRegisteredItems().isEmpty())
        {
            doItemsExist = true;
        }
    }
}
