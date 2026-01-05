package me.mythicalflame.netherreactor.internals.v1_21_8;

import me.mythicalflame.netherreactor.api.ModdedItem;
import me.mythicalflame.netherreactor.core.AbstractItemRegistryMutator;
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
