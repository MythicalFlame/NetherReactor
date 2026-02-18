package me.mythicalflame.netherreactor.internals.v1_21_8;

import me.mythicalflame.netherreactor.api.content.Mod;
import me.mythicalflame.netherreactor.core.registries.AbstractEffectRegistryMutator;
import me.mythicalflame.netherreactor.core.registries.NetherReactorRegistry;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.potion.PotionEffectTypeCategory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

public class EffectRegistryMutator_v1_21_8 implements AbstractEffectRegistryMutator
{
    private MappedRegistry<MobEffect> EFFECTS;
    private Constructor<MobEffect> mobEffectConstructor;

    @Override
    public void unfreezeRegistry() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException
    {
        EFFECTS = (MappedRegistry<MobEffect>) BuiltInRegistries.MOB_EFFECT;

        Field frozenField = MappedRegistry.class.getDeclaredField("frozen");
        frozenField.setAccessible(true);
        frozenField.set(EFFECTS, false);

        Field allTagsField = MappedRegistry.class.getDeclaredField("allTags");
        allTagsField.setAccessible(true);

        Field frozenTagsField = MappedRegistry.class.getDeclaredField("frozenTags");
        frozenTagsField.setAccessible(true);

        mobEffectConstructor = MobEffect.class.getDeclaredConstructor(MobEffectCategory.class, int.class);
        mobEffectConstructor.setAccessible(true);
    }

    @Override
    public void registerEffects(Collection<Mod> mods)
    {
        try
        {
            unfreezeRegistry();
        }
        catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e)
        {
            System.out.println("[NetherReactor] Could not initialize effect registry injector!");
            e.printStackTrace();
            return;
        }

        mods.forEach(mod -> mod.getRegisteredEffects().forEach(moddedEffect -> {
            Key moddedEffectKey = moddedEffect.getKey();
            try
            {
                Field allTagsField = MappedRegistry.class.getDeclaredField("allTags");
                allTagsField.setAccessible(true);

                Field unregisteredIntrusiveHolders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
                unregisteredIntrusiveHolders.setAccessible(true);
                unregisteredIntrusiveHolders.set(EFFECTS, new IdentityHashMap<>());

                ResourceKey<MobEffect> resourceKey = ResourceKey.create(Registries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(moddedEffectKey.namespace(), moddedEffectKey.value()));

                MobEffect minecraftEffect = mobEffectConstructor.newInstance(convertPaperEffectCategory(moddedEffect.getCategory()), moddedEffect.getColor());
                moddedEffect.getAttributes().forEach(entry -> {
                    Holder<Attribute> attributeHolder = convertKeyAttribute(entry.getKey());
                    if (attributeHolder == null)
                    {
                        System.err.println("[NetherReactor] Could not find attribute \"" + entry.getKey() + "\"! Are you using an outdated version of Minecraft?");
                        return;
                    }

                    minecraftEffect.addAttributeModifier(
                            attributeHolder,
                            ResourceLocation.fromNamespaceAndPath(entry.getValue().key().namespace(), entry.getValue().key().value()),
                            entry.getValue().getAmount(),
                            convertPaperAttributeModifierOperation(entry.getValue().getOperation()));
                });

                Method unboundMethod = getUnboundMethod();
                unboundMethod.setAccessible(true);
                allTagsField.set(EFFECTS, unboundMethod.invoke(null));

                EFFECTS.createIntrusiveHolder(minecraftEffect);
                Holder<MobEffect> holder = EFFECTS.register(resourceKey, minecraftEffect, RegistrationInfo.BUILT_IN);

                Set<TagKey<MobEffect>> tags = new HashSet<>();
                Holder.direct(minecraftEffect).tags().forEach(tags::add);

                Method bindMethod = Holder.Reference.class.getDeclaredMethod("bindTags", Collection.class);
                bindMethod.setAccessible(true);
                bindMethod.invoke(holder, tags);

                unregisteredIntrusiveHolders.set(EFFECTS, null);
            }
            catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e)
            {
                System.out.println("[NetherReactor] Could not inject effect " + moddedEffectKey + " into the Minecraft Effect registry!");
                e.printStackTrace();
                return;
            }

            NetherReactorRegistry.Effects.add(moddedEffect);

            System.out.println("[NetherReactor] Registered effect " + moddedEffectKey + " successfully!");
        }));

        EFFECTS.freeze();
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

    private static MobEffectCategory convertPaperEffectCategory(PotionEffectTypeCategory paperCategory)
    {
        return switch (paperCategory)
        {
            case BENEFICIAL -> MobEffectCategory.BENEFICIAL;
            case HARMFUL -> MobEffectCategory.HARMFUL;
            case NEUTRAL -> MobEffectCategory.NEUTRAL;
        };
    }

    private static Holder<Attribute> convertKeyAttribute(Key attributeKey)
    {
        if (!attributeKey.namespace().equals("minecraft"))
        {
            return null;
        }

        if (attributeKey.equals(org.bukkit.attribute.Attribute.ARMOR)) return Attributes.ARMOR;
        else if (attributeKey.equals(Key.key("minecraft", "armor_toughness"))) return Attributes.ARMOR_TOUGHNESS;
        else if (attributeKey.equals(Key.key("minecraft", "attack_damage"))) return Attributes.ATTACK_DAMAGE;
        else if (attributeKey.equals(Key.key("minecraft", "attack_speed"))) return Attributes.ATTACK_SPEED;
        else if (attributeKey.equals(Key.key("minecraft", "attack_knockback"))) return Attributes.ATTACK_KNOCKBACK;
        else if (attributeKey.equals(Key.key("minecraft", "block_break_speed"))) return Attributes.BLOCK_BREAK_SPEED;
        else if (attributeKey.equals(Key.key("minecraft", "block_interaction_range"))) return Attributes.BLOCK_INTERACTION_RANGE;
        else if (attributeKey.equals(Key.key("minecraft", "burning_time"))) return Attributes.BURNING_TIME;
        else if (attributeKey.equals(Key.key("minecraft", "camera_distance"))) return Attributes.CAMERA_DISTANCE;
        else if (attributeKey.equals(Key.key("minecraft", "entity_interaction_range"))) return Attributes.ENTITY_INTERACTION_RANGE;
        else if (attributeKey.equals(Key.key("minecraft", "explosion_knockback_resistance"))) return Attributes.EXPLOSION_KNOCKBACK_RESISTANCE;
        else if (attributeKey.equals(Key.key("minecraft", "fall_damage_multiplier"))) return Attributes.FALL_DAMAGE_MULTIPLIER;
        else if (attributeKey.equals(Key.key("minecraft", "flying_speed"))) return Attributes.FLYING_SPEED;
        else if (attributeKey.equals(Key.key("minecraft", "follow_range"))) return Attributes.FOLLOW_RANGE;
        else if (attributeKey.equals(Key.key("minecraft", "gravity"))) return Attributes.GRAVITY;
        else if (attributeKey.equals(Key.key("minecraft", "jump_strength"))) return Attributes.JUMP_STRENGTH;
        else if (attributeKey.equals(Key.key("minecraft", "knockback_resistance"))) return Attributes.KNOCKBACK_RESISTANCE;
        else if (attributeKey.equals(Key.key("minecraft", "luck"))) return Attributes.LUCK;
        else if (attributeKey.equals(Key.key("minecraft", "max_absorption"))) return Attributes.MAX_ABSORPTION;
        else if (attributeKey.equals(Key.key("minecraft", "max_health"))) return Attributes.MAX_HEALTH;
        else if (attributeKey.equals(Key.key("minecraft", "mining_efficiency"))) return Attributes.MINING_EFFICIENCY;
        else if (attributeKey.equals(Key.key("minecraft", "movement_efficiency"))) return Attributes.MOVEMENT_EFFICIENCY;
        else if (attributeKey.equals(Key.key("minecraft", "movement_speed"))) return Attributes.MOVEMENT_SPEED;
        else if (attributeKey.equals(Key.key("minecraft", "oxygen_bonus"))) return Attributes.OXYGEN_BONUS;
        else if (attributeKey.equals(Key.key("minecraft", "safe_fall_distance"))) return Attributes.SAFE_FALL_DISTANCE;
        else if (attributeKey.equals(Key.key("minecraft", "scale"))) return Attributes.SCALE;
        else if (attributeKey.equals(Key.key("minecraft", "sneaking_speed"))) return Attributes.SNEAKING_SPEED;
        else if (attributeKey.equals(Key.key("minecraft", "spawn_reinforcements"))) return Attributes.SPAWN_REINFORCEMENTS_CHANCE;
        else if (attributeKey.equals(Key.key("minecraft", "step_height"))) return Attributes.STEP_HEIGHT;
        else if (attributeKey.equals(Key.key("minecraft", "submerged_mining_speed"))) return Attributes.SUBMERGED_MINING_SPEED;
        else if (attributeKey.equals(Key.key("minecraft", "sweeping_damage_ratio"))) return Attributes.SWEEPING_DAMAGE_RATIO;
        else if (attributeKey.equals(Key.key("minecraft", "tempt_range"))) return Attributes.TEMPT_RANGE;
        else if (attributeKey.equals(Key.key("minecraft", "water_movement_efficiency"))) return Attributes.WATER_MOVEMENT_EFFICIENCY;
        else if (attributeKey.equals(Key.key("minecraft", "waypoint_receive_range"))) return Attributes.WAYPOINT_RECEIVE_RANGE;
        else if (attributeKey.equals(Key.key("minecraft", "waypoint_transmit_range"))) return Attributes.WAYPOINT_TRANSMIT_RANGE;
        else return null;
    }

    private static AttributeModifier.Operation convertPaperAttributeModifierOperation(org.bukkit.attribute.AttributeModifier.Operation paperOperation)
    {
        return switch (paperOperation)
        {
            case ADD_NUMBER -> AttributeModifier.Operation.ADD_VALUE;
            case ADD_SCALAR -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case MULTIPLY_SCALAR_1 -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
        };
    }
}
