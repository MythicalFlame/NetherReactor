package me.mythicalflame.netherreactor;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.mythicalflame.netherreactor.content.Mod;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

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
        ComponentLogger logger = context.getLogger();

        InternalsManager.getInternalInterface().initRegistries();
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    //Spark profiler is weird and calls this method when you stop profiling for some reason
                    if (hasAlreadyRun)
                    {
                        return;
                    }
                    hasAlreadyRun = true;

                    try
                    {
                        if (doStatisticsExist)
                        {
                            InternalsManager.getStatisticMutator().registerStatistics(MODS, logger);
                        }
                        if (doEffectsExist)
                        {
                            InternalsManager.getEffectMutator().registerEffects(MODS, logger);
                        }
                        if (doItemsExist)
                        {
                            InternalsManager.getItemMutator().registerItems(MODS, logger);
                        }

                        InternalsManager.getInternalInterface().nullRegistries();
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception thrown when trying to load mods:", e);
                        logger.error("COULD NOT START UP NETHERREACTOR! SHUTTING DOWN SERVER...");
                        logger.error("If you remove NetherReactor, your data may be deleted since content will no longer exist.");
                        System.exit(1);
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
