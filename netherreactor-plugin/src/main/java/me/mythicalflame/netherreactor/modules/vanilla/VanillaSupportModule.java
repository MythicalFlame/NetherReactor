package me.mythicalflame.netherreactor.modules.vanilla;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import me.mythicalflame.netherreactor.NetherReactorPlugin;
import me.mythicalflame.netherreactor.registries.NetherReactorRegistry;

public final class VanillaSupportModule
{
    public static void activate(NetherReactorPlugin plugin)
    {
        if (!PacketEventsInjector.inject())
        {
            plugin.getLogger().severe("Could not enable vanilla support module due to an injection error!");
            return;
        }

        EventManager events = PacketEvents.getAPI().getEventManager();

        if (!NetherReactorRegistry.Effects.isEmpty())
        {
            events.registerListener(new EffectPacketInterceptor(), PacketListenerPriority.HIGHEST);
        }

        if (!NetherReactorRegistry.Statistics.isEmpty())
        {
            events.registerListener(new StatisticPacketInterceptor(), PacketListenerPriority.HIGHEST);
        }
    }
}
