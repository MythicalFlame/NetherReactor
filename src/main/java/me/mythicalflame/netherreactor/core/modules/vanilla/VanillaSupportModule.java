package me.mythicalflame.netherreactor.core.modules.vanilla;

import com.comphenix.protocol.ProtocolLibrary;
import me.mythicalflame.netherreactor.core.NetherReactorPlugin;

public final class VanillaSupportModule
{
    public static void activate(NetherReactorPlugin plugin)
    {
        ProtocolLibrary.getProtocolManager().addPacketListener(new EffectPacketInterceptor(plugin));
    }
}
