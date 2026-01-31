package me.mythicalflame.netherreactor.core.modules.vanilla;

import com.comphenix.protocol.ProtocolLibrary;
import me.mythicalflame.netherreactor.core.NetherReactorPlugin;
import me.mythicalflame.netherreactor.core.registries.NetherReactorRegistry;

public final class VanillaSupportModule
{
    public static void activate(NetherReactorPlugin plugin)
    {
        if (!NetherReactorRegistry.Effects.isEmpty())
        {
            ProtocolLibrary.getProtocolManager().addPacketListener(new EffectPacketInterceptor(plugin));
        }
        if (!NetherReactorRegistry.Statistics.isEmpty())
        {
            ProtocolLibrary.getProtocolManager().addPacketListener(new StatisticPacketInterceptor(plugin));
        }
    }
}
