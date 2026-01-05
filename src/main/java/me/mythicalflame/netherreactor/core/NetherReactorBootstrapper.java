package me.mythicalflame.netherreactor.core;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.mythicalflame.netherreactor.api.ModdedItem;
import me.mythicalflame.netherreactor.internals.v1_21_8.ItemRegistryMutator_v1_21_8;

import java.util.ArrayList;
import java.util.List;

public class NetherReactorBootstrapper implements PluginBootstrap
{
    public static final List<ModdedItem> moddedItems = new ArrayList<>();

    @Override
    public void bootstrap(BootstrapContext context)
    {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
                event -> {
                    new ItemRegistryMutator_v1_21_8().registerItems(moddedItems);
                }
        ));
    }

    public static void registerItems(List<ModdedItem> newItems)
    {
        moddedItems.addAll(newItems);
    }
}
