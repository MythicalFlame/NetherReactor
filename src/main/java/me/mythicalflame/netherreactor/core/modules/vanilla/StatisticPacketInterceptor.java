package me.mythicalflame.netherreactor.core.modules.vanilla;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedStatistic;
import me.mythicalflame.netherreactor.core.modules.enderreactor.EnderReactorModule;
import me.mythicalflame.netherreactor.core.registries.NetherReactorRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Map;

public final class StatisticPacketInterceptor extends PacketAdapter
{
    public StatisticPacketInterceptor(Plugin plugin)
    {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.STATISTIC);
    }

    @Override
    public void onPacketSending(PacketEvent event)
    {
        Player player = event.getPlayer();
        if (event.getPacketType() == PacketType.Play.Server.STATISTIC)
        {
            if (EnderReactorModule.hasPlayer(player))
            {
                return;
            }

            event.setCancelled(true);

            //ProtocolLib has a bug that causes an error on reading statistics
            //TODO add back in when bug fixed
            /*
            PacketContainer packet = event.getPacket();

            Map<WrappedStatistic, Integer> stats = packet.getStatisticMaps().read(0);
            HashSet<String> badNames = new HashSet<>();

            stats.forEach((stat, value) -> {
                if (!NetherReactorRegistry.Statistics.containsName(stat.getName()))
                {
                    badNames.add(stat.getName());
                }
            });

            badNames.forEach(stats::remove);

            packet.getStatisticMaps().write(0, stats);*/
        }
    }
}
