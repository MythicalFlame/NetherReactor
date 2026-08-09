package me.mythicalflame.netherreactor.content;

import net.kyori.adventure.key.Key;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class ModdedBlock
{
    private final ModdedBlockState[] STATE_LIST;
    private final BlockProperties BLOCK_PROPERTIES;

    public ModdedBlock(ModdedBlockState[] stateList, BlockProperties blockProperties)
    {
        this.STATE_LIST = stateList;
        this.BLOCK_PROPERTIES = blockProperties;
    }

    public ModdedBlockState[] getBlockStates()
    {
        return this.STATE_LIST;
    }

    public BlockProperties getBlockProperties()
    {
        return this.BLOCK_PROPERTIES;
    }

    public static class BlockProperties
    {
        private final @Nonnull Key KEY;
        private Function<BlockStateContext, String> mapColorFunction = null;
        private Boolean hasNoCollision = null;
        private Boolean hasNoOcclusion = null;
        private Float friction = null;
        private Float speedFactor = null;
        private Float jumpFactor = null;
        private Function<BlockStateContext, Integer> lightEmissionFunction = null;
        private Boolean ignitedByLava = null;
        private Boolean liquid = null;
        private Boolean forceSolidOn = null;
        private PushReaction pushReaction = null;
        private Function<BlockStateContext, Boolean> isValidSpawn = null;
        private Function<BlockStateContext, Boolean> isRedstoneConducting = null;
        private Function<BlockStateContext, Boolean> isSuffocating = null;
        private Function<BlockStateContext, Boolean> isViewBlocking = null;
        private Boolean requiresCorrectToolForDrops = null;
        private Float destroyTime = null;
        private Float explosionResistance = null;

        public BlockProperties(Key key)
        {
            this.KEY = key;
        }

        public Key getKey()
        {
            return this.KEY;
        }

        public Function<BlockStateContext, String> getMapColorFunction()
        {
            return mapColorFunction;
        }

        public Boolean getHasNoCollision()
        {
            return hasNoCollision;
        }

        public Boolean getHasNoOcclusion()
        {
            return hasNoOcclusion;
        }

        public Float getFriction()
        {
            return friction;
        }

        public Float getSpeedFactor()
        {
            return speedFactor;
        }

        public Float getJumpFactor()
        {
            return jumpFactor;
        }

        public Function<BlockStateContext, Integer> getLightEmissionFunction()
        {
            return lightEmissionFunction;
        }

        public Boolean getIgnitedByLava()
        {
            return ignitedByLava;
        }

        public Boolean getLiquid()
        {
            return liquid;
        }

        public Boolean getForceSolidOn()
        {
            return forceSolidOn;
        }

        public PushReaction getPushReaction()
        {
            return pushReaction;
        }

        public Function<BlockStateContext, Boolean> getIsValidSpawn()
        {
            return isValidSpawn;
        }

        public Function<BlockStateContext, Boolean> getIsRedstoneConducting()
        {
            return isRedstoneConducting;
        }

        public Function<BlockStateContext, Boolean> getIsSuffocating()
        {
            return isSuffocating;
        }

        public Function<BlockStateContext, Boolean> getIsViewBlocking()
        {
            return isViewBlocking;
        }

        public Boolean getRequiresCorrectToolForDrops()
        {
            return requiresCorrectToolForDrops;
        }

        public Float getDestroyTime()
        {
            return destroyTime;
        }

        public Float getExplosionResistance()
        {
            return explosionResistance;
        }

        public ModdedBlock.BlockProperties setMapColorFunction(Function<BlockStateContext, String> mapColorFunction)
        {
            this.mapColorFunction = mapColorFunction;
            return this;
        }

        public ModdedBlock.BlockProperties setHasNoCollision(Boolean hasNoCollision)
        {
            this.hasNoCollision = hasNoCollision;
            this.hasNoOcclusion = false;
            return this;
        }

        public ModdedBlock.BlockProperties setHasNoOcclusion(Boolean hasNoOcclusion)
        {
            this.hasNoOcclusion = hasNoOcclusion;
            return this;
        }

        public ModdedBlock.BlockProperties setFriction(Float friction)
        {
            this.friction = friction;
            return this;
        }

        public ModdedBlock.BlockProperties setSpeedFactor(Float speedFactor)
        {
            this.speedFactor = speedFactor;
            return this;
        }

        public ModdedBlock.BlockProperties setJumpFactor(Float jumpFactor)
        {
            this.jumpFactor = jumpFactor;
            return this;
        }

        public ModdedBlock.BlockProperties setLightEmissionFunction(Function<BlockStateContext, Integer> lightEmissionFunction)
        {
            this.lightEmissionFunction = lightEmissionFunction;
            return this;
        }

        public ModdedBlock.BlockProperties setIgnitedByLava(Boolean ignitedByLava)
        {
            this.ignitedByLava = ignitedByLava;
            return this;
        }

        public ModdedBlock.BlockProperties setLiquid(Boolean liquid)
        {
            this.liquid = liquid;
            return this;
        }

        public ModdedBlock.BlockProperties setForceSolidOn(Boolean forceSolidOn)
        {
            this.forceSolidOn = forceSolidOn;
            return this;
        }

        public ModdedBlock.BlockProperties setPushReaction(PushReaction pushReaction)
        {
            this.pushReaction = pushReaction;
            return this;
        }

        public ModdedBlock.BlockProperties setIsValidSpawn(Function<BlockStateContext, Boolean> isValidSpawn)
        {
            this.isValidSpawn = isValidSpawn;
            return this;
        }

        public ModdedBlock.BlockProperties setIsRedstoneConducting(Function<BlockStateContext, Boolean> isRedstoneConducting)
        {
            this.isRedstoneConducting = isRedstoneConducting;
            return this;
        }

        public ModdedBlock.BlockProperties setIsSuffocating(Function<BlockStateContext, Boolean> isSuffocating)
        {
            this.isSuffocating = isSuffocating;
            return this;
        }

        public ModdedBlock.BlockProperties setIsViewBlocking(Function<BlockStateContext, Boolean> isViewBlocking)
        {
            this.isViewBlocking = isViewBlocking;
            return this;
        }

        public ModdedBlock.BlockProperties setRequiresCorrectToolForDrops(Boolean requiresCorrectToolForDrops)
        {
            this.requiresCorrectToolForDrops = requiresCorrectToolForDrops;
            return this;
        }

        public ModdedBlock.BlockProperties setDestroyTime(Float destroyTime)
        {
            this.destroyTime = destroyTime;
            return this;
        }

        public ModdedBlock.BlockProperties setExplosionResistance(Float explosionResistance)
        {
            this.explosionResistance = explosionResistance;
            return this;
        }
    }

    public static final class ModdedBlockState
    {
        private final String NAME;
        private final BlockStateType TYPE;
        private final Object DEFAULT_DATA;

        public ModdedBlockState(String name, BlockStateType type, Object defaultData)
        {
            if (type == BlockStateType.BOOLEAN_PROPERTY)
            {
                if (!(defaultData instanceof Boolean))
                {
                    throw new IllegalArgumentException("Tried to instantiate a boolean block state with non-boolean default data.");
                }
            }
            else if (type == BlockStateType.INTEGER_PROPERTY)
            {
                if (!(defaultData instanceof Integer))
                {
                    throw new IllegalArgumentException("Tried to instantiate a integer block state with non-integer default data.");
                }
            }

            if (!name.matches("^[a-z0-9_.-]*$"))
            {
                throw new IllegalArgumentException("Tried to instantiate a block state with illegal name \"" + name + "\".");
            }

            this.NAME = name;
            this.TYPE = type;
            this.DEFAULT_DATA = defaultData;
        }

        public String getName()
        {
            return this.NAME;
        }

        public BlockStateType getType()
        {
            return this.TYPE;
        }

        public Object getDefaultData()
        {
            return this.DEFAULT_DATA;
        }

        public enum BlockStateType
        {
            BOOLEAN_PROPERTY,
            INTEGER_PROPERTY
        }
    }

    public interface BlockStateContext
    {
        Object getValue(String property) throws NoSuchFieldException, IllegalAccessException;
    }

    public enum PushReaction
    {
        NORMAL,
        DESTROY,
        BLOCK,
        IGNORE,
        PUSH_ONLY
    }
}
