package me.mythicalflame.netherreactor.api;

import net.kyori.adventure.key.Key;

public class ItemProperties
{
    private final Key KEY;

    public ItemProperties(Key key)
    {
        this.KEY = key;
    }

    public Key getKey()
    {
        return this.KEY;
    }
}
