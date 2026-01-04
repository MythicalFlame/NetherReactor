package me.mythicalflame.netherreactor.api;

public class ModdedItem
{
    private final ItemProperties ITEM_PROPERTIES;

    public ModdedItem(ItemProperties itemProperties)
    {
        this.ITEM_PROPERTIES = itemProperties;
    }

    public ItemProperties getItemProperties()
    {
        return this.ITEM_PROPERTIES;
    }
}
