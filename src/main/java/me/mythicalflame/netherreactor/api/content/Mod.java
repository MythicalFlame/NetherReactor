package me.mythicalflame.netherreactor.api.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mod
{
    private final ArrayList<ModdedItem> ITEMS = new ArrayList<>();
    private final ArrayList<ModdedEffect> EFFECTS = new ArrayList<>();
    private final ArrayList<ModdedStatistic> STATISTICS = new ArrayList<>();

    public void addItem(ModdedItem item)
    {
        ITEMS.add(item);
    }

    public void addEffect(ModdedEffect effect)
    {
        EFFECTS.add(effect);
    }

    public void addStatistic(ModdedStatistic statistic)
    {
        STATISTICS.add(statistic);
    }

    public List<ModdedItem> getRegisteredItems()
    {
        return Collections.unmodifiableList(ITEMS);
    }

    public List<ModdedEffect> getRegisteredEffects()
    {
        return Collections.unmodifiableList(EFFECTS);
    }

    public List<ModdedStatistic> getRegisteredStatistics()
    {
        return Collections.unmodifiableList(STATISTICS);
    }
}
