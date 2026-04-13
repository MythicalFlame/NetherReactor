package me.mythicalflame.netherreactor.modules.vanilla;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.advancements.Advancement;
import com.github.retrooper.packetevents.protocol.advancements.AdvancementDisplay;
import com.github.retrooper.packetevents.protocol.advancements.AdvancementHolder;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleItemStackData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.recipe.SingleInputOptionDisplay;
import com.github.retrooper.packetevents.protocol.recipe.data.MerchantOffer;
import com.github.retrooper.packetevents.protocol.recipe.display.FurnaceRecipeDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.RecipeDisplayTypes;
import com.github.retrooper.packetevents.protocol.recipe.display.ShapedCraftingRecipeDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.ShapelessCraftingRecipeDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.SmithingRecipeDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.StonecutterRecipeDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.slot.ItemStackSlotDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.slot.SlotDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.slot.SlotDisplayTypes;
import com.github.retrooper.packetevents.protocol.util.WeightedList;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.SuspiciousStewEffects;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import me.mythicalflame.netherreactor.registries.NetherReactorRegistry;
import me.mythicalflame.netherreactor.modules.enderreactor.EnderReactorModule;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectTypeCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class EffectPacketInterceptor implements PacketListener
{
    @Override
    public void onPacketSend(PacketSendEvent event)
    {
        Player player = event.getPlayer();
        if (EnderReactorModule.hasPlayer(player))
        {
            return;
        }

        boolean hasChanged = false;

        if (event.getPacketType() == PacketType.Play.Server.REMOVE_ENTITY_EFFECT)
        {
            WrapperPlayServerRemoveEntityEffect packet = new WrapperPlayServerRemoveEntityEffect(event);
            if (NetherReactorRegistry.Effects.get(packet.getPotionType().getName().key()) != null)
            {
                event.setCancelled(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EFFECT)
        {
            WrapperPlayServerEntityEffect packet = new WrapperPlayServerEntityEffect(event);
            if (NetherReactorRegistry.Effects.get(packet.getPotionType().getName().key()) != null)
            {
                event.setCancelled(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS)
        {
            WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
            Optional<com.github.retrooper.packetevents.protocol.item.ItemStack> carriedOptional = packet.getCarriedItem();
            if (carriedOptional.isPresent())
            {
                org.bukkit.inventory.ItemStack carriedSlot = SpigotConversionUtil.toBukkitItemStack(carriedOptional.get());
                hasChanged = cleanItemStack(carriedSlot);
                if (hasChanged)
                {
                    packet.setCarriedItem(SpigotConversionUtil.fromBukkitItemStack(carriedSlot));
                }
            }

            List<com.github.retrooper.packetevents.protocol.item.ItemStack> windowSlotsPE = packet.getItems();
            ArrayList<org.bukkit.inventory.ItemStack> windowSlots = new ArrayList<>(windowSlotsPE.size());
            windowSlotsPE.forEach(peStack -> windowSlots.add(SpigotConversionUtil.toBukkitItemStack(peStack)));
            for (org.bukkit.inventory.ItemStack stack : windowSlots)
            {
                hasChanged |= cleanItemStack(stack);
            }

            if (hasChanged)
            {
                windowSlotsPE.clear();
                windowSlots.forEach(stack -> windowSlotsPE.add(SpigotConversionUtil.fromBukkitItemStack(stack)));
                packet.setItems(windowSlotsPE);
                event.markForReEncode(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.SET_SLOT)
        {
            WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);

            org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(packet.getItem());
            hasChanged = cleanItemStack(stack);

            if (hasChanged)
            {
                packet.setItem(SpigotConversionUtil.fromBukkitItemStack(stack));
                event.markForReEncode(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.EXPLOSION)
        {
            WrapperPlayServerExplosion packet = new WrapperPlayServerExplosion(event);
            List<WeightedList.Entry<WrapperPlayServerExplosion.ParticleInfo>> particles = packet.getBlockParticles().getEntries();

            for (int i = 0; i < particles.size(); ++i)
            {
                WeightedList.Entry<WrapperPlayServerExplosion.ParticleInfo> particleEntry = particles.get(i);
                com.github.retrooper.packetevents.protocol.particle.Particle<?> particle = particleEntry.getValue().getParticle();
                if (particle.getType() == ParticleTypes.ITEM)
                {
                    com.github.retrooper.packetevents.protocol.particle.Particle<ParticleItemStackData> itemParticle = (com.github.retrooper.packetevents.protocol.particle.Particle<ParticleItemStackData>) particle;
                    ParticleItemStackData data = itemParticle.getData();
                    org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(data.getItemStack());
                    boolean changeParticle = cleanItemStack(stack);
                    hasChanged |= changeParticle;
                    if (changeParticle)
                    {
                        data.setItemStack(SpigotConversionUtil.fromBukkitItemStack(stack));
                        itemParticle.setData(data);
                        particles.set(i, particleEntry);
                    }
                }
            }

            if (hasChanged)
            {
                packet.setBlockParticles(new WeightedList<>(particles));
                event.markForReEncode(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.SET_CURSOR_ITEM)
        {
            WrapperPlayServerSetCursorItem packet = new WrapperPlayServerSetCursorItem(event);

            org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(packet.getStack());
            hasChanged = cleanItemStack(stack);

            if (hasChanged)
            {
                packet.setStack(SpigotConversionUtil.fromBukkitItemStack(stack));
                event.markForReEncode(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.MERCHANT_OFFERS)
        {
            WrapperPlayServerMerchantOffers packet = new WrapperPlayServerMerchantOffers(event);

            List<MerchantOffer> merchantOffers = packet.getMerchantOffers();
            for (MerchantOffer trade : merchantOffers)
            {
                org.bukkit.inventory.ItemStack result = SpigotConversionUtil.toBukkitItemStack(trade.getOutputItem());
                boolean changeStack = cleanItemStack(result);
                hasChanged |= changeStack;
                if (changeStack)
                {
                    trade.setOutputItem(SpigotConversionUtil.fromBukkitItemStack(result));
                }

                org.bukkit.inventory.ItemStack first = SpigotConversionUtil.toBukkitItemStack(trade.getFirstInputItem());
                changeStack = cleanItemStack(first);
                hasChanged |= changeStack;
                if (changeStack)
                {
                    trade.setFirstInputItem(SpigotConversionUtil.fromBukkitItemStack(first));
                }

                org.bukkit.inventory.ItemStack second = SpigotConversionUtil.toBukkitItemStack(trade.getSecondInputItem());
                changeStack = cleanItemStack(second);
                hasChanged |= changeStack;
                if (changeStack)
                {
                    trade.setSecondInputItem(SpigotConversionUtil.fromBukkitItemStack(second));
                }
            }

            if (hasChanged)
            {
                packet.setMerchantOffers(merchantOffers);
                event.markForReEncode(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.PARTICLE)
        {
            WrapperPlayServerParticle packet = new WrapperPlayServerParticle(event);
            com.github.retrooper.packetevents.protocol.particle.Particle<?> particle = packet.getParticle();
            if (particle.getType() == ParticleTypes.ITEM)
            {
                com.github.retrooper.packetevents.protocol.particle.Particle<ParticleItemStackData> itemParticle = (com.github.retrooper.packetevents.protocol.particle.Particle<ParticleItemStackData>) particle;
                ParticleItemStackData data = itemParticle.getData();
                org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(data.getItemStack());
                hasChanged = cleanItemStack(stack);
                if (hasChanged)
                {
                    data.setItemStack(SpigotConversionUtil.fromBukkitItemStack(stack));
                    itemParticle.setData(data);
                    packet.setParticle(itemParticle);
                    event.markForReEncode(true);
                }
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.SET_PLAYER_INVENTORY)
        {
            WrapperPlayServerSetPlayerInventory packet = new WrapperPlayServerSetPlayerInventory(event);

            org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(packet.getStack());
            hasChanged = cleanItemStack(stack);

            if (hasChanged)
            {
                packet.setStack(SpigotConversionUtil.fromBukkitItemStack(stack));
                event.markForReEncode(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA)
        {
            WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);

            List<EntityData<?>> metadata = packet.getEntityMetadata();

            for (EntityData<?> data : metadata)
            {
                if (data.getType() == EntityDataTypes.ITEMSTACK)
                {
                    EntityData<com.github.retrooper.packetevents.protocol.item.ItemStack> itemMetadata = (EntityData<com.github.retrooper.packetevents.protocol.item.ItemStack>) data;
                    org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(itemMetadata.getValue());
                    boolean changeData = cleanItemStack(stack);
                    hasChanged |= changeData;
                    if (changeData)
                    {
                        itemMetadata.setValue(SpigotConversionUtil.fromBukkitItemStack(stack));
                    }
                }
            }

            if (hasChanged)
            {
                packet.setEntityMetadata(metadata);
                event.markForReEncode(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT)
        {
            WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
            List<Equipment> equipment = packet.getEquipment();

            for (Equipment equip : equipment)
            {
                org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(equip.getItem());
                boolean equipmentChanged = cleanItemStack(stack);
                hasChanged |= equipmentChanged;
                if (equipmentChanged)
                {
                    equip.setItem(SpigotConversionUtil.fromBukkitItemStack(stack));
                }
            }

            if (hasChanged)
            {
                packet.setEquipment(equipment);
                event.markForReEncode(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.UPDATE_ADVANCEMENTS)
        {
            WrapperPlayServerUpdateAdvancements packet = new WrapperPlayServerUpdateAdvancements(event);
            List<AdvancementHolder> advancements = packet.getAddedAdvancements();

            for (AdvancementHolder advancementHolder : advancements)
            {
                Advancement advancement = advancementHolder.getAdvancement();
                AdvancementDisplay display = advancement.getDisplay();
                if (display == null)
                {
                    continue;
                }
                org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(display.getIcon());
                boolean changeIcon = cleanItemStack(stack);
                hasChanged |= changeIcon;
                if (changeIcon)
                {
                    display.setIcon(SpigotConversionUtil.fromBukkitItemStack(stack));
                }
            }

            if (hasChanged)
            {
                packet.setAddedAdvancements(advancements);
                event.markForReEncode(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.RECIPE_BOOK_ADD)
        {
            WrapperPlayServerRecipeBookAdd packet = new WrapperPlayServerRecipeBookAdd(event);
            List<WrapperPlayServerRecipeBookAdd.AddEntry> added = packet.getEntries();

            for (WrapperPlayServerRecipeBookAdd.AddEntry entry : added)
            {
                if (entry.getContents().getDisplay().getType() == RecipeDisplayTypes.STONECUTTER)
                {
                    StonecutterRecipeDisplay stonecutterDisplay = (StonecutterRecipeDisplay) entry.getContents().getDisplay();
                    SlotDisplay<?> inputDisplay = stonecutterDisplay.getInput();
                    SlotDisplay<?> resultDisplay = stonecutterDisplay.getResult();
                    hasChanged |= cleanSlotDisplay(inputDisplay);
                    hasChanged |= cleanSlotDisplay(resultDisplay);
                }
                else if (entry.getContents().getDisplay().getType() == RecipeDisplayTypes.CRAFTING_SHAPED)
                {
                    ShapedCraftingRecipeDisplay craftingDisplay = (ShapedCraftingRecipeDisplay) entry.getContents().getDisplay();
                    SlotDisplay<?> resultDisplay = craftingDisplay.getResult();
                    hasChanged |= cleanSlotDisplay(resultDisplay);
                    for (SlotDisplay<?> display : craftingDisplay.getIngredients())
                    {
                        hasChanged |= cleanSlotDisplay(display);
                    }
                }
                else if (entry.getContents().getDisplay().getType() == RecipeDisplayTypes.FURNACE)
                {
                    FurnaceRecipeDisplay furnaceDisplay = (FurnaceRecipeDisplay) entry.getContents().getDisplay();
                    SlotDisplay<?> fuelDisplay = furnaceDisplay.getFuel();
                    SlotDisplay<?> inputDisplay = furnaceDisplay.getIngredient();
                    SlotDisplay<?> resultDisplay = furnaceDisplay.getResult();
                    hasChanged |= cleanSlotDisplay(fuelDisplay);
                    hasChanged |= cleanSlotDisplay(inputDisplay);
                    hasChanged |= cleanSlotDisplay(resultDisplay);
                }
                else if (entry.getContents().getDisplay().getType() == RecipeDisplayTypes.CRAFTING_SHAPELESS)
                {
                    ShapelessCraftingRecipeDisplay craftingDisplay = (ShapelessCraftingRecipeDisplay) entry.getContents().getDisplay();
                    SlotDisplay<?> resultDisplay = craftingDisplay.getResult();
                    hasChanged |= cleanSlotDisplay(resultDisplay);
                    for (SlotDisplay<?> display : craftingDisplay.getIngredients())
                    {
                        hasChanged |= cleanSlotDisplay(display);
                    }
                }
                else if (entry.getContents().getDisplay().getType() == RecipeDisplayTypes.SMITHING)
                {
                    SmithingRecipeDisplay smithingDisplay = (SmithingRecipeDisplay) entry.getContents().getDisplay();
                    SlotDisplay<?> baseDisplay = smithingDisplay.getBase();
                    SlotDisplay<?> additionDisplay = smithingDisplay.getAddition();
                    SlotDisplay<?> resultDisplay = smithingDisplay.getResult();
                    hasChanged |= cleanSlotDisplay(baseDisplay);
                    hasChanged |= cleanSlotDisplay(additionDisplay);
                    hasChanged |= cleanSlotDisplay(resultDisplay);
                }
            }

            if (hasChanged)
            {
                packet.setEntries(added);
                event.markForReEncode(true);
            }
        }
        else if (event.getPacketType() == PacketType.Play.Server.DECLARE_RECIPES)
        {
            WrapperPlayServerDeclareRecipes packet = new WrapperPlayServerDeclareRecipes(event);
            List<SingleInputOptionDisplay> stonecutterRecipes = packet.getStonecutterRecipes();
            for (SingleInputOptionDisplay stonecutterRecipe : stonecutterRecipes)
            {
                SlotDisplay<?> display = stonecutterRecipe.getOptionDisplay();
                hasChanged |= cleanSlotDisplay(display);
            }

            if (hasChanged)
            {
                packet.setStonecutterRecipes(stonecutterRecipes);
                event.markForReEncode(true);
            }
        }
    }

    private boolean cleanSlotDisplay(SlotDisplay<?> display)
    {
        if (display.getType() == SlotDisplayTypes.ITEM_STACK)
        {
            ItemStackSlotDisplay stackDisplay = (ItemStackSlotDisplay) display;
            org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(stackDisplay.getStack());
            if (cleanItemStack(stack))
            {
                stackDisplay.setStack(SpigotConversionUtil.fromBukkitItemStack(stack));
                return true;
            }
        }

        return false;
    }

    //Removes non-vanilla effect data from the stack
    private boolean cleanItemStack(org.bukkit.inventory.ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }

        boolean result = false;

        if (stack.hasData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS))
        {
            boolean hasChanged = false;

            ArrayList<SuspiciousEffectEntry> susStewEffects = new ArrayList<>(stack.getData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS).effects());
            for (int i = 0; i < susStewEffects.size(); ++i)
            {
                if (NetherReactorRegistry.Effects.get(susStewEffects.get(i).effect().key()) != null)
                {
                    result = true;
                    hasChanged = true;
                    susStewEffects.remove(i);
                    --i;
                }
            }

            if (hasChanged)
            {
                stack.setData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.suspiciousStewEffects(susStewEffects));
            }
        }

        if (stack.hasData(DataComponentTypes.POTION_CONTENTS))
        {
            boolean hasChanged = false;
            PotionContents potionData = stack.getData(DataComponentTypes.POTION_CONTENTS);
            ArrayList<org.bukkit.potion.PotionEffect> potionEffects = new ArrayList<>();
            if (potionData.potion() != null)
            {
                potionEffects.addAll(potionData.potion().getPotionEffects());
            }
            potionEffects.addAll(potionData.customEffects());
            ArrayList<Pair<Key, Integer>> removedEffects = new ArrayList<>();

            for (int i = 0; i < potionEffects.size(); ++i)
            {
                if (NetherReactorRegistry.Effects.get(potionEffects.get(i).getType().key()) != null)
                {
                    result = true;
                    hasChanged = true;
                    removedEffects.add(Pair.of(potionEffects.get(i).getType().key(), potionEffects.get(i).getDuration()));
                    potionEffects.remove(i);
                    --i;
                }
            }

            //TODO 1.21.5: hide tooltip if empty for potion contents
            //TODO: calculate custom colour?
            if (hasChanged)
            {
                stack.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents()
                        .addCustomEffects(potionEffects)
                        .customColor(potionData.customColor())
                        .customName(potionData.customName())
                        .potion(potionData.potion()));
                List<Component> lore = new ArrayList<>(stack.getData(DataComponentTypes.LORE).lines());
                for (int i = removedEffects.size() - 1; i >= 0; --i)
                {
                    Pair<Key, Integer> removedEffect = removedEffects.get(i);
                    lore.addFirst(potionEffectToComponent(removedEffect.getLeft(), removedEffect.getRight()));
                }
                stack.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
            }
        }

        if (stack.hasData(DataComponentTypes.CONSUMABLE))
        {
            Consumable consumableData = stack.getData(DataComponentTypes.CONSUMABLE);
            ArrayList<ConsumeEffect> consumeEffects = new ArrayList<>(consumableData.consumeEffects());
            boolean hasChangedOuter = false;
            for (int i = 0; i < consumeEffects.size(); ++i)
            {
                if (!(consumeEffects.get(i) instanceof ConsumeEffect.ApplyStatusEffects statusEffect))
                {
                    continue;
                }

                boolean hasChangedInner = false;
                List<org.bukkit.potion.PotionEffect> potionEffects = statusEffect.effects();
                for (int j = 0; j < potionEffects.size(); ++j)
                {
                    org.bukkit.potion.PotionEffect potionEffect = potionEffects.get(j);
                    if (NetherReactorRegistry.Effects.get(potionEffect.getType().key()) != null)
                    {
                        result = true;
                        hasChangedInner = true;
                        hasChangedOuter = true;
                        potionEffects.remove(j);
                        --j;
                        break;
                    }
                }

                if (hasChangedInner)
                {
                    consumeEffects.set(i, ConsumeEffect.applyStatusEffects(potionEffects, statusEffect.probability()));
                }
            }

            if (hasChangedOuter)
            {
                stack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                        .addEffects(consumeEffects)
                        .sound(consumableData.sound())
                        .animation(consumableData.animation())
                        .consumeSeconds(consumableData.consumeSeconds())
                        .hasConsumeParticles(consumableData.hasConsumeParticles()));
            }
        }

        if (stack.hasData(DataComponentTypes.DEATH_PROTECTION))
        {
            ArrayList<ConsumeEffect> consumeEffects = new ArrayList<>(stack.getData(DataComponentTypes.DEATH_PROTECTION).deathEffects());
            boolean hasChangedOuter = false;
            for (int i = 0; i < consumeEffects.size(); ++i)
            {
                if (!(consumeEffects.get(i) instanceof ConsumeEffect.ApplyStatusEffects statusEffect))
                {
                    continue;
                }

                boolean hasChangedInner = false;
                List<org.bukkit.potion.PotionEffect> potionEffects = statusEffect.effects();
                for (int j = 0; j < potionEffects.size(); ++j)
                {
                    org.bukkit.potion.PotionEffect potionEffect = potionEffects.get(j);
                    if (NetherReactorRegistry.Effects.get(potionEffect.getType().key()) != null)
                    {
                        result = true;
                        hasChangedInner = true;
                        hasChangedOuter = true;
                        potionEffects.remove(j);
                        --j;
                    }
                }

                if (hasChangedInner)
                {
                    consumeEffects.set(i, ConsumeEffect.applyStatusEffects(potionEffects, statusEffect.probability()));
                }
            }

            if (hasChangedOuter)
            {
                stack.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection(consumeEffects));
            }
        }

        return result;
    }

    //TODO cache?
    private static Component potionEffectToComponent(Key key, int length)
    {
        TranslatableComponent.Builder component = translatable().key("effect." + key.namespace() + "." + key.value()).decoration(TextDecoration.ITALIC, false);
        if (NetherReactorRegistry.Effects.getEffects().get(key).getRight().getCategory() == PotionEffectTypeCategory.HARMFUL)
        {
            component.color(NamedTextColor.RED);
        }
        else
        {
            component.color(NamedTextColor.BLUE);
        }

        length /= 20;
        int minutes = length / 60;
        int seconds = length % 60;

        String minutesRep;
        if (minutes < 10)
        {
            minutesRep = "0" + minutes;
        }
        else
        {
            minutesRep = Integer.toString(minutes);
        }

        String secondsRep;
        if (seconds < 10)
        {
            secondsRep = "0" + seconds;
        }
        else
        {
            secondsRep = Integer.toString(seconds);
        }

        component.append(text().content(" (" + minutesRep + ":" + secondsRep + ")"));

        return component.build();
    }
}
