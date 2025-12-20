package me.mythicalflame.netherreactor.listeners;

import me.mythicalflame.netherreactor.content.ModdedItem;
import me.mythicalflame.netherreactor.utilities.NetherReactorAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.event.EventPriority.HIGHEST;

public final class CompostingWatcher implements Listener
{
    @EventHandler(priority = HIGHEST, ignoreCancelled = true)
    public void onCompost(PlayerInteractEvent event)
    {
        if (event.getHand() != EquipmentSlot.HAND)
        {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Block block = event.getClickedBlock();
            if (block.getType() == Material.COMPOSTER)
            {
                ModdedItem moddedItem = NetherReactorAPI.getModdedItem(event.getItem());
                if (moddedItem != null)
                {
                    event.setCancelled(true);
                    ItemStack oldStack = event.getPlayer().getInventory().getItemInMainHand();
                    oldStack.setAmount(oldStack.getAmount() - 1);
                    event.getPlayer().getInventory().setItemInMainHand(oldStack);

                    int chance = moddedItem.getCompostingChance();
                    int rng = (int) (Math.random() * 100);
                    if (rng < chance)
                    {
                        Levelled level = (Levelled) block.getBlockData();
                        level.setLevel(level.getLevel() + 1);
                        block.setBlockData(level);
                    }
                }
            }
        }
    }

    public static int getCompostingChance(Material material)
    {
        if (material == null)
        {
            return 0;
        }

        if (material == Material.valueOf("BUSH"))
        {
            return 30; //REMOVE WHEN UPDATED TO 1.21.5 MINIMUM VERSION
        }
        if (material == Material.valueOf("CACTUS_FLOWER"))
        {
            return 30; //REMOVE WHEN UPDATED TO 1.21.5 MINIMUM VERSION
        }
        if (material == Material.valueOf("FIREFLY_BUSH"))
        {
            return 30; //REMOVE WHEN UPDATED TO 1.21.5 MINIMUM VERSION
        }
        if (material == Material.valueOf("LEAF_LITTER"))
        {
            return 30; //REMOVE WHEN UPDATED TO 1.21.5 MINIMUM VERSION
        }
        if (material == Material.valueOf("SHORT_DRY_GRASS"))
        {
            return 30; //REMOVE WHEN UPDATED TO 1.21.5 MINIMUM VERSION
        }
        if (material == Material.valueOf("TALL_DRY_GRASS"))
        {
            return 30; //REMOVE WHEN UPDATED TO 1.21.5 MINIMUM VERSION
        }
        if (material == Material.valueOf("WILDFLOWERS"))
        {
            return 30; //REMOVE WHEN UPDATED TO 1.21.5 MINIMUM VERSION
        }
        return switch (material)
        {
            case BEETROOT_SEEDS, DRIED_KELP, GLOW_BERRIES, HANGING_ROOTS, KELP, ACACIA_LEAVES, AZALEA_LEAVES,
                 BIRCH_LEAVES, CHERRY_LEAVES, DARK_OAK_LEAVES, JUNGLE_LEAVES, MANGROVE_LEAVES, OAK_LEAVES,
                 PALE_OAK_LEAVES, SPRUCE_LEAVES, MANGROVE_PROPAGULE, MANGROVE_ROOTS, MELON_SEEDS, MOSS_CARPET,
                 PALE_HANGING_MOSS, PALE_MOSS_CARPET, PINK_PETALS, PITCHER_POD, PUMPKIN_SEEDS, ACACIA_SAPLING,
                 BAMBOO_SAPLING, BIRCH_SAPLING, CHERRY_SAPLING, DARK_OAK_SAPLING, JUNGLE_SAPLING, OAK_SAPLING,
                 PALE_OAK_SAPLING, SPRUCE_SAPLING, SEAGRASS, SHORT_GRASS, SMALL_DRIPLEAF, SWEET_BERRIES,
                 TORCHFLOWER_SEEDS, WHEAT_SEEDS -> 30;
            case CACTUS, DRIED_KELP_BLOCK, FLOWERING_AZALEA_LEAVES, GLOW_LICHEN, MELON_SLICE, NETHER_SPROUTS,
                 SUGAR_CANE, TALL_GRASS, TWISTING_VINES, VINE, WEEPING_VINES -> 50;
            case APPLE, AZALEA, BEETROOT, BIG_DRIPLEAF, CARROT, COCOA_BEANS, FERN, ALLIUM, AZURE_BLUET, BLUE_ORCHID,
                 CORNFLOWER, DANDELION, CLOSED_EYEBLOSSOM, OPEN_EYEBLOSSOM, LILY_OF_THE_VALLEY, OXEYE_DAISY, POPPY,
                 ORANGE_TULIP, PINK_TULIP, RED_TULIP, WHITE_TULIP, LILAC, PEONY, ROSE_BUSH, SUNFLOWER, LARGE_FERN,
                 LILY_PAD, MELON, MOSS_BLOCK, BROWN_MUSHROOM, RED_MUSHROOM, MUSHROOM_STEM, CRIMSON_FUNGUS,
                 WARPED_FUNGUS, NETHER_WART, PALE_MOSS_BLOCK, POTATO, PUMPKIN, CARVED_PUMPKIN, CRIMSON_ROOTS,
                 WARPED_ROOTS, SEA_PICKLE, SHROOMLIGHT, SPORE_BLOSSOM, WHEAT, WITHER_ROSE -> 65;
            case BAKED_POTATO, BREAD, COOKIE, FLOWERING_AZALEA, HAY_BLOCK, BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK,
                 NETHER_WART_BLOCK, PITCHER_PLANT, TORCHFLOWER, WARPED_WART_BLOCK -> 85;
            case CAKE, PUMPKIN_PIE -> 100;
            default -> 0;
        };
    }
}
