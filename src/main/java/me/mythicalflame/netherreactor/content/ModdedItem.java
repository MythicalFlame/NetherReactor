package me.mythicalflame.netherreactor.content;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.Tool;
import me.mythicalflame.netherreactor.NetherReactorModLoader;
import me.mythicalflame.netherreactor.creative.CreativeTab;
import me.mythicalflame.netherreactor.listeners.CompostingWatcher;
import me.mythicalflame.netherreactor.utilities.KeyPersistentDataType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO: recipes
/**
 * This class represents a custom item.
 */
public abstract class ModdedItem
{
    //Required properties
    /**
     * The non-null key containing the namespace and ID of the item. The namespace must match your mod's namespace.
     */
    @Nonnull
    private final Key KEY;
    /**
     * The material that the item is based off of.
     */
    @Nonnull
    private final Material MATERIAL;

    //Optional properties
    /**
     * The maximum stack size of the item. For technical reasons, this cannot not exceed 99.
     */
    @Nonnegative
    private int maxStackSize;
    /**
     * The maximum damage (durability) the item can endure.
     */
    @Nonnegative
    private int maxDamage;
    /**
     * The component used as the name of the item.
     */
    @Nonnull
    private Component itemName;
    /**
     * The key pointing to the item model asset. May be null for no item model.
     */
    @Nullable
    private Key itemModel;
    /**
     * The lore of the item. For technical reasons, the line count cannot exceed 255.
     */
    @Nonnull
    private List<Component> lore = new ArrayList<>();
    /**
     * The rarity of the item.
     */
    @Nonnull
    private ItemRarity rarity;
    /**
     * The consumable component of the item. Use null to disable. For reference, you can create a Consumable object by calling Consumable.consumable().build().
     */
    @Nullable
    private Consumable consumableComponent;
    /**
     * The consumable component of the item. Use null to disable. This requires consumableComponent to be non-null to take effect. For reference, you can create a FoodProperties object by calling FoodProperties.food().build().
     */
    @Nullable
    private FoodProperties foodComponent;
    /**
     * The tool component of the item. Use null to disable. For reference, you can create a Tool object by calling Tool.tool().build().
     */
    @Nullable
    private Tool toolComponent;
    /**
     * The chance for an item to successfully raise the level of a composter, from 0 to 100. Note that this only works when you manually compost, and does not support hoppers currently. Also, by default, NetherReactor tries to automatically get the composting chance from a list of vanilla items. This may not work for newer items if your plugin version is outdated.
     */
    @Nonnegative
    private int compostingChance;
    /**
     * The creative tab that the item will show up under in the NetherReactor creative inventory. Null means no tab.
     */
    @Nullable
    private CreativeTab creativeTab = null;

    /**
     * Constructs a ModdedItem object.
     *
     * @param key The non-null key containing the namespace and ID of the item. The namespace must match your mod's namespace.
     * @param material The non-null non-air Material that this item is based off of.
     *
     * @throws IllegalArgumentException If this constructor is called with an invalid material (AIR).
     */
    public ModdedItem(@Nonnull Key key, @Nonnull Material material)
    {
        if (material == Material.AIR)
        {
            throw new IllegalArgumentException("Attempted to initialize a ModdedItem object with an invalid material (AIR)! (" + key.namespace() + ":" + key.value() + ")");
        }

        this.KEY = key;
        this.MATERIAL = material;
        this.maxStackSize = MATERIAL.getDefaultData(DataComponentTypes.MAX_STACK_SIZE);
        this.maxDamage = MATERIAL.getDefaultData(DataComponentTypes.MAX_DAMAGE) == null ? 0 : MATERIAL.getDefaultData(DataComponentTypes.MAX_DAMAGE);
        this.itemName = MATERIAL.getDefaultData(DataComponentTypes.ITEM_NAME);
        this.rarity = MATERIAL.getDefaultData(DataComponentTypes.RARITY);
        this.consumableComponent = MATERIAL.getDefaultData(DataComponentTypes.CONSUMABLE);
        this.foodComponent = MATERIAL.getDefaultData(DataComponentTypes.FOOD);
        this.toolComponent = MATERIAL.getDefaultData(DataComponentTypes.TOOL);
        this.compostingChance = CompostingWatcher.getCompostingChance(material);
    }

    /**
     * Gets the ItemStack representation of the item with an amount of 1. If you want to override this for more customization, consider overriding getItemStack(int amount) instead.
     *
     * @return The ItemStack representation of this item with an amount of 1.
     */
    @Nonnull
    public ItemStack getItemStack()
    {
        return getItemStack(1);
    }

    /**
     * Gets the ItemStack representation of the item with a specific amount. You may override this if you want to do more customization.
     *
     * @param amount The amount of items that should be in the ItemStack.
     * @return The ItemStack representation of this item with a variable amount.
     */
    @Nonnull
    public ItemStack getItemStack(int amount)
    {
        ItemStack stack = ItemStack.of(MATERIAL);

        stack.setData(DataComponentTypes.MAX_STACK_SIZE, maxStackSize);
        if (maxDamage > 0)
        {
            stack.setData(DataComponentTypes.MAX_DAMAGE, maxDamage);
        }
        else
        {
            stack.unsetData(DataComponentTypes.MAX_DAMAGE);
        }
        stack.setData(DataComponentTypes.ITEM_NAME, itemName);
        //stack.setData(DataComponentTypes.ITEM_MODEL, new NamespacedKey("e", "e"));
        stack.setData(DataComponentTypes.RARITY, rarity);
        if (consumableComponent != null)
        {
            stack.setData(DataComponentTypes.CONSUMABLE, consumableComponent);
            if (foodComponent != null)
            {
                stack.setData(DataComponentTypes.FOOD, foodComponent);
            }
        }
        if (toolComponent != null)
        {
            stack.setData(DataComponentTypes.TOOL, toolComponent);
        }

        //PDC and item lore
        List<Component> moddedItemLore = new ArrayList<>();
        //Component firstLine = minimessage(NAMESPACE + ":" + ID);
        //moddedItemLore.add(firstLine); TODO make this just say mod name instead? + technical details inspect command
        moddedItemLore.addAll(lore);
        stack.setData(DataComponentTypes.LORE, ItemLore.lore(moddedItemLore));

        stack.editPersistentDataContainer(pdc -> pdc.set(NetherReactorModLoader.getItemKey(), KeyPersistentDataType.INSTANCE, KEY));

        stack.setAmount(amount);

        return stack;
    }

    /*
     *
     * PROPERTIES
     *
     */

    /**
     * Gets the namespace and ID of the item as a NamespacedKey.
     *
     * @return A Key with the namespace and ID of this item.
     */
    @Nonnull
    public final Key getKey()
    {
        return KEY;
    }

    /**
     * Gets the material of the item.
     *
     * @return The non-null Material that this item is based off of.
     */
    @Nonnull
    public final Material getMaterial()
    {
        return MATERIAL;
    }

    /**
     * Gets the maximum stack size of the item.
     *
     * @return The maximum stack size of this item.
     */
    @Nonnegative
    public int getMaxStackSize()
    {
        return maxStackSize;
    }

    /**
     * Sets the maximum stack size of the item. Since this is incompatible with durability, the method also disables durability on the item.
     *
     * @param maxStackSize The new maximum stack size of this item. For technical reasons, this cannot not exceed 99.
     *
     * @throws IllegalArgumentException If maxStackSize is less than 1 or larger than 99.
     */
    protected void setMaxStackSize(@Nonnegative int maxStackSize)
    {
        if (maxStackSize == 0 || maxStackSize > 99)
        {
            throw new IllegalArgumentException("Tried to set maxStackSize to an out of bounds value! (" + maxStackSize + ")! (" + KEY.namespace() + ":" + KEY.value() + ")");
        }

        this.maxStackSize = maxStackSize;

        if (maxStackSize != 1)
        {
            setMaxDamage(0);
        }
    }

    /**
     * Gets the maximum damage (durability) the item can endure.
     *
     * @return The maximum damage (durability) of this item.
     */
    @Nonnegative
    public int getMaxDamage()
    {
        return maxDamage;
    }

    /**
     * Sets the maximum damage (durability) the item can endure. Since this is incompatible with stacking, this method also sets the maximum stack size to 1.
     *
     * @param maxDamage The new maximum damage (durability) of this item. This must be non-negative.
     */
    protected void setMaxDamage(@Nonnegative int maxDamage)
    {
        this.maxDamage = maxDamage;

        if (maxDamage != 0)
        {
            setMaxStackSize(1);
        }
    }

    /**
     * Gets the name of the item.
     *
     * @return The component used as the name of this item.
     */
    @Nonnull
    public Component getItemName()
    {
        return itemName;
    }

    /**
     * Sets the name of the item.
     *
     * @param name The new non-null component to be used as the name of this item.
     */
    protected void setItemName(@Nonnull Component name)
    {
        this.itemName = name;
    }

    /**
     * Gets the model key of the item.
     *
     * @return The key that points to the model asset of this item.
     */
    @Nullable
    public Key getItemModel()
    {
        return itemModel;
    }

    /**
     * Sets the model key of the item.
     *
     * @param key The new key that points to the model asset of this item.
     */
    protected void setItemModel(@Nullable Key key)
    {
        this.itemModel = key;
    }

    /**
     * Gets the lore of the item.
     *
     * @return The List of Components used as lore for this item.
     */
    @Nonnull
    public List<Component> getLore()
    {
        return lore;
    }

    /**
     * Sets the lore of the item.
     *
     * @param lore The List of Components used as lore for this item. For technical reasons, the line count cannot exceed 255.
     *
     * @throws IllegalArgumentException If the size of the List is greater than 255. This method makes no checks for components that take multiple lines, which may cause your lore to exceed the maximum limit of 255 lines.
     */
    protected void setLore(@Nonnull List<Component> lore)
    {
        if (lore.size() > 255)
        {
            throw new IllegalArgumentException("Tried to set lore to an illegal value! (length of " + lore.size() + ")! (" + KEY.namespace() + ":" + KEY.value() + ")");
        }
        this.lore = lore;
    }

    /**
     * Gets the rarity of the item.
     *
     * @return The rarity of this item.
     */
    @Nonnull
    public ItemRarity getRarity()
    {
        return rarity;
    }

    /**
     * Sets the rarity of the item.
     *
     * @param rarity The new non-null rarity of this item.
     */
    protected void setRarity(@Nonnull ItemRarity rarity)
    {
        this.rarity = rarity;
    }

    /**
     * Gets the consumable component of the item.
     *
     * @return The consumable component of this item.
     */
    @Nullable
    public Consumable getConsumableComponent()
    {
        return consumableComponent;
    }

    /**
     * Sets the consumable component of the item. For reference, you can create a Consumable object by calling Consumable.consumable().build().
     *
     * @param consumable The new consumable component of this item. Null disables the component.
     */
    protected void setConsumableComponent(@Nullable Consumable consumable)
    {
        this.consumableComponent = consumable;
    }

    /**
     * Gets the food component of the item.
     *
     * @return The food component of this item.
     */
    @Nullable
    public FoodProperties getFoodComponent()
    {
        return foodComponent;
    }

    /**
     * Sets the food component of the item. This requires consumableComponent to be non-null to take effect. For reference, you can create a FoodProperties object by calling FoodProperties.food().build().
     *
     * @param food The new food component of this item. Null disables the component.
     */
    protected void setFoodComponent(@Nullable FoodProperties food)
    {
        this.foodComponent = food;
    }

    /**
     * Gets the tool component of the item.
     *
     * @return The tool component of this item.
     */
    @Nullable
    public Tool getToolComponent()
    {
        return toolComponent;
    }

    /**
     * Sets the tool component of the item. For reference, you can create a Tool object by calling Tool.tool().build().
     *
     * @param tool The new tool component of this item. Null disables the component.
     */
    protected void setToolComponent(@Nullable Tool tool)
    {
        this.toolComponent = tool;
    }

    /**
     * Gets the composting chance of the item. Note that this only works when you manually compost, and does not support hoppers currently.
     *
     * @return The chance of this item raising the level of a composter, from 0 to 100.
     */
    @Nonnegative
    public int getCompostingChance()
    {
        return compostingChance;
    }

    /**
     * Sets the composting chance of the item. Note that this only works when you manually compost, and does not support hoppers currently.
     *
     * @param compostingChance The chance of this item raising the level of a composter, from 0 to 100.
     *
     * @throws IllegalArgumentException If compostingChance is greater than 100.
     */
    protected void setCompostingChance(@Nonnegative int compostingChance)
    {
        if (compostingChance > 100)
        {
            throw new IllegalArgumentException("Tried to set compostingChance to an out of bounds value (" + compostingChance + ")! (" + KEY.namespace() + ":" + KEY.value() + ")");
        }
        this.compostingChance = compostingChance;
    }

    /**
     * Gets the creative tab that the item shows up under in the NetherReactor creative inventory.
     *
     * @return The creative tab of this item. Null means no tab.
     */
    @Nullable
    public CreativeTab getCreativeTab()
    {
        return creativeTab;
    }

    /**
     * Sets the creative tab that the item shows up under in the NetherReactor creative inventory.
     *
     * @param creativeTab The new creative tab of this item. Null means no tab. You can use a custom tab by extending CreativeTab, or you can use a vanilla one from the fields in the CreativeTabs class.
     */
    protected void setCreativeTab(@Nullable CreativeTab creativeTab)
    {
        this.creativeTab = creativeTab;
    }

    /**
     * This method checks if two objects are equal.
     *
     * @param other The object to compare to.
     * @return Whether the two objects are equal.
     */
    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ModdedItem otherItem))
        {
            return false;
        }

        return KEY.equals(otherItem.KEY) && MATERIAL.equals(otherItem.MATERIAL);
    }

    /**
     * Overrides Object#hashCode.
     * <p><a href="https://stackoverflow.com/a/16377941">Credit</a></p>
     *
     * @return The hashcode for this object.
     */
    @Override
    public int hashCode()
    {
        return Arrays.hashCode(new Object[]{KEY, MATERIAL});
    }
}
