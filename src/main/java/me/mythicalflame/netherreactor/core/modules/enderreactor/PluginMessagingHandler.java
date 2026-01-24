package me.mythicalflame.netherreactor.core.modules.enderreactor;

import me.mythicalflame.netherreactor.api.Version;
import me.mythicalflame.netherreactor.core.NetherReactorBootstrapper;
import me.mythicalflame.netherreactor.core.NetherReactorPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.potion.PotionEffectTypeCategory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static me.mythicalflame.netherreactor.core.NetherReactorUtilities.minimessage;

public final class PluginMessagingHandler implements PluginMessageListener
{
    private final NetherReactorPlugin plugin;

    public PluginMessagingHandler(NetherReactorPlugin plugin)
    {
        this.plugin = plugin;
    }

    //Receiving - Player sends major minor patch releasedata version of their mod
    //Outgoing - see below
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message)
    {
        if (!channel.equals("netherreactor"))
        {
            return;
        }

        if (message.length < 3)
        {
            sendUnsupportedMessage(player);
            return;
        }

        byte[] releaseBytes = Arrays.copyOfRange(message, 3, message.length);
        String releaseData = new String(releaseBytes, StandardCharsets.US_ASCII);
        Version playerVersion = new Version(message[0], message[1], message[2], releaseData);

        if (!EnderReactorModule.SUPPORTED_VERSIONS.contains(playerVersion))
        {
            sendUnsupportedMessage(player);
            return;
        }

        EnderReactorModule.addPlayer(player);
        //TODO items
        ContentEncoder.sendEffects(plugin, player);
    }

    private void sendUnsupportedMessage(Player player)
    {
        player.sendMessage(minimessage("<red>WARNING! Your version of the EnderReactor mod is not supported on this server!</red>"));
        String supportedVersions = EnderReactorModule.SUPPORTED_VERSIONS.stream()
                .map(Version::toString)
                .collect(Collectors.joining(", "));
        player.sendMessage(minimessage("<red>Please upgrade to one of: " + supportedVersions + "</red>"));
    }

    //TODO cache it
    //Encoding: type byte + other data as defined below
    private static class ContentEncoder
    {
        //TODO add items (type 1)

        //Effect encoding: {type byte, category byte, color int, namespace size int, namespace bytes, value size int, value bytes}
        //Type byte - 1
        //Category byte - 0 beneficial, 1 neutral, 2 harmful
        private static void sendEffects(NetherReactorPlugin plugin, Player player)
        {
            /*
            NetherReactorBootstrapper.moddedEffects.forEach(effect -> {
                ArrayList<Byte> send = new ArrayList<>();

                //TYPE BYTE
                send.add((byte) 1);

                //BENEFICIAL BYTE
                if (effect.getCategory() == PotionEffectTypeCategory.BENEFICIAL)
                {
                    send.add((byte) 0);
                }
                else if (effect.getCategory() == PotionEffectTypeCategory.NEUTRAL)
                {
                    send.add((byte) 1);
                }
                else
                {
                    send.add((byte) 2);
                }

                //COLOR BYTES
                int color = effect.getColor();
                send.add((byte) (color >> 24));
                send.add((byte) (color >> 16));
                send.add((byte) (color >> 8));
                send.add((byte) color);

                //KEY BYTES

                byte[] result = new byte[send.size()];
                for (int i = 0; i < send.size(); ++i)
                {
                    result[i] = send.get(i);
                }

                player.sendPluginMessage(plugin, "netherreactor", result);
            });*/
        }
    }
}
