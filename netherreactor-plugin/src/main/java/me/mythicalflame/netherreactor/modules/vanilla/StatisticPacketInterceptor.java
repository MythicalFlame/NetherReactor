package me.mythicalflame.netherreactor.modules.vanilla;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerStatistics;
import me.mythicalflame.netherreactor.modules.enderreactor.EnderReactorModule;
import me.mythicalflame.netherreactor.registries.NetherReactorRegistry;

import java.util.HashSet;
import java.util.Map;

public final class StatisticPacketInterceptor implements PacketListener
{
    @Override
    public void onPacketSend(PacketSendEvent event)
    {
        if (EnderReactorModule.hasPlayer(event.getPlayer()))
        {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Server.STATISTICS)
        {
            WrapperPlayServerStatistics packet = new WrapperPlayServerStatistics(event);

            Map<String, Integer> stats = packet.getStatistics();
            HashSet<String> badNames = new HashSet<>();

            stats.forEach((stat, value) -> {
                if (!NetherReactorRegistry.Statistics.containsName(stat))
                {
                    badNames.add(stat);
                }
            });

            badNames.forEach(stats::remove);
            packet.setStatistics(stats);

            event.markForReEncode(true);
        }
    }
}
