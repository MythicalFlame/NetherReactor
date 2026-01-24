package me.mythicalflame.netherreactor.api.content;

import io.papermc.paper.datacomponent.DataComponentType;
import net.kyori.adventure.key.Key;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ItemProperties
{
    private final Key KEY;
    private final HashMap<DataComponentType, Object> COMPONENTS = new HashMap<>();

    public ItemProperties(Key key)
    {
        this.KEY = key;
    }

    public Key getKey()
    {
        return this.KEY;
    }

    public Map<DataComponentType, Object> getComponents()
    {
        return Collections.unmodifiableMap(this.COMPONENTS);
    }

    public ItemProperties setComponent(DataComponentType type, Object value)
    {
        this.COMPONENTS.put(type, value);
        return this;
    }
}
