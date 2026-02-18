package me.mythicalflame.netherreactor.core.modules.enderreactor;

import me.mythicalflame.netherreactor.api.Version;
import me.mythicalflame.netherreactor.core.NetherReactorPlugin;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public final class EnderReactorModule
{
    private static final HashSet<Player> ENDER_REACTOR_PLAYERS = new HashSet<>();
    public static final HashSet<Version> SUPPORTED_VERSIONS = new HashSet<>(Set.of(new Version(0, 0, 1)));

    private EnderReactorModule() {}

    public static void activate(NetherReactorPlugin plugin)
    {
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "enderreactor:enderreactor_specification", new NetherReactorMessagingHandler());
        plugin.getServer().getPluginManager().registerEvents(new ModdedLeaveListener(), plugin);
    }

    public static boolean hasPlayer(Player player)
    {
        return ENDER_REACTOR_PLAYERS.contains(player);
    }

    public static void addPlayer(Player player)
    {
        ENDER_REACTOR_PLAYERS.add(player);
    }

    public static void removePlayer(Player player)
    {
        ENDER_REACTOR_PLAYERS.remove(player);
    }
}
