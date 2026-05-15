package me.mythicalflame.netherreactor;

import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import javax.annotation.Nonnull;

/**
 * Miscellaneous API methods for NetherReactor.
 */
public final class NetherReactorAPI
{
    private NetherReactorAPI() {}

    /**
     * Awards a custom statistic to a player.
     *
     * @param player The player to award the statistic to.
     * @param key The key of the statistic.
     * @param amount The value to add to the statistic.
     */
    public static void awardStatistic(@Nonnull Player player, @Nonnull Key key, int amount)
    {
        InternalsManager.getStatisticMutator().awardStatistic(player, key, amount);
    }

    /**
     * Returns the true ItemType for a stack.
     *
     * @param stack The stack to get the ItemType for.
     * @return The true ItemType for this stack, or null if it cannot find it for some reason.
     */
    public static ItemType getItemType(ItemStack stack)
    {
        return Registry.ITEM.get(InternalsManager.getItemMutator().getMaterialKey(stack));
    }

    /**
     * Returns the true ItemType for a stack.
     *
     * @param stack The stack to get the ItemType for.
     * @return The true ItemType for this stack. If it cannot be found, throws an exception.
     */
    public static ItemType getItemTypeOrThrow(ItemStack stack)
    {
        return Registry.ITEM.getOrThrow(InternalsManager.getItemMutator().getMaterialKey(stack));
    }
}
