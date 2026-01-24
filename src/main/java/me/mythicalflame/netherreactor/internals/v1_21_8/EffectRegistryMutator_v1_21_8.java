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

        //TODO look into particleoptions constructor
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

                //TODO add particle options stuff and attributes
                MobEffect minecraftEffect = mobEffectConstructor.newInstance(convertPaperEffectCategory(moddedEffect.getCategory()), moddedEffect.getColor());

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
}
