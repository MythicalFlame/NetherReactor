package me.mythicalflame.netherreactor.modules.vanilla;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedParticle;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.SuspiciousStewEffects;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import me.mythicalflame.netherreactor.registries.NetherReactorRegistry;
import me.mythicalflame.netherreactor.modules.enderreactor.EnderReactorModule;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class EffectPacketInterceptor extends PacketAdapter
{
    public EffectPacketInterceptor(Plugin plugin)
    {
        super(plugin, ListenerPriority.HIGHEST,
                PacketType.Play.Server.REMOVE_ENTITY_EFFECT,
                PacketType.Play.Server.ENTITY_EFFECT,
                PacketType.Play.Server.WINDOW_ITEMS,
                PacketType.Play.Server.SET_SLOT,
                PacketType.Play.Server.EXPLOSION,
                PacketType.Play.Server.SET_CURSOR_ITEM,
                PacketType.Play.Server.OPEN_WINDOW_MERCHANT,
                PacketType.Play.Server.WORLD_PARTICLES,
                PacketType.Play.Server.SET_PLAYER_INVENTORY,
                PacketType.Play.Server.ENTITY_METADATA,
                PacketType.Play.Server.ENTITY_EQUIPMENT,
                PacketType.Play.Server.ADVANCEMENTS,
                PacketType.Play.Server.RECIPE_BOOK_ADD,
                PacketType.Play.Server.RECIPE_UPDATE);
    }

    @Override
    public void onPacketSending(PacketEvent event)
    {
        Player player = event.getPlayer();
        if (EnderReactorModule.hasPlayer(player))
        {
            return;
        }
        PacketContainer packet = event.getPacket();

        if (event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT)
        {
            if (NetherReactorRegistry.Effects.get(packet.getEffectTypes().read(0).key()) != null)
            {
                event.setCancelled(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT)
        {
            if (NetherReactorRegistry.Effects.get(packet.getEffectTypes().read(0).key()) != null)
            {
                event.setCancelled(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS)
        {
            ItemStack carriedSlot = packet.getItemModifier().read(0);
            cleanItemStack(carriedSlot);
            packet.getItemModifier().write(0, carriedSlot);

            List<ItemStack> windowSlots = packet.getItemListModifier().read(0);
            for (ItemStack stack : windowSlots)
            {
                cleanItemStack(stack);
            }
            packet.getItemListModifier().write(0, windowSlots);
        }
        else if (event.getPacketType() == PacketType.Play.Server.SET_SLOT)
        {
            ItemStack slot = packet.getItemModifier().read(0);
            cleanItemStack(slot);
            packet.getItemModifier().write(0, slot);
        }
        else if (event.getPacketType() == PacketType.Play.Server.EXPLOSION)
        {
            //documentation for explosion from https://github.com/dmulloy2/PacketWrapper/pull/95/changes
            if (packet.getNewParticles().read(0).getData() instanceof ItemStack smallParticle)
            {
                cleanItemStack(smallParticle);
                packet.getNewParticles().write(0, WrappedParticle.create(Particle.ITEM, smallParticle));
            }

            if (packet.getNewParticles().read(1).getData() instanceof ItemStack largeParticle)
            {
                cleanItemStack(largeParticle);
                packet.getNewParticles().write(1, WrappedParticle.create(Particle.ITEM, largeParticle));
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.SET_CURSOR_ITEM)
        {
            ItemStack stack = packet.getItemModifier().read(0);
            cleanItemStack(stack);
            packet.getItemModifier().write(0, stack);
        }
        else if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW_MERCHANT)
        {
            List<MerchantRecipe> merchantRecipes = packet.getMerchantRecipeLists().read(0);
            for (int i = 0; i < merchantRecipes.size(); ++i)
            {
                MerchantRecipe oldTrade = merchantRecipes.get(i);
                ItemStack oldResult = oldTrade.getResult();
                cleanItemStack(oldResult);
                MerchantRecipe newTrade = new MerchantRecipe(oldResult, oldTrade.getUses(), oldTrade.getMaxUses(), oldTrade.hasExperienceReward(), oldTrade.getVillagerExperience(), oldTrade.getPriceMultiplier(), oldTrade.getDemand(), oldTrade.getSpecialPrice(), oldTrade.shouldIgnoreDiscounts());
                for (ItemStack ingredient : oldTrade.getIngredients())
                {
                    cleanItemStack(ingredient);
                    newTrade.addIngredient(ingredient);
                }
                merchantRecipes.set(i, newTrade);
            }
            packet.getMerchantRecipeLists().write(0, merchantRecipes);
        }
        else if (event.getPacketType() == PacketType.Play.Server.WORLD_PARTICLES)
        {
            if (packet.getNewParticles().read(0).getData() instanceof ItemStack itemParticle)
            {
                cleanItemStack(itemParticle);
                packet.getNewParticles().write(0, WrappedParticle.create(Particle.ITEM, itemParticle));
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.SET_PLAYER_INVENTORY)
        {
            ItemStack stack = packet.getItemModifier().read(0);
            cleanItemStack(stack);
            packet.getItemModifier().write(0, stack);
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA)
        {
            List<WrappedDataValue> metadata = packet.getDataValueCollectionModifier().read(0);

            for (WrappedDataValue value : metadata)
            {
                if (value.getValue() instanceof ItemStack stack)
                {
                    cleanItemStack(stack);
                }
            }
            packet.getDataValueCollectionModifier().write(0, metadata);
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT)
        {
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment = packet.getSlotStackPairLists().read(0);

            for (Pair<EnumWrappers.ItemSlot, ItemStack> pair : equipment)
            {
                ItemStack stack = pair.getSecond();
                cleanItemStack(stack);
            }
            packet.getSlotStackPairLists().write(0, equipment);
        }
        //TODO
        else if (event.getPacketType() == PacketType.Play.Server.ADVANCEMENTS)
        {
            Map<MinecraftKey, ?> advancements = packet.getMaps(MinecraftKey.getConverter(), null).read(0);
        }
        else if (event.getPacketType() == PacketType.Play.Server.RECIPE_BOOK_ADD)
        {

        }
        else if (event.getPacketType() == PacketType.Play.Server.RECIPE_UPDATE)
        {

        }
    }

    //Removes non-vanilla effect data from the stack
    private void cleanItemStack(ItemStack stack)
    {
        if (stack == null)
        {
            return;
        }

        if (stack.hasData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS))
        {
            ArrayList<SuspiciousEffectEntry> susStewEffects = new ArrayList<>(stack.getData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS).effects());
            for (int i = 0; i < susStewEffects.size(); ++i)
            {
                if (NetherReactorRegistry.Effects.get(susStewEffects.get(i).effect().key()) != null)
                {
                    susStewEffects.remove(i);
                    --i;
                }
            }
            stack.setData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.suspiciousStewEffects(susStewEffects));
        }

        if (stack.hasData(DataComponentTypes.POTION_CONTENTS))
        {
            PotionContents potionData = stack.getData(DataComponentTypes.POTION_CONTENTS);
            ArrayList<PotionEffect> potionEffects = new ArrayList<>();
            potionEffects.addAll(potionData.potion().getPotionEffects());
            potionEffects.addAll(potionData.customEffects());

            for (int i = 0; i < potionEffects.size(); ++i)
            {
                if (NetherReactorRegistry.Effects.get(potionEffects.get(i).getType().key()) != null)
                {
                    potionEffects.remove(i);
                    --i;
                }
            }
            stack.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents()
                    .addCustomEffects(potionEffects)
                    .customColor(potionData.customColor())
                    .customName(potionData.customName())
                    .potion(potionData.potion()));
        }

        if (stack.hasData(DataComponentTypes.CONSUMABLE))
        {
            Consumable consumableData = stack.getData(DataComponentTypes.CONSUMABLE);
            ArrayList<ConsumeEffect> consumeEffects = new ArrayList<>(consumableData.consumeEffects());
            for (int i = 0; i < consumeEffects.size(); ++i)
            {
                if (!(consumeEffects.get(i) instanceof ConsumeEffect.ApplyStatusEffects statusEffect))
                {
                    continue;
                }

                List<PotionEffect> potionEffects = statusEffect.effects();
                for (PotionEffect potionEffect : potionEffects)
                {
                    if (NetherReactorRegistry.Effects.get(potionEffect.getType().key()) != null)
                    {
                        consumeEffects.remove(i);
                        --i;
                        break;
                    }
                }
            }
            stack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .addEffects(consumeEffects)
                    .sound(consumableData.sound())
                    .animation(consumableData.animation())
                    .consumeSeconds(consumableData.consumeSeconds())
                    .hasConsumeParticles(consumableData.hasConsumeParticles()));
        }

        if (stack.hasData(DataComponentTypes.DEATH_PROTECTION))
        {
            ArrayList<ConsumeEffect> consumeEffects = new ArrayList<>(stack.getData(DataComponentTypes.DEATH_PROTECTION).deathEffects());
            for (int i = 0; i < consumeEffects.size(); ++i)
            {
                if (!(consumeEffects.get(i) instanceof ConsumeEffect.ApplyStatusEffects statusEffect))
                {
                    continue;
                }

                List<PotionEffect> potionEffects = statusEffect.effects();
                for (PotionEffect potionEffect : potionEffects)
                {
                    if (NetherReactorRegistry.Effects.get(potionEffect.getType().key()) != null)
                    {
                        consumeEffects.remove(i);
                        --i;
                        break;
                    }
                }
            }
            stack.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection(consumeEffects));
        }
    }
}
