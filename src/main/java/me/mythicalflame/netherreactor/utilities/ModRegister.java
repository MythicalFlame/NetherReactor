package me.mythicalflame.netherreactor.utilities;

import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.content.ModdedTag;
import me.mythicalflame.netherreactor.events.ModRegisterEvent;
import me.mythicalflame.netherreactor.listeners.CompostingWatcher;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class ModRegister
{
    private static final HashMap<String, Mod> modCache = new HashMap<>();
    private static final HashMap<Key, ModdedItem> itemCache = new HashMap<>();
    private static final HashMap<Key, ModdedTag> tagCache = new HashMap<>();
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
        return modCache.get(namespace);
    }

    public static ModdedItem getCachedItem(Key key)
    {
        return itemCache.get(key);
    }

    public static ModdedTag getCachedTag(Key key)
    {
        return tagCache.get(key);
    }

    public static Stream<Map.Entry<String, Mod>> modCacheStream()
    {
        return modCache.entrySet().stream();
    }

    public static Stream<Map.Entry<Key, ModdedItem>> itemCacheStream()
    {
        return itemCache.entrySet().stream();
    }

    public static Stream<Map.Entry<Key, ModdedTag>> tagCacheStream()
    {
        return tagCache.entrySet().stream();
    }

    private static void registerItems(Mod mod, NetherReactorModLoader plugin)
    {
        for (ModdedItem item : mod.getRegisteredItems())
        {
            itemCache.put(item.getKey(), item);

            if (!registered[0] && item.getCompostingChance() > 0)
            {
                registered[0] = true;
                plugin.getServer().getPluginManager().registerEvents(new CompostingWatcher(), plugin);
            }
        }
    }

    private static void registerTags(Mod mod)
    {
        for (ModdedTag tag : mod.getRegisteredTags())
        {
            tagCache.put(tag.getKey(), tag);
        }
    }

    //TODO creative tabs
}
