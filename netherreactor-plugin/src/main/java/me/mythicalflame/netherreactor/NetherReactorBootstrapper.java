package me.mythicalflame.netherreactor;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.mythicalflame.netherreactor.content.Mod;

import java.util.ArrayList;
import java.util.List;

public class NetherReactorBootstrapper implements PluginBootstrap
{
    public static final List<Mod> MODS = new ArrayList<>();
    private static boolean hasAlreadyRun = false;
    private static boolean doStatisticsExist = false;
    private static boolean doEffectsExist = false;
    private static boolean doItemsExist = false;

    @Override
    public void bootstrap(BootstrapContext context)
    {
        InternalsManager.getInternalInterface().initRegistries();
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    //Spark profiler is weird and calls this method when you stop profiling for some reason
                    if (hasAlreadyRun)
                    {
                        return;
                    }
                    hasAlreadyRun = true;

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

                    InternalsManager.getInternalInterface().nullRegistries();
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
