package me.mythicalflame.netherreactor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class NetherReactorUtilities
{
    public static Component minimessage(String msg)
    {
        return MiniMessage.miniMessage().deserialize(msg);
    }
}
