package me.mythicalflame.netherreactor.creative;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class CreativeTabs
{
    public static final BuildingTab BUILDING_BLOCKS = new BuildingTab();
    public static final ColoredTab COLORED_BLOCKS = new ColoredTab();
    public static final NaturalTab NATURAL_BLOCKS = new NaturalTab();
    public static final FunctionalTab FUNCTIONAL_BLOCKS = new FunctionalTab();
    public static final RedstoneTab REDSTONE_BLOCKS = new RedstoneTab();
    public static final ToolsTab TOOLS_AND_UTILITIES = new ToolsTab();
    public static final CombatTab COMBAT = new CombatTab();
    public static final FoodTab FOODS_AND_DRINKS = new FoodTab();
    public static final IngredientsTab INGREDIENTS = new IngredientsTab();
    public static final SpawnEggsTab SPAWN_EGGS = new SpawnEggsTab();

    public static class BuildingTab extends CreativeTab
    {
        public BuildingTab()
        {
            super(Key.key("minecraft", "building_blocks"), -100, ItemStack.of(Material.BRICK), Component.translatable("itemGroup.buildingBlocks"));
        }
    }

    public static class ColoredTab extends CreativeTab
    {
        public ColoredTab()
        {
            super(Key.key("minecraft", "colored_blocks"), -99, ItemStack.of(Material.CYAN_WOOL), Component.translatable("itemGroup.coloredBlocks"));
        }
    }

    public static class NaturalTab extends CreativeTab
    {
        public NaturalTab()
        {
            super(Key.key("minecraft", "natural_blocks"), -98, ItemStack.of(Material.GRASS_BLOCK), Component.translatable("itemGroup.natural"));
        }
    }

    public static class FunctionalTab extends CreativeTab
    {
        public FunctionalTab()
        {
            super(Key.key("minecraft", "functional_blocks"), -97, ItemStack.of(Material.OAK_SIGN), Component.translatable("itemGroup.functional"));
        }
    }

    public static class RedstoneTab extends CreativeTab
    {
        public RedstoneTab()
        {
            super(Key.key("minecraft", "redstone_blocks"), -96, ItemStack.of(Material.REDSTONE), Component.translatable("itemGroup.redstone"));
        }
    }

    public static class ToolsTab extends CreativeTab
    {
        public ToolsTab()
        {
            super(Key.key("minecraft", "tools_and_utilities"), -95, ItemStack.of(Material.DIAMOND_PICKAXE), Component.translatable("itemGroup.tools"));
        }
    }

    public static class CombatTab extends CreativeTab
    {
        public CombatTab()
        {
            super(Key.key("minecraft", "combat"), -94, ItemStack.of(Material.NETHERITE_SWORD), Component.translatable("itemGroup.combat"));
        }
    }

    public static class FoodTab extends CreativeTab
    {
        public FoodTab()
        {
            super(Key.key("minecraft", "foods_and_drinks"), -93, ItemStack.of(Material.GOLDEN_APPLE), Component.translatable("itemGroup.foodAndDrink"));
        }
    }

    public static class IngredientsTab extends CreativeTab
    {
        public IngredientsTab()
        {
            super(Key.key("minecraft", "ingredients"), -92, ItemStack.of(Material.IRON_INGOT), Component.translatable("itemGroup.ingredients"));
        }
    }

    public static class SpawnEggsTab extends CreativeTab
    {
        public SpawnEggsTab()
        {
            super(Key.key("minecraft", "spawn_eggs"), -91, ItemStack.of(Material.PIG_SPAWN_EGG), Component.translatable("itemGroup.spawnEggs"));
        }
    }
}
