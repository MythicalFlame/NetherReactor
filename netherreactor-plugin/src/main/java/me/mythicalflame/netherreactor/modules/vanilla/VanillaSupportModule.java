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
        boolean effectsEmpty = NetherReactorRegistry.Effects.isEmpty();
        boolean itemsEmpty = NetherReactorRegistry.Items.isEmpty();

        if (effectsEmpty && itemsEmpty)
        {
            plugin.getLogger().severe("No content, not activating vanilla support module.");
            return;
        }

        if (!PacketEventsInjector.inject(effectsEmpty, itemsEmpty))
        {
            plugin.getLogger().severe("Could not enable vanilla support module due to an injection error!");
            return;
        }

        EventManager events = PacketEvents.getAPI().getEventManager();

        events.registerListener(new ItemStackPacketInterceptor(effectsEmpty, itemsEmpty), PacketListenerPriority.HIGHEST);

        if (!NetherReactorRegistry.Statistics.isEmpty())
        {
            events.registerListener(new StatisticPacketInterceptor(), PacketListenerPriority.HIGHEST);
        }
    }
}
