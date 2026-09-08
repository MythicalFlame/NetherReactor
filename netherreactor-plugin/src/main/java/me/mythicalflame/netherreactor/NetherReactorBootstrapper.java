package me.mythicalflame.netherreactor;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.instrumentation.Patcher;
import me.mythicalflame.netherreactor.instrumentation.patches.Patch;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class NetherReactorBootstrapper implements PluginBootstrap
{
    public static final List<Mod> MODS = new ArrayList<>();
    private static boolean hasAlreadyRun = false;
    private static boolean doEffectsExist = false;
    private static boolean doItemsExist = false;
    private static boolean doBlocksExist = false;
    private static boolean doStatisticsExist = false;
    private static ComponentLogger LOGGER;

    @Override
    public void bootstrap(BootstrapContext context)
    {
        LOGGER = context.getLogger();

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
                        if (doEffectsExist)
                        {
                            InternalsManager.getEffectMutator().registerEffects(MODS, LOGGER);
                        }
                        if (doItemsExist)
                        {
                            InternalsManager.getItemMutator().registerItems(MODS, LOGGER);
                        }
                        if (doBlocksExist)
                        {
                            InternalsManager.getBlockMutator().registerBlocks(MODS, LOGGER);
                        }
                        if (doStatisticsExist)
                        {
                            InternalsManager.getStatisticMutator().registerStatistics(MODS, LOGGER);
                        }
                        if (doEffectsExist || doItemsExist || doBlocksExist || doStatisticsExist)
                        {
                            InternalsManager.getInternalInterface().nullRegistries();
                        }
                    }
                    catch (Exception e)
                    {
                        LOGGER.error("Exception thrown when trying to load mods:", e);
                        LOGGER.error("COULD NOT START UP NETHERREACTOR! SHUTTING DOWN SERVER...");
                        LOGGER.error("If you remove NetherReactor, your data may be deleted since content will no longer exist.");
                        System.exit(1);
                    }
                }
        ));
    }

    /**
     * Adds to the list of mods to be registered.
     *
     * @param mod The mod to register.
     */
    public static void registerMod(@NonNull Mod mod)
    {
        MODS.add(mod);

        if (!mod.getRegisteredEffects().isEmpty())
        {
            doEffectsExist = true;
        }
        if (!mod.getRegisteredItems().isEmpty())
        {
            doItemsExist = true;
        }
        if (!mod.getRegisteredBlocks().isEmpty())
        {
            doBlocksExist = true;
        }
        if (!mod.getRegisteredStatistics().isEmpty())
        {
            doStatisticsExist = true;
        }
    }

    /**
     * Attempts to apply a patch to a class.
     *
     * @param patch The patch to apply.
     */
    public static void registerPatch(@NonNull Patch patch)
    {
        try
        {
            Patcher.patch(patch);
        }
        catch (Exception e)
        {
            LOGGER.error("Could not register patch on {} because of {}", patch.getClassName(), e);
        }
    }
}
