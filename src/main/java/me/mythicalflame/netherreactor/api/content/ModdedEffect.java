package me.mythicalflame.netherreactor.api.content;

import net.kyori.adventure.key.Key;
import org.bukkit.potion.PotionEffectTypeCategory;

public class ModdedEffect
{
    //TODO attribute modifiers
    private final Key KEY;
    private final PotionEffectTypeCategory CATEGORY;
    private final int COLOR;

    public ModdedEffect(Key key, PotionEffectTypeCategory category, int color)
    {
        this.KEY = key;
        this.CATEGORY = category;
        this.COLOR = color;
    }

    public PotionEffectTypeCategory getCategory()
    {
        return this.CATEGORY;
    }

    public Key getKey()
    {
        return this.KEY;
    }

    public int getColor()
    {
        return this.COLOR;
    }
}
