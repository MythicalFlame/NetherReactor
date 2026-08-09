/*package me.mythicalflame.netherreactor.modules.vanilla;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.advancements.Advancement;
import com.github.retrooper.packetevents.protocol.advancements.AdvancementDisplay;
import com.github.retrooper.packetevents.protocol.advancements.AdvancementHolder;
import com.github.retrooper.packetevents.protocol.chat.ChatType;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleItemStackData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.recipe.RecipePropertySet;
import com.github.retrooper.packetevents.protocol.recipe.SingleInputOptionDisplay;
import com.github.retrooper.packetevents.protocol.recipe.data.MerchantOffer;
import com.github.retrooper.packetevents.protocol.recipe.display.FurnaceRecipeDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.RecipeDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.RecipeDisplayTypes;
import com.github.retrooper.packetevents.protocol.recipe.display.ShapedCraftingRecipeDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.ShapelessCraftingRecipeDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.SmithingRecipeDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.StonecutterRecipeDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.slot.CompositeSlotDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.slot.ItemSlotDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.slot.ItemStackSlotDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.slot.SlotDisplay;
import com.github.retrooper.packetevents.protocol.recipe.display.slot.SlotDisplayTypes;
import com.github.retrooper.packetevents.protocol.util.WeightedList;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BundleContents;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.SuspiciousStewEffects;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import me.mythicalflame.netherreactor.InternalsManager;
import me.mythicalflame.netherreactor.NetherReactorUtilities;
import me.mythicalflame.netherreactor.content.ModdedEffect;
import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.registries.NetherReactorRegistry;
import me.mythicalflame.netherreactor.modules.enderreactor.EnderReactorModule;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.UnsafeValues;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class BlockPacketInterceptor implements PacketListener
{
    @Override
    public void onPacketSend(PacketSendEvent event)
    {
        if (event.isCancelled())
        {
            return;
        }

        Player player = event.getPlayer();
        if (EnderReactorModule.hasPlayer(player))
        {
            return;
        }

        boolean hasChanged = false;

        if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE)
        {
            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event);
        }
        else if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE)
        {
            WrapperPlayServerMultiBlockChange packet = new WrapperPlayServerMultiBlockChange(event);
        }
        else if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK_BULK)
        {
            WrapperPlayServerChunkDataBulk packet = new WrapperPlayServerChunkDataBulk(event);
        }

        //particles and entity metadata
    }

    private boolean cleanItemStack(org.bukkit.inventory.ItemStack stack)
    {
        boolean result = false;
        if (!ignoreEffects)
        {
            result = cleanEffects(stack);
        }
        if (!ignoreItems)
        {
            result |= cleanMaterial(stack);
        }
        result |= cleanMiscData(stack);
        return result;
    }

    private boolean cleanEffects(org.bukkit.inventory.ItemStack stack)
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
            ArrayList<org.bukkit.potion.PotionEffect> potionEffects = new ArrayList<>(potionData.customEffects());
            ArrayList<org.bukkit.potion.PotionEffect> removedEffects = new ArrayList<>();

            for (int i = 0; i < potionEffects.size(); ++i)
            {
                if (NetherReactorRegistry.Effects.get(potionEffects.get(i).getType().key()) != null)
                {
                    result = true;
                    hasChanged = true;
                    removedEffects.add(potionEffects.get(i));
                    potionEffects.remove(i);
                    --i;
                }
            }

            if (hasChanged)
            {
                PotionContents.Builder newPotionData = PotionContents.potionContents()
                        .addCustomEffects(potionEffects)
                        .customName(potionData.customName())
                        .potion(potionData.potion());
                if (potionData.customColor() == null)
                {
                    newPotionData.customColor(getPotionColor(potionData.potion(), potionEffects, removedEffects));
                }
                else
                {
                    newPotionData.customColor(potionData.customColor());
                }
                stack.setData(DataComponentTypes.POTION_CONTENTS, newPotionData);
                List<Component> lore = new ArrayList<>(stack.getData(DataComponentTypes.LORE).lines());
                for (int i = removedEffects.size() - 1; i >= 0; --i)
                {
                    org.bukkit.potion.PotionEffect removedEffect = removedEffects.get(i);
                    lore.addFirst(potionEffectToComponent(removedEffect.getType().key(), removedEffect.getAmplifier(), removedEffect.getDuration()));
                }
                stack.setData(DataComponentTypes.LORE, ItemLore.lore(lore));

                if (potionData.potion() == null && potionEffects.isEmpty())
                {
                    //TODO remove reflection when 1.21.5 becomes min version
                    try
                    {
                        Object tooltipDisplay = DataComponentTypes.class.getField("TOOLTIP_DISPLAY").get(null);

                        Set<DataComponentType> hiddenComponents = new HashSet<>();
                        Object tooltipData = org.bukkit.inventory.ItemStack.class.getMethod("getData", DataComponentType.Valued.class).invoke(stack, tooltipDisplay);
                        Method getComponentsMethod = tooltipData.getClass().getDeclaredMethod("hiddenComponents");
                        getComponentsMethod.setAccessible(true);
                        Object oldHiddenComponents = getComponentsMethod.invoke(tooltipData);
                        Method addAllMethod = Set.class.getDeclaredMethod("addAll", Collection.class);
                        addAllMethod.setAccessible(true);
                        addAllMethod.invoke(hiddenComponents, oldHiddenComponents);
                        hiddenComponents.add(DataComponentTypes.POTION_CONTENTS);

                        Method builderMethod = Class.forName("io.papermc.paper.datacomponent.item.TooltipDisplay").getDeclaredMethod("tooltipDisplay");
                        builderMethod.setAccessible(true);
                        Object tooltipBuilder = builderMethod.invoke(null);
                        Method setComponentsMethod = tooltipBuilder.getClass().getDeclaredMethod("hiddenComponents", Set.class);
                        setComponentsMethod.setAccessible(true);
                        tooltipBuilder = setComponentsMethod.invoke(tooltipBuilder, hiddenComponents);
                        org.bukkit.inventory.ItemStack.class.getMethod("setData", DataComponentType.Valued.class, DataComponentBuilder.class)
                                .invoke(stack, tooltipDisplay, tooltipBuilder);
                    }
                    catch (Exception ignored) {}
                }
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

    private boolean cleanMaterial(org.bukkit.inventory.ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }

        Key key = InternalsManager.getItemMutator().getMaterialKey(stack);
        if (NetherReactorRegistry.Items.getByKey(key) != null)
        {
            boolean hasDefaultName;
            if (!stack.hasData(DataComponentTypes.ITEM_NAME))
            {
                hasDefaultName = true;
            }
            else
            {
                hasDefaultName = stack.getData(DataComponentTypes.ITEM_NAME).equals(translatable("item." + key.namespace() + "." + key.value()));
            }
            boolean hasDefaultModel;
            if (!stack.hasData(DataComponentTypes.ITEM_MODEL))
            {
                hasDefaultModel = true;
            }
            else
            {
                hasDefaultModel = stack.getData(DataComponentTypes.ITEM_MODEL).equals(key);
            }

            ModdedItem moddedItem = NetherReactorRegistry.Items.getByKey(key).getRight();
            ItemMeta meta = stack.getItemMeta();
            int amount = stack.getAmount();

            //Don't add removed components back
            HashMap<Key, Object> componentAddMap = new HashMap<>();
            for (Map.Entry<Key, Object> componentEntry : moddedItem.getItemProperties().getComponents().entrySet())
            {
                DataComponentType componentType = Registry.DATA_COMPONENT_TYPE.get(componentEntry.getKey());
                if (componentType == null)
                {
                    continue;
                }
                if (!stack.hasData(componentType))
                {
                    continue;
                }

                if (componentType instanceof DataComponentType.NonValued)
                {
                    componentAddMap.put(componentEntry.getKey(), componentEntry.getValue());
                }
                else if (componentType instanceof DataComponentType.Valued valued && stack.getData(valued).equals(componentEntry.getValue()))
                {
                    componentAddMap.put(componentEntry.getKey(), componentEntry.getValue());
                }
            }

            stack.setType(Material.AIR);
            stack.setType(moddedItem.getVanillaSettings().getDisguise());
            stack.setItemMeta(meta);
            stack.setAmount(amount);

            for (Map.Entry<Key, Object> componentEntry : componentAddMap.entrySet())
            {
                DataComponentType componentType = Registry.DATA_COMPONENT_TYPE.get(componentEntry.getKey());
                switch (componentType)
                {
                    case DataComponentType.NonValued nonValued -> stack.setData(nonValued);
                    case DataComponentType.Valued valued -> stack.setData(valued, componentEntry.getValue());
                    case null, default -> {}
                }

            }

            if (hasDefaultName)
            {
                stack.setData(DataComponentTypes.ITEM_NAME, translatable("item." + key.namespace() + "." + key.value()));
            }

            if (hasDefaultModel)
            {
                stack.setData(DataComponentTypes.ITEM_MODEL, key);
            }

            return true;
        }

        return false;
    }

    private boolean cleanMiscData(org.bukkit.inventory.ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }

        boolean result = false;

        if (stack.hasData(DataComponentTypes.BUNDLE_CONTENTS))
        {
            boolean hasChanged = false;
            List<org.bukkit.inventory.ItemStack> contents = new ArrayList<>(stack.getData(DataComponentTypes.BUNDLE_CONTENTS).contents());
            for (org.bukkit.inventory.ItemStack bundleItem : contents)
            {
                if (cleanItemStack(bundleItem))
                {
                    hasChanged = true;
                }
            }

            if (hasChanged)
            {
                result = true;
                stack.setData(DataComponentTypes.BUNDLE_CONTENTS, BundleContents.bundleContents(contents));
            }
        }

        if (stack.hasData(DataComponentTypes.CONTAINER))
        {
            boolean hasChanged = false;
            List<org.bukkit.inventory.ItemStack> contents = new ArrayList<>(stack.getData(DataComponentTypes.CONTAINER).contents());
            for (org.bukkit.inventory.ItemStack containerItem : contents)
            {
                if (cleanItemStack(containerItem))
                {
                    hasChanged = true;
                }
            }

            if (hasChanged)
            {
                result = true;
                stack.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(contents));
            }
        }

        return result;
    }

    private boolean cleanSlotDisplay(SlotDisplay<?> display)
    {
        if (display.getType() == SlotDisplayTypes.ITEM)
        {
            ItemSlotDisplay itemDisplay = (ItemSlotDisplay) display;
            com.github.retrooper.packetevents.protocol.item.type.ItemType newType = SpigotConversionUtil.fromBukkitItemMaterial(SpigotConversionUtil.toBukkitItemMaterial(itemDisplay.getItem()));
            if (!itemDisplay.getItem().equals(newType))
            {
                itemDisplay.setItem(newType);
                return true;
            }
        }
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
        else if (display.getType() == SlotDisplayTypes.COMPOSITE)
        {
            CompositeSlotDisplay compositeDisplay = (CompositeSlotDisplay) display;
            boolean hasChanged = false;
            for (SlotDisplay<?> subDisplay : compositeDisplay.getContents())
            {
                hasChanged |= cleanSlotDisplay(subDisplay);
            }
            return hasChanged;
        }

        return false;
    }

    private Component cleanComponent(Component component)
    {
        if (component == null)
        {
            return null;
        }

        if (component.hoverEvent() != null)
        {
            if (component.hoverEvent().action() == HoverEvent.Action.SHOW_ITEM)
            {
                HoverEvent.ShowItem showItem = (HoverEvent.ShowItem) component.hoverEvent().value();

                //TODO remove reflection when min version 1.21.11
                try
                {
                    Method deserializeMethod = UnsafeValues.class.getDeclaredMethod("deserializeItemHover", HoverEvent.ShowItem.class);
                    deserializeMethod.setAccessible(true);
                    org.bukkit.inventory.ItemStack stack = (org.bukkit.inventory.ItemStack) deserializeMethod.invoke(Bukkit.getUnsafe(), showItem);
                    if (cleanItemStack(stack))
                    {
                        component = component.hoverEvent(stack);
                    }
                }
                catch (Exception ignored)
                {
                    //Ignore components I guess
                    component = component.hoverEvent(Registry.ITEM.get(showItem.item()).createItemStack(showItem.count()));
                }
            }
            else if (component.hoverEvent().action() == HoverEvent.Action.SHOW_TEXT)
            {
                Component showText = (Component) component.hoverEvent().value();
                Component newShowText = cleanComponent(showText);
                if (showText != newShowText)
                {
                    component = component.hoverEvent(newShowText);
                }
            }
        }

        List<Component> newChildren = new ArrayList<>(component.children().size());
        boolean childrenChanged = false;
        for (Component child : component.children())
        {
            Component cleanedChild = cleanComponent(child);
            newChildren.add(cleanedChild);
            if (child != cleanedChild)
            {
                childrenChanged = true;
            }
        }

        if (childrenChanged)
        {
            component = component.children(newChildren);
        }

        if (component instanceof TranslatableComponent translatable)
        {
            List<TranslationArgument> newArgs = new ArrayList<>(translatable.arguments().size());
            boolean argsChanged = false;
            for (TranslationArgument arg : translatable.arguments())
            {
                if (arg.value() instanceof Component argComponent)
                {
                    Component cleanedArg = cleanComponent(argComponent);
                    newArgs.add(TranslationArgument.component(cleanedArg));
                    if (argComponent != cleanedArg)
                    {
                        argsChanged = true;
                    }
                }
                else
                {
                    newArgs.add(arg);
                }
            }

            if (argsChanged)
            {
                component = translatable.arguments(newArgs);
            }
        }

        return component;
    }

    private static Color getPotionColor(PotionType potionType, ArrayList<org.bukkit.potion.PotionEffect> effects, ArrayList<org.bukkit.potion.PotionEffect> removed)
    {
        if (potionType == null && effects.isEmpty() && removed.isEmpty())
        {
            return null;
        }

        float red = 0F;
        float green = 0F;
        float blue = 0F;
        int numColors = 0;

        if (potionType != null)
        {
            for (org.bukkit.potion.PotionEffect effect : potionType.getPotionEffects())
            {
                if (!effect.hasParticles())
                {
                    continue;
                }
                Color color = effect.getType().getColor();
                for (int i = 0; i <= effect.getAmplifier(); ++i)
                {
                    red += color.getRed() / 255F;
                    green += color.getGreen() / 255F;
                    blue += color.getBlue() / 255F;
                    ++numColors;
                }
            }
        }

        for (org.bukkit.potion.PotionEffect effect : effects)
        {
            if (!effect.hasParticles())
            {
                continue;
            }
            Color color = effect.getType().getColor();
            for (int i = 0; i <= effect.getAmplifier(); ++i)
            {
                red += color.getRed() / 255F;
                green += color.getGreen() / 255F;
                blue += color.getBlue() / 255F;
                ++numColors;
            }
        }

        for (org.bukkit.potion.PotionEffect effect : removed)
        {
            if (!effect.hasParticles())
            {
                continue;
            }
            Color color = effect.getType().getColor();
            for (int i = 0; i <= effect.getAmplifier(); ++i)
            {
                red += color.getRed() / 255F;
                green += color.getGreen() / 255F;
                blue += color.getBlue() / 255F;
                ++numColors;
            }
        }

        red = (red / numColors) * 255F;
        green = (green / numColors) * 255F;
        blue = (blue / numColors) * 255F;

        return Color.fromRGB((int) red, (int) green, (int) blue);
    }

    private static Component potionEffectToComponent(Key key, int amplifier, Integer length)
    {
        TranslatableComponent.Builder component = translatable().key("effect." + key.namespace() + "." + key.value()).decoration(TextDecoration.ITALIC, false);
        if (NetherReactorRegistry.Effects.get(key).getRight().getCategory() == PotionEffectTypeCategory.HARMFUL)
        {
            component.color(NamedTextColor.RED);
        }
        else
        {
            component.color(NamedTextColor.BLUE);
        }

        if (amplifier > 0)
        {
            component.append(text().content(" " + NetherReactorUtilities.RomanNumeral.getRomanNumber(amplifier + 1)));
        }

        if (length != null)
        {

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
        }

        return component.build();
    }
}*/