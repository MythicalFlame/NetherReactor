package me.mythicalflame.netherreactor.utilities;

import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.events.ModRegisterEvent;
import me.mythicalflame.netherreactor.listeners.CompostingWatcher;

import java.util.HashMap;

public final class ModRegister
{
    private static final HashMap<String, Mod> modCache = new HashMap<>();
    private static final HashMap<String, ModdedItem> itemCache = new HashMap<>();
    /*
        Smart listener system that only registers listeners when needed
        Index - Listener
        0 - CompostingWatcher
     */
    private static final boolean[] registered = new boolean[1];

    public static void register(Mod mod, NetherReactorModLoader plugin)
    {
        modCache.put(mod.getNamespace(), mod);
        registerItems(mod, plugin);
        ModRegisterEvent event = new ModRegisterEvent(mod);
        event.callEvent();
    }

    public static Mod getCachedMod(String namespace)
    {
        return modCache.get(namespace.toLowerCase());
    }

    public static ModdedItem getCachedItem(String technicalName)
    {
        return itemCache.get(technicalName.toLowerCase());
    }

    private static void registerItems(Mod mod, NetherReactorModLoader plugin)
    {
        for (ModdedItem item : mod.getRegisteredItems())
        {
            itemCache.put(item.getNamespace() + ":" + item.getID(), item);

            if (!registered[0] && item.getCompostingChance() > 0)
            {
                registered[0] = true;
                plugin.getServer().getPluginManager().registerEvents(new CompostingWatcher(), plugin);
            }
        }
    }
}
