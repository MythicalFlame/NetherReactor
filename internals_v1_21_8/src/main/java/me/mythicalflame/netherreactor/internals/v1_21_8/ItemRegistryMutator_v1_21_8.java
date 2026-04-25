package me.mythicalflame.netherreactor.internals.v1_21_8;

import io.papermc.paper.datacomponent.PaperDataComponentType;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.registries.AbstractItemRegistryMutator;
import me.mythicalflame.netherreactor.registries.NetherReactorRegistry;
import net.bytebuddy.agent.ByteBuddyAgent;
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
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.FuelValues;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

//Inspired by ItemsAdderBlockInjector
public class ItemRegistryMutator_v1_21_8 implements AbstractItemRegistryMutator
{
    private static final Logger log = LoggerFactory.getLogger(ItemRegistryMutator_v1_21_8.class);
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

        InternalInterface_v1_21_8.initRegistries();

        HashMap<Key, Integer> newFuelMap = new HashMap<>();

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

                if (moddedItem.getItemProperties().getCraftRemainder() != null)
                {
                    Key remainderKey = moddedItem.getItemProperties().getCraftRemainder();
                    ResourceLocation remainderLocation = ResourceLocation.fromNamespaceAndPath(remainderKey.namespace(), remainderKey.value());
                    Optional<Holder.Reference<Item>> found = BuiltInRegistries.ITEM.get(remainderLocation);
                    if (found.isEmpty())
                    {
                        throw new IllegalArgumentException("Could not find item " + remainderKey + "while trying to set the craftRemainder for " + moddedItemKey + "!");
                    }
                    properties.craftRemainder(found.get().value());
                }

                minecraftItem = new Item(properties);

                if (moddedItem.getItemProperties().getCompostingChance() != 0.0f)
                {
                    ComposterBlock.COMPOSTABLES.put(minecraftItem, moddedItem.getItemProperties().getCompostingChance());
                }

                if (moddedItem.getItemProperties().getFuelTime() > 0)
                {
                    newFuelMap.put(moddedItemKey, moddedItem.getItemProperties().getFuelTime());
                }

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

        if (!newFuelMap.isEmpty())
        {
            try
            {
                Instrumentation inst = ByteBuddyAgent.install();

                ClassPool pool = ClassPool.getDefault();
                pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
                CtClass ct = pool.get("net.minecraft.world.level.block.entity.FuelValues");
                CtMethod method = ct.getDeclaredMethod(
                        "vanillaBurnTimes",
                        new CtClass[] {
                                pool.get("net.minecraft.core.HolderLookup$Provider"),
                                pool.get("net.minecraft.world.flag.FeatureFlagSet"),
                                CtClass.intType
                        });

                StringBuilder methodBody = new StringBuilder("{return new net.minecraft.world.level.block.entity.FuelValues.Builder($1, $2)" +
                        ".add(net.minecraft.world.item.Items.LAVA_BUCKET, $3 * 100).add(net.minecraft.world.level.block.Blocks.COAL_BLOCK, $3 * 8 * 10).add(net.minecraft.world.item.Items.BLAZE_ROD, $3 * 12).add(net.minecraft.world.item.Items.COAL, $3 * 8).add(net.minecraft.world.item.Items.CHARCOAL, $3 * 8).add(net.minecraft.tags.ItemTags.LOGS, $3 * 3 / 2).add(net.minecraft.tags.ItemTags.BAMBOO_BLOCKS, $3 * 3 / 2).add(net.minecraft.tags.ItemTags.PLANKS, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.BAMBOO_MOSAIC, $3 * 3 / 2).add(net.minecraft.tags.ItemTags.WOODEN_STAIRS, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.BAMBOO_MOSAIC_STAIRS, $3 * 3 / 2).add(net.minecraft.tags.ItemTags.WOODEN_SLABS, $3 * 3 / 4).add(net.minecraft.world.level.block.Blocks.BAMBOO_MOSAIC_SLAB, $3 * 3 / 4).add(net.minecraft.tags.ItemTags.WOODEN_TRAPDOORS, $3 * 3 / 2).add(net.minecraft.tags.ItemTags.WOODEN_PRESSURE_PLATES, $3 * 3 / 2).add(net.minecraft.tags.ItemTags.WOODEN_FENCES, $3 * 3 / 2).add(net.minecraft.tags.ItemTags.FENCE_GATES, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.NOTE_BLOCK, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.BOOKSHELF, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.CHISELED_BOOKSHELF, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.LECTERN, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.JUKEBOX, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.CHEST, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.TRAPPED_CHEST, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.CRAFTING_TABLE, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.DAYLIGHT_DETECTOR, $3 * 3 / 2).add(net.minecraft.tags.ItemTags.BANNERS, $3 * 3 / 2).add(net.minecraft.world.item.Items.BOW, $3 * 3 / 2).add(net.minecraft.world.item.Items.FISHING_ROD, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.LADDER, $3 * 3 / 2).add(net.minecraft.tags.ItemTags.SIGNS, $3).add(net.minecraft.tags.ItemTags.HANGING_SIGNS, $3 * 4).add(net.minecraft.world.item.Items.WOODEN_SHOVEL, $3).add(net.minecraft.world.item.Items.WOODEN_SWORD, $3).add(net.minecraft.world.item.Items.WOODEN_HOE, $3).add(net.minecraft.world.item.Items.WOODEN_AXE, $3).add(net.minecraft.world.item.Items.WOODEN_PICKAXE, $3).add(net.minecraft.tags.ItemTags.WOODEN_DOORS, $3).add(net.minecraft.tags.ItemTags.BOATS, $3 * 6).add(net.minecraft.tags.ItemTags.WOOL, $3 / 2).add(net.minecraft.tags.ItemTags.WOODEN_BUTTONS, $3 / 2).add(net.minecraft.world.item.Items.STICK, $3 / 2).add(net.minecraft.tags.ItemTags.SAPLINGS, $3 / 2).add(net.minecraft.world.item.Items.BOWL, $3 / 2).add(net.minecraft.tags.ItemTags.WOOL_CARPETS, 1 + $3 / 3).add(net.minecraft.world.level.block.Blocks.DRIED_KELP_BLOCK, 1 + $3 * 20).add(net.minecraft.world.item.Items.CROSSBOW, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.BAMBOO, $3 / 4).add(net.minecraft.world.level.block.Blocks.DEAD_BUSH, $3 / 2).add(net.minecraft.world.level.block.Blocks.SHORT_DRY_GRASS, $3 / 2).add(net.minecraft.world.level.block.Blocks.TALL_DRY_GRASS, $3 / 2).add(net.minecraft.world.level.block.Blocks.SCAFFOLDING, $3 / 4).add(net.minecraft.world.level.block.Blocks.LOOM, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.BARREL, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.CARTOGRAPHY_TABLE, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.FLETCHING_TABLE, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.SMITHING_TABLE, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.COMPOSTER, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.AZALEA, $3 / 2).add(net.minecraft.world.level.block.Blocks.FLOWERING_AZALEA, $3 / 2).add(net.minecraft.world.level.block.Blocks.MANGROVE_ROOTS, $3 * 3 / 2).add(net.minecraft.world.level.block.Blocks.LEAF_LITTER, $3 / 2).remove(net.minecraft.tags.ItemTags.NON_FLAMMABLE_WOOD)");
                for (Map.Entry<Key, Integer> fuelEntry : newFuelMap.entrySet())
                {
                    Key fuelKey = fuelEntry.getKey();
                    //A mod could make a bad Key implementation and pass it to do code injection, but they could execute malicious code in the mod itself anyways
                    //However, might as well still check for it
                    if (!fuelKey.namespace().matches("^[a-z0-9_.-]*$"))
                    {
                        throw new IllegalArgumentException("Item namespace \"" + fuelKey.namespace() + "\" contains illegal characters!");
                    }
                    if (!fuelKey.value().matches("^[a-z0-9_.-/]*$"))
                    {
                        throw new IllegalArgumentException("Item ID \"" + fuelKey.value() + "\" contains illegal characters!");
                    }
                    methodBody.append(".add((net.minecraft.world.item.Item) ((net.minecraft.core.Holder.Reference) net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(\"").append(fuelKey.namespace()).append("\", \"").append(fuelKey.value()).append("\")).get()).value(), $3 * ").append(fuelEntry.getValue()).append("/ 200)");
                }
                methodBody.append(".build();}");
                method.setBody(methodBody.toString());

                inst.redefineClasses(new ClassDefinition(FuelValues.class, ct.toBytecode()));

                ct.detach();
            }
            catch (Exception e)
            {
                System.out.println("[NetherReactor] Could not rewrite FuelValues#vanillaBurnTimes!");
                e.printStackTrace();
            }
        }

        InternalInterface_v1_21_8.nullRegistries();
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

    @Override
    public Key getMaterialKey(org.bukkit.inventory.ItemStack stack)
    {
        ResourceLocation location = ITEMS.getKey(CraftItemStack.asNMSCopy(stack).getItem());
        return Key.key(location.getNamespace(), location.getPath());
    }
}
