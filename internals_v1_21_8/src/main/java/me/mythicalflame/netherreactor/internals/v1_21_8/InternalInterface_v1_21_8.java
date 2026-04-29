package me.mythicalflame.netherreactor.internals.v1_21_8;

import me.mythicalflame.netherreactor.registries.AbstractInternalInterface;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import org.bukkit.craftbukkit.CraftRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InternalInterface_v1_21_8 implements AbstractInternalInterface
{
    private static Method unboundMethod = null;

    static Method getUnboundMethod() throws NoSuchMethodException
    {
        if (unboundMethod != null)
        {
            return unboundMethod;
        }

        for (Class<?> clazz : MappedRegistry.class.getDeclaredClasses())
        {
            if (clazz.getSimpleName().equals("TagSet"))
            {
                unboundMethod = clazz.getDeclaredMethod("unbound");
                unboundMethod.setAccessible(true);
                return unboundMethod;
            }
        }

        throw new IllegalArgumentException("Could not find method TagSet#unbound!");
    }

    @Override
    public int getServerProtocolVersion()
    {
        return SharedConstants.getProtocolVersion();
    }

    @Override
    public void initRegistries()
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

    @Override
    public void nullRegistries() throws NoSuchFieldException, IllegalAccessException
    {
        Field registryField = CraftRegistry.class.getDeclaredField("registry");
        registryField.setAccessible(true);
        registryField.set(null, null);
    }
}
