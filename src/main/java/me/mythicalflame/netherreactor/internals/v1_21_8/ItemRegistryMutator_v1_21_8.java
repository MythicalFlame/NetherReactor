package me.mythicalflame.netherreactor.internals.v1_21_8;

import me.mythicalflame.netherreactor.api.content.Mod;
import me.mythicalflame.netherreactor.core.registries.AbstractItemRegistryMutator;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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

//Inspired by ItemsAdderBlockInjector
public class ItemRegistryMutator_v1_21_8 implements AbstractItemRegistryMutator
{
    private MappedRegistry<Item> ITEMS;
    @Override
    public void unfreezeRegistry() throws NoSuchFieldException, IllegalAccessException
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

    @Override
    public void registerItems(Collection<Mod> mods)
    {
        try
        {
            unfreezeRegistry();
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            System.out.println("[NetherReactor] Could not initialize item registry injector!");
            e.printStackTrace();
            return;
        }

        mods.forEach(mod -> mod.getRegisteredItems().forEach(moddedItem -> {
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

            System.out.println("[NetherReactor] Registered item " + moddedItemKey + " successfully!");
        }));

        ITEMS.freeze();
    }

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
/*
    private DataComponentType thing(io.papermc.paper.datacomponent.DataComponentType paperType)
    {
        //custom data
        if (paperType.equals(DataComponentTypes.MAX_STACK_SIZE))
        {
            return DataComponents.MAX_STACK_SIZE;
        }
        else if (paperType.equals(DataComponentTypes.MAX_DAMAGE))
        {
            return DataComponents.MAX_DAMAGE;
        }
        else if (paperType.equals(DataComponentTypes.DAMAGE))
        {
            return DataComponents.DAMAGE;
        }
        else if (paperType.equals(DataComponentTypes.UNBREAKABLE))
        {
            return DataComponents.UNBREAKABLE;
        }
        else if (paperType.equals(DataComponentTypes.CUSTOM_NAME))
        {
            return DataComponents.CUSTOM_NAME;
        }
        else if (paperType.equals(DataComponentTypes.ITEM_NAME))
        {
            return DataComponents.ITEM_NAME;
        }
        else if (paperType.equals(DataComponentTypes.ITEM_MODEL))
        {
            return DataComponents.ITEM_MODEL;
        }
        else if (paperType.equals(DataComponentTypes.LORE))
        {
            return DataComponents.LORE;
        }
        else if (paperType.equals(DataComponentTypes.RARITY))
        {
            return DataComponents.RARITY;
        }
        else if (paperType.equals(DataComponentTypes.ENCHANTMENTS))
        {
            return DataComponents.ENCHANTMENTS;
        }
        else if (paperType.equals(DataComponentTypes.CAN_PLACE_ON))
        {
            return DataComponents.CAN_PLACE_ON;
        }
        else if (paperType.equals(DataComponentTypes.CAN_BREAK))
        {
            return DataComponents.CAN_BREAK;
        }
        else if (paperType.equals(DataComponentTypes.ATTRIBUTE_MODIFIERS))
        {
            return DataComponents.ATTRIBUTE_MODIFIERS;
        }
    }*/
}
