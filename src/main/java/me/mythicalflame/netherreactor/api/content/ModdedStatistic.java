package me.mythicalflame.netherreactor.api.content;

import net.kyori.adventure.key.Key;

public class ModdedStatistic
{
    private final Key KEY;

    public ModdedStatistic(Key key)
    {
        this.KEY = key;
    }

    public Key getKey()
    {
        return this.KEY;
    }
}
