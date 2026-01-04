package me.mythicalflame.netherreactor.internals.v1_21_8;

import me.mythicalflame.netherreactor.api.ModdedItem;
import me.mythicalflame.netherreactor.core.AbstractItemRegistryMutator;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.bukkit.Material;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import sun.misc.Unsafe;

import static net.minecraft.core.component.DataComponents.*;

//Inspired by ItemsAdderBlockInjector
public class ItemRegistryMutator_v1_21_8 implements AbstractItemRegistryMutator
{
    @Override
    public void registerItems(Collection<ModdedItem> items)
    {
        MappedRegistry<Item> ITEMS;
        try
        {
            ITEMS = (MappedRegistry<Item>) BuiltInRegistries.ITEM;

            Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
            frozenField.setAccessible(true);
            frozenField.set(ITEMS, false);

            Field allTagsField = MappedRegistry.class.getDeclaredField("allTags");
            allTagsField.setAccessible(true);

            Field frozenTagsField = MappedRegistry.class.getDeclaredField("frozenTags");
            frozenTagsField.setAccessible(true);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            System.out.println("[NetherReactor] Could not initialize item registry injector!");
            e.printStackTrace();
            return;
        }

        items.forEach(moddedItem -> {
            Key moddedItemKey = moddedItem.getItemProperties().getKey();
            Item minecraftItem;
            try
            {
                Field allTagsField = MappedRegistry.class.getDeclaredField("allTags");
                allTagsField.setAccessible(true);

                Field unregisteredIntrusiveHolders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
                unregisteredIntrusiveHolders.setAccessible(true);
                unregisteredIntrusiveHolders.set(ITEMS, new IdentityHashMap<>());

                ResourceKey<Item> resourceKey = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(moddedItemKey.namespace(), moddedItemKey.value()));

                Item.Properties properties = new Item.Properties().setId(resourceKey);
                minecraftItem = new Item(properties);

                Method unboundMethod = getUnboundMethod();
                unboundMethod.setAccessible(true);
                allTagsField.set(ITEMS, unboundMethod.invoke(null));

                ITEMS.createIntrusiveHolder(minecraftItem);
                Holder<Item> holder = ITEMS.register(resourceKey, minecraftItem, RegistrationInfo.BUILT_IN);

                Set<TagKey<Item>> tags = new HashSet<>();
                Holder.direct(minecraftItem).tags().forEach(tags::add);

                Method bindMethod = Holder.Reference.class.getDeclaredMethod("bindTags", Collection.class);
                bindMethod.setAccessible(true);
                bindMethod.invoke(holder, tags);

                unregisteredIntrusiveHolders.set(ITEMS, null);
            }
            catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
            {
                System.out.println("[NetherReactor] Could not inject item " + moddedItemKey + " into the Minecraft Item registry!");
                e.printStackTrace();
                return;
            }

            //Bukkit injector
            try
            {
                Field itemMaterialField = CraftMagicNumbers.class.getDeclaredField("ITEM_MATERIAL");
                itemMaterialField.setAccessible(true);
                HashMap<Item, Material> ITEM_MATERIAL = (HashMap<Item, Material>) itemMaterialField.get(null);
                ITEM_MATERIAL.put(minecraftItem, Material.COBBLESTONE); //placeholder
            }
            catch (IllegalAccessException | NoSuchFieldException e)
            {
                System.out.println("[NetherReactor] Could not inject item " + moddedItemKey + " into the Bukkit Item->Material registry!");
                e.printStackTrace();
                return;
            }

            ITEMS.freeze();

            System.out.println("[NetherReactor] Registered item " + moddedItemKey + " successfully!");
        });
    }
    /*

    //For some reason, DataComponents.COMMON_ITEM_COMPONENTS is not immediately instantiated, so we cannot create an Item.Properties object normally
    //https://stackoverflow.com/a/25448017
    public Item.Properties getProperties(ModdedItem moddedItem) throws NoSuchFieldException, IllegalAccessException, InstantiationException
    {
        Key moddedItemKey = moddedItem.getItemProperties().getKey();
        ResourceKey<Item> resourceKey = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(moddedItemKey.namespace(), moddedItemKey.value()));

        Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
        singleoneInstanceField.setAccessible(true);
        Unsafe unsafe = (Unsafe) singleoneInstanceField.get(null);
        Item.Properties properties = (Item.Properties) unsafe.allocateInstance(Item.Properties.class);

        Field componentsPropertiesField = Item.Properties.class.getDeclaredField("components");
        componentsPropertiesField.setAccessible(true);
        componentsPropertiesField.set(properties, DataComponentMap.builder()/*.addAll(DataComponentMap.builder().set(MAX_STACK_SIZE, 64).set(LORE, ItemLore.EMPTY).set(ENCHANTMENTS, ItemEnchantments.EMPTY).set(REPAIR_COST, 0).set(ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).set(RARITY, Rarity.COMMON).set(BREAK_SOUND, SoundEvents.ITEM_BREAK).set(TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT)*//*);
        Field requiredFeaturesPropertiesField = Item.Properties.class.getDeclaredField("requiredFeatures");
        requiredFeaturesPropertiesField.setAccessible(true);
        requiredFeaturesPropertiesField.set(properties, FeatureFlags.VANILLA_SET);

        Field itemDescriptionIdPropertiesField = Item.Properties.class.getDeclaredField("ITEM_DESCRIPTION_ID");
        itemDescriptionIdPropertiesField.setAccessible(true);

        Field descriptionIdPropertiesField = Item.Properties.class.getDeclaredField("descriptionId");
        descriptionIdPropertiesField.setAccessible(true);
        descriptionIdPropertiesField.set(properties, itemDescriptionIdPropertiesField.get(null));

        Items;
        Field modelPropertiesField = Item.Properties.class.getDeclaredField("model");
        modelPropertiesField.setAccessible(true);
        modelPropertiesField.set(properties, (DependantName<Item, ResourceLocation>) ResourceKey::location);

        new Item.Properties().setId(resourceKey);
        return null;
    }*/

    private Method getUnboundMethod() throws NoSuchMethodException
    {
        for (Class<?> clazz : MappedRegistry.class.getDeclaredClasses())
        {
            if (clazz.getSimpleName().equals("TagSet"))
            {
                return clazz.getDeclaredMethod("unbound");
            }
        }

        throw new IllegalArgumentException("Could not find method TagSet#unbound!");
    }

    @Override
    public void freezeItemRegistry()
    {
        try
        {
            MappedRegistry<Item> ITEMS = (MappedRegistry<Item>) BuiltInRegistries.ITEM;

            Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
            frozenField.setAccessible(true);
            frozenField.set(ITEMS, true);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            System.out.println("Could not freeze registry:");
            e.printStackTrace();
        }
    }
}
