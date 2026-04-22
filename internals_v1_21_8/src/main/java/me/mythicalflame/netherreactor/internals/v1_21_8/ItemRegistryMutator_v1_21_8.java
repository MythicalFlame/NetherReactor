package me.mythicalflame.netherreactor.internals.v1_21_8;

import io.papermc.paper.datacomponent.PaperDataComponentType;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.registries.AbstractItemRegistryMutator;
import me.mythicalflame.netherreactor.registries.NetherReactorRegistry;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
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
            System.err.println("[NetherReactor] Could not initialize item registry injector!");
            e.printStackTrace();
            return;
        }

        initRegistries();

        mods.forEach(mod -> mod.getRegisteredItems().forEach(moddedItem ->
        {
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

                for (Map.Entry<Key, ?> componentEntry : moddedItem.getItemProperties().getComponents().entrySet())
                {
                    io.papermc.paper.datacomponent.DataComponentType apiType = Registry.DATA_COMPONENT_TYPE.get(componentEntry.getKey());
                    if (apiType instanceof io.papermc.paper.datacomponent.DataComponentType.NonValued nonValued)
                    {
                        setData(properties, nonValued);
                    }
                    else if (apiType instanceof io.papermc.paper.datacomponent.DataComponentType.Valued valued)
                    {
                        setData(properties, valued, componentEntry.getValue());
                    }
                }

                //TODO model and craftRemainder

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
            catch (Exception e)
            {
                System.err.println("[NetherReactor] Could not inject item " + moddedItemKey + " into the Minecraft Item registry!");
                e.printStackTrace();
                return;
            }

            //Bukkit injector
            try
            {
                Field itemMaterialField = CraftMagicNumbers.class.getDeclaredField("ITEM_MATERIAL");
                itemMaterialField.setAccessible(true);
                HashMap<Item, Material> ITEM_MATERIAL = (HashMap<Item, Material>) itemMaterialField.get(null);
                ITEM_MATERIAL.put(minecraftItem, moddedItem.getVanillaSettings().getDisguise());
            }
            catch (IllegalAccessException | NoSuchFieldException e)
            {
                System.err.println("[NetherReactor] Could not inject item " + moddedItemKey + " into the Bukkit Item->Material registry!");
                e.printStackTrace();
                return;
            }

            NetherReactorRegistry.Items.add(ITEMS.size() - 1, moddedItem);

            System.out.println("[NetherReactor] Registered item " + moddedItemKey + " successfully!");
        }));

        ITEMS.freeze();

        nullRegistries();
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

    //Adapted from Paper internals (CraftItemStack)
    private void setData(Item.Properties properties, io.papermc.paper.datacomponent.DataComponentType.NonValued type)
    {
        setDataInternal(properties, (PaperDataComponentType.NonValuedImpl) type, null);
    }

    private <T> void setData(Item.Properties properties, io.papermc.paper.datacomponent.DataComponentType.Valued<T> type, T value)
    {
        setDataInternal(properties, (PaperDataComponentType.ValuedImpl) type, value);
    }

    private <A, V> void setDataInternal(Item.Properties properties, PaperDataComponentType<A, V> type, A value)
    {
        properties.component(type.getHandle(), type.getAdapter().toVanilla(value, type.getHolder()));
    }
    //End Paper internals adaptation

    private void initRegistries()
    {
        CraftRegistry.setMinecraftRegistry(new RegistryAccess.ImmutableRegistryAccess(new ArrayList<>(List.of(
                BuiltInRegistries.GAME_EVENT,
                BuiltInRegistries.SOUND_EVENT,
                BuiltInRegistries.FLUID,
                BuiltInRegistries.MOB_EFFECT,
                BuiltInRegistries.BLOCK,
                BuiltInRegistries.ENTITY_TYPE,
                BuiltInRegistries.ITEM,
                BuiltInRegistries.POTION,
                BuiltInRegistries.PARTICLE_TYPE,
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                BuiltInRegistries.CUSTOM_STAT,
                BuiltInRegistries.CHUNK_STATUS,
                BuiltInRegistries.RULE_TEST,
                BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER,
                BuiltInRegistries.POS_RULE_TEST,
                BuiltInRegistries.MENU,
                BuiltInRegistries.RECIPE_TYPE,
                BuiltInRegistries.RECIPE_SERIALIZER,
                BuiltInRegistries.ATTRIBUTE,
                BuiltInRegistries.POSITION_SOURCE_TYPE,
                BuiltInRegistries.COMMAND_ARGUMENT_TYPE,
                BuiltInRegistries.STAT_TYPE,
                BuiltInRegistries.VILLAGER_TYPE,
                BuiltInRegistries.VILLAGER_PROFESSION,
                BuiltInRegistries.POINT_OF_INTEREST_TYPE,
                BuiltInRegistries.MEMORY_MODULE_TYPE,
                BuiltInRegistries.SENSOR_TYPE,
                BuiltInRegistries.SCHEDULE,
                BuiltInRegistries.ACTIVITY,
                BuiltInRegistries.LOOT_POOL_ENTRY_TYPE,
                BuiltInRegistries.LOOT_FUNCTION_TYPE,
                BuiltInRegistries.LOOT_CONDITION_TYPE,
                BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE,
                BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE,
                BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE,
                BuiltInRegistries.FLOAT_PROVIDER_TYPE,
                BuiltInRegistries.INT_PROVIDER_TYPE,
                BuiltInRegistries.HEIGHT_PROVIDER_TYPE,
                BuiltInRegistries.BLOCK_PREDICATE_TYPE,
                BuiltInRegistries.CARVER,
                BuiltInRegistries.FEATURE,
                BuiltInRegistries.STRUCTURE_PLACEMENT,
                BuiltInRegistries.STRUCTURE_PIECE,
                BuiltInRegistries.STRUCTURE_TYPE,
                BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,
                BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE,
                BuiltInRegistries.FOLIAGE_PLACER_TYPE,
                BuiltInRegistries.TRUNK_PLACER_TYPE,
                BuiltInRegistries.ROOT_PLACER_TYPE,
                BuiltInRegistries.TREE_DECORATOR_TYPE,
                BuiltInRegistries.FEATURE_SIZE_TYPE,
                BuiltInRegistries.BIOME_SOURCE,
                BuiltInRegistries.CHUNK_GENERATOR,
                BuiltInRegistries.MATERIAL_CONDITION,
                BuiltInRegistries.MATERIAL_RULE,
                BuiltInRegistries.DENSITY_FUNCTION_TYPE,
                BuiltInRegistries.BLOCK_TYPE,
                BuiltInRegistries.STRUCTURE_PROCESSOR,
                BuiltInRegistries.STRUCTURE_POOL_ELEMENT,
                BuiltInRegistries.POOL_ALIAS_BINDING_TYPE,
                BuiltInRegistries.DECORATED_POT_PATTERN,
                BuiltInRegistries.CREATIVE_MODE_TAB,
                BuiltInRegistries.TRIGGER_TYPES,
                BuiltInRegistries.NUMBER_FORMAT_TYPE,
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE,
                BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE,
                BuiltInRegistries.MAP_DECORATION_TYPE,
                BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
                BuiltInRegistries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE,
                BuiltInRegistries.ENCHANTMENT_ENTITY_EFFECT_TYPE,
                BuiltInRegistries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE,
                BuiltInRegistries.ENCHANTMENT_VALUE_EFFECT_TYPE,
                BuiltInRegistries.ENCHANTMENT_PROVIDER_TYPE,
                BuiltInRegistries.CONSUME_EFFECT_TYPE,
                BuiltInRegistries.RECIPE_DISPLAY,
                BuiltInRegistries.SLOT_DISPLAY,
                BuiltInRegistries.RECIPE_BOOK_CATEGORY,
                BuiltInRegistries.TICKET_TYPE,
                BuiltInRegistries.TEST_ENVIRONMENT_DEFINITION_TYPE,
                BuiltInRegistries.TEST_INSTANCE_TYPE,
                BuiltInRegistries.SPAWN_CONDITION_TYPE,
                BuiltInRegistries.DIALOG_TYPE,
                BuiltInRegistries.DIALOG_ACTION_TYPE,
                BuiltInRegistries.INPUT_CONTROL_TYPE,
                BuiltInRegistries.DIALOG_BODY_TYPE,
                BuiltInRegistries.TEST_FUNCTION,
                BuiltInRegistries.REGISTRY))));
    }

    private void nullRegistries()
    {
        try
        {
            Field registryField = CraftRegistry.class.getDeclaredField("registry");
            registryField.setAccessible(true);
            registryField.set(null, null);
        }
        catch (Exception e)
        {
            System.err.println("[NetherReactor] Could not empty CraftRegistry!");
            e.printStackTrace();
        }
    }

    @Override
    public Key getMaterialKey(org.bukkit.inventory.ItemStack stack)
    {
        ResourceLocation location = ITEMS.getKey(CraftItemStack.asNMSCopy(stack).getItem());
        return Key.key(location.getNamespace(), location.getPath());
    }
}
