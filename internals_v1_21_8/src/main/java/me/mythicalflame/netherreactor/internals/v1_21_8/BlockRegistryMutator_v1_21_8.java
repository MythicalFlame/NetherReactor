package me.mythicalflame.netherreactor.internals.v1_21_8;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import me.mythicalflame.netherreactor.content.Mod;
import me.mythicalflame.netherreactor.content.ModdedBlock;
import me.mythicalflame.netherreactor.registries.AbstractBlockRegistryMutator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.bukkit.Material;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Function;

public class BlockRegistryMutator_v1_21_8 implements AbstractBlockRegistryMutator
{
    private MappedRegistry<Block> BLOCKS;

    public void unfreezeRegistry() throws NoSuchFieldException, IllegalAccessException
    {
        BLOCKS = (MappedRegistry<Block>) BuiltInRegistries.BLOCK;

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(BLOCKS, false);
    }

    @Override
    public void registerBlocks(Collection<Mod> mods, ComponentLogger logger) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException
    {
        unfreezeRegistry();

        ArrayList<Block> created = new ArrayList<>();
        for (Mod mod : mods)
        {
            for (ModdedBlock moddedBlock : mod.getRegisteredBlocks())
            {
                Key moddedBlockKey = moddedBlock.getBlockProperties().getKey();

                Field allTagsField = MappedRegistry.class.getDeclaredField("allTags");
                allTagsField.setAccessible(true);

                Field unregisteredIntrusiveHolders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
                unregisteredIntrusiveHolders.setAccessible(true);
                unregisteredIntrusiveHolders.set(BLOCKS, new IdentityHashMap<>());

                ResourceKey<Block> resourceKey = ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(moddedBlockKey.namespace(), moddedBlockKey.value()));

                Class<?> blockClass;
                try
                {
                    ClassPool pool = ClassPool.getDefault();
                    pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
                    CtClass ct = pool.makeClass("me.mythicalflame.netherreactor.internals.v1_21_8.GeneratedBlock_" + moddedBlockKey.namespace() + "_" + moddedBlockKey.value(), pool.getCtClass("net.minecraft.world.level.block.Block"));

                    for (ModdedBlock.ModdedBlockState state : moddedBlock.getBlockStates())
                    {
                        CtClass propertyCt;
                        if (state.getType() == ModdedBlock.ModdedBlockState.BlockStateType.BOOLEAN_PROPERTY)
                        {
                            propertyCt = pool.get("net.minecraft.world.level.block.state.properties.BooleanProperty");
                        }
                        else //INTEGER_PROPERTY
                        {
                            propertyCt = pool.get("net.minecraft.world.level.block.state.properties.IntegerProperty");
                        }
                        CtField stateField = new CtField(propertyCt, state.getName(), ct);
                        stateField.setModifiers(Modifier.PUBLIC);
                        stateField.setModifiers(Modifier.STATIC);
                        stateField.setModifiers(Modifier.FINAL);
                        ct.addField(stateField, CtField.Initializer.byCall(propertyCt, "create", new String[]{state.getName()}));
                    }

                    CtClass stateBuilderCt = pool.get("net.minecraft.world.level.block.state.StateDefinition$Builder");
                    CtMethod createStateMethod = new CtMethod(CtClass.voidType, "createBlockStateDefinition", new CtClass[]{stateBuilderCt}, ct);
                    StringBuilder createStateBody = new StringBuilder("{");
                    for (ModdedBlock.ModdedBlockState state : moddedBlock.getBlockStates())
                    {
                        createStateBody.append("$1.add(").append(state.getName()).append(");");
                    }
                    createStateBody.append("}");
                    createStateMethod.setBody(createStateBody.toString());

                    CtClass propertiesCt = pool.get("net.minecraft.world.level.block.state.BlockBehaviour$Properties");
                    CtConstructor constructor = new CtConstructor(new CtClass[]{propertiesCt}, ct);
                    StringBuilder constructorBody = new StringBuilder("{super($1);");
                    for (ModdedBlock.ModdedBlockState state : moddedBlock.getBlockStates())
                    {
                        constructorBody.append("registerDefaultState(defaultBlockState().setValue(").append(state.getName()).append(", ").append(state.getDefaultData()).append("));");
                    }
                    constructorBody.append("}");
                    constructor.setBody(constructorBody.toString());
                    ct.addConstructor(constructor);

                    blockClass = ct.toClass(BlockRegistryMutator_v1_21_8.class);
                }
                catch (Exception e)
                {
                    logger.error("Could not create class for block {}", moddedBlockKey, e);
                    throw new RuntimeException("Could not create class for block " + moddedBlockKey + "!");
                }

                allTagsField.set(BLOCKS, InternalInterface_v1_21_8.getUnboundMethod().invoke(null));

                BlockBehaviour.Properties minecraftProperties = moddedPropertyToMinecraftProperty(moddedBlock.getBlockProperties(), resourceKey);
                Block minecraftBlock = (Block) blockClass.getDeclaredConstructor(BlockBehaviour.Properties.class).newInstance(minecraftProperties);
                created.add(minecraftBlock);

                BLOCKS.createIntrusiveHolder(minecraftBlock);
                Holder<Block> holder = BLOCKS.register(resourceKey, minecraftBlock, RegistrationInfo.BUILT_IN);

                Set<TagKey<Block>> tags = new HashSet<>();
                Holder.direct(minecraftBlock).tags().forEach(tags::add);

                Method bindMethod = Holder.Reference.class.getDeclaredMethod("bindTags", Collection.class);
                bindMethod.setAccessible(true);
                bindMethod.invoke(holder, tags);

                unregisteredIntrusiveHolders.set(BLOCKS, null);

                //Bukkit injector
                //TODO disguise
                Field blockMaterialField = CraftMagicNumbers.class.getDeclaredField("BLOCK_MATERIAL");
                blockMaterialField.setAccessible(true);
                HashMap<Block, Material> blockMaterialMap = (HashMap<Block, Material>) blockMaterialField.get(null);
                blockMaterialMap.put(minecraftBlock, Material.COAL_BLOCK);

                logger.info("Registered block {} successfully!", moddedBlockKey);
            }
        }

        for (Block block : created)
        {
            for (BlockState state : block.getStateDefinition().getPossibleStates())
            {
                Block.BLOCK_STATE_REGISTRY.add(state);
                state.initCache();
            }
        }

        BLOCKS.freeze();
    }

    private static BlockBehaviour.Properties moddedPropertyToMinecraftProperty(ModdedBlock.BlockProperties moddedProperties, ResourceKey<Block> resourceKey)
    {
        BlockBehaviour.Properties minecraftProperties = BlockBehaviour.Properties.of();
        minecraftProperties.setId(resourceKey);

        //TODO better way to do colours?
        Function<ModdedBlock.BlockStateContext, String> mapColorFunction = moddedProperties.getMapColorFunction();
        if (mapColorFunction != null)
        {
            minecraftProperties.mapColor((state) -> {
                try
                {
                    Field field = MapColor.class.getField(mapColorFunction.apply(new BlockStateContext_v1_21_8(state)));
                    return (MapColor) field.get(null);
                }
                catch (Exception e)
                {
                    throw new IllegalArgumentException(e);
                }
            });
        }
        if (Boolean.TRUE.equals(moddedProperties.getHasNoCollision()))
        {
            minecraftProperties.noCollission();
        }
        if (Boolean.TRUE.equals(moddedProperties.getHasNoOcclusion()))
        {
            minecraftProperties.noOcclusion();
        }
        if (moddedProperties.getFriction() != null)
        {
            minecraftProperties.friction(moddedProperties.getFriction());
        }
        if (moddedProperties.getSpeedFactor() != null)
        {
            minecraftProperties.speedFactor(moddedProperties.getSpeedFactor());
        }
        if (moddedProperties.getJumpFactor() != null)
        {
            minecraftProperties.jumpFactor(moddedProperties.getJumpFactor());
        }
        Function<ModdedBlock.BlockStateContext, Integer> lightEmissionFunction = moddedProperties.getLightEmissionFunction();
        if (lightEmissionFunction != null)
        {
            minecraftProperties.lightLevel((state) -> lightEmissionFunction.apply(new BlockStateContext_v1_21_8(state)));
        }
        if (Boolean.TRUE.equals(moddedProperties.getIgnitedByLava()))
        {
            minecraftProperties.ignitedByLava();
        }
        if (Boolean.TRUE.equals(moddedProperties.getLiquid()))
        {
            minecraftProperties.liquid();
        }
        if (Boolean.TRUE.equals(moddedProperties.getForceSolidOn()))
        {
            minecraftProperties.forceSolidOn();
        }
        if (moddedProperties.getPushReaction() != null)
        {
            minecraftProperties.pushReaction(moddedPushReactionToMinecraftPushReaction(moddedProperties.getPushReaction()));
        }
        /*
        Function<ModdedBlock.BlockStateContext, Boolean> isValidSpawnFunction = moddedProperties.getIsValidSpawn();
        if (isValidSpawnFunction != null)
        {
            minecraftProperties.isValidSpawn((state) -> lightEmissionFunction.apply(new BlockStateContext_v1_21_8(state)));
        }
        Function<ModdedBlock.BlockStateContext, Boolean> isRedstoneConductingFunction = moddedProperties.getIsRedstoneConducting();
        if (isRedstoneConductingFunction != null)
        {
            minecraftProperties.isRedstoneConductor((state) -> isRedstoneConductingFunction.apply(new BlockStateContext_v1_21_8(state)));
        }
        Function<ModdedBlock.BlockStateContext, Boolean> lightEmissionFunction = moddedProperties.getLightEmissionFunction();
        if (lightEmissionFunction != null)
        {
            minecraftProperties.isSuffocating((state) -> lightEmissionFunction.apply(new BlockStateContext_v1_21_8(state)));
        }
        Function<ModdedBlock.BlockStateContext, Boolean> lightEmissionFunction = moddedProperties.getLightEmissionFunction();
        if (lightEmissionFunction != null)
        {
            minecraftProperties.isViewBlocking((state) -> lightEmissionFunction.apply(new BlockStateContext_v1_21_8(state)));
        }*/
        //valid spawn
        //conducting
        //suffocating
        //view blocking
        if (Boolean.TRUE.equals(moddedProperties.getRequiresCorrectToolForDrops()))
        {
            minecraftProperties.requiresCorrectToolForDrops();
        }
        if (moddedProperties.getDestroyTime() != null)
        {
            minecraftProperties.destroyTime(moddedProperties.getDestroyTime());
        }
        if (moddedProperties.getExplosionResistance() != null)
        {
            minecraftProperties.explosionResistance(moddedProperties.getExplosionResistance());
        }

        return minecraftProperties;
    }

    private static PushReaction moddedPushReactionToMinecraftPushReaction(ModdedBlock.PushReaction moddedReaction)
    {
        return switch (moddedReaction)
        {
            case BLOCK -> PushReaction.BLOCK;
            case IGNORE -> PushReaction.IGNORE;
            case NORMAL -> PushReaction.NORMAL;
            case DESTROY -> PushReaction.DESTROY;
            case PUSH_ONLY -> PushReaction.PUSH_ONLY;
        };
    }

    private static class BlockStateContext_v1_21_8 implements ModdedBlock.BlockStateContext
    {
        private final BlockState STATE;

        public BlockStateContext_v1_21_8(BlockState state)
        {
            this.STATE = state;
        }

        @Override
        public Object getValue(String property) throws NoSuchFieldException, IllegalAccessException
        {
            Field field = STATE.getBlock().getClass().getField(property);
            return STATE.getValue((Property<?>) field.get(null));
        }
    }
}
