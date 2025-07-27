package me.mythicalflame.netherreactor.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Utilities
{
    public static Component minimessage(String msg)
    {
        return MiniMessage.miniMessage().deserialize(msg);
    }
}
