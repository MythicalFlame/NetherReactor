package me.mythicalflame.netherreactor.core.modules.enderreactor;

import me.mythicalflame.netherreactor.api.Version;
import me.mythicalflame.netherreactor.api.content.Mod;
import me.mythicalflame.netherreactor.core.NetherReactorBootstrapper;
import me.mythicalflame.netherreactor.core.NetherReactorPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import static me.mythicalflame.netherreactor.core.NetherReactorUtilities.minimessage;

public final class NetherReactorMessagingHandler implements PluginMessageListener
{
    //Receiving - Player sends client mod version + mod1name:mod1ver,mod2name:mod2ver,... installed mods
    //Outgoing - see below
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message)
    {
        if (!channel.equals("enderreactor:enderreactor_specification"))
        {
            return;
        }

        if (message.length < 3)
        {
            NetherReactorPlugin.getLoggerStatic().info("Rejected EnderReactor player " + player.getName() + " because of insufficient message length.");
            sendUnsupportedMessage(player);
            return;
        }

        //TODO client MC version

        Version clientModVersion = new Version(message[0], message[1], message[2]);
        if (!EnderReactorModule.SUPPORTED_VERSIONS.contains(clientModVersion))
        {
            NetherReactorPlugin.getLoggerStatic().info("Rejected EnderReactor player " + player.getName() + " because of outdated version " + clientModVersion + ".");
            sendUnsupportedMessage(player);
            return;
        }

        byte[] modListBytes = Arrays.copyOfRange(message, 3, message.length);
        String modListString = new String(modListBytes, StandardCharsets.US_ASCII);
        String[] clientMods = modListString.split(",");

        if (NetherReactorBootstrapper.MODS.size() != clientMods.length)
        {
            NetherReactorPlugin.getLoggerStatic().info("Rejected EnderReactor player " + player.getName() + " because of mismatched mod list length.");
            player.sendMessage(minimessage("<red>WARNING! You have a different number of installed EnderReactor mods than the server does.</red>"));
            return;
        }

        for (int i = 0; i < NetherReactorBootstrapper.MODS.size(); ++i)
        {
            Mod serverMod = NetherReactorBootstrapper.MODS.get(i);
            if (!clientMods[i].equals(serverMod.toString()))
            {
                NetherReactorPlugin.getLoggerStatic().info("Rejected EnderReactor player " + player.getName() + " because of mod mismatch. Expected \"" + serverMod + "\", got \"" + clientMods[i] + "\".");
                player.sendMessage(minimessage("<red>WARNING! Your EnderReactor modlist does not match the server modlist, so you will be treated as a vanilla player."));
                return;
            }
        }

        NetherReactorPlugin.getLoggerStatic().info("Added EnderReactor player " + player.getName() + ".");
        EnderReactorModule.addPlayer(player);
    }

    private void sendUnsupportedMessage(Player player)
    {
        player.sendMessage(minimessage("<red>WARNING! Your version of the EnderReactor mod is not supported on this server!</red>"));
        String supportedVersions = EnderReactorModule.SUPPORTED_VERSIONS.stream()
                .map(Version::toString)
                .collect(Collectors.joining(", "));
        player.sendMessage(minimessage("<red>Please upgrade to one of: " + supportedVersions + "</red>"));
    }
}
