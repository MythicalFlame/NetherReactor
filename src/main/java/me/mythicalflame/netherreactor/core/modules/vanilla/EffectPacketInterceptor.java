package me.mythicalflame.netherreactor.core.modules.vanilla;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.mythicalflame.netherreactor.core.registries.NetherReactorRegistry;
import me.mythicalflame.netherreactor.core.modules.enderreactor.EnderReactorModule;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class EffectPacketInterceptor extends PacketAdapter
{
    public EffectPacketInterceptor(Plugin plugin)
    {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.REMOVE_ENTITY_EFFECT, PacketType.Play.Server.ENTITY_EFFECT);
    }

    @Override
    public void onPacketSending(PacketEvent event)
    {
        Player player = event.getPlayer();
        if (event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT)
        {
            PacketContainer packet =  event.getPacket();
            if (NetherReactorRegistry.Effects.get(packet.getEffectTypes().read(0).key()) == null)
            {
                return;
            }

            if (!EnderReactorModule.hasPlayer(player))
            {
                event.setCancelled(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT)
        {
            PacketContainer packet =  event.getPacket();
            if (NetherReactorRegistry.Effects.get(packet.getEffectTypes().read(0).key()) == null)
            {
                return;
            }

            if (!EnderReactorModule.hasPlayer(player))
            {
                event.setCancelled(true);
            }
        }
    }
}
