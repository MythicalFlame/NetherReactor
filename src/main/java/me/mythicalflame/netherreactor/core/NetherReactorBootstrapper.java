package me.mythicalflame.netherreactor.core;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.mythicalflame.netherreactor.api.ItemProperties;
import me.mythicalflame.netherreactor.api.ModdedItem;
import me.mythicalflame.netherreactor.internals.v1_21_8.ItemRegistryMutator_v1_21_8;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.java.JavaPlugin;

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
                    new ItemRegistryMutator_v1_21_8().registerItems(List.of(new ModdedItem(new ItemProperties(Key.key("custom", "sulphur_ingot")))));
                }
        ));
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context)
    {
        return new NetherReactorPlugin();
    }
}
