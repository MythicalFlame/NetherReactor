package me.mythicalflame.netherreactor.creative;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class CreativeTabs
{
    public final BuildingTab BUILDING_BLOCKS = new BuildingTab();
    public final ColoredTab COLORED_BLOCKS = new ColoredTab();
    public final NaturalTab NATURAL_BLOCKS = new NaturalTab();
    public final FunctionalTab FUNCTIONAL_BLOCKS = new FunctionalTab();
    public final RedstoneTab REDSTONE_BLOCKS = new RedstoneTab();
    public final ToolsTab TOOLS_AND_UTILITIES = new ToolsTab();
    public final CombatTab COMBAT = new CombatTab();
    public final FoodTab FOODS_AND_DRINKS = new FoodTab();
    public final IngredientsTab INGREDIENTS = new IngredientsTab();
    public final SpawnEggsTab SPAWN_EGGS = new SpawnEggsTab();
    public static class BuildingTab extends CreativeTab
    {
        public BuildingTab()
        {
            super("minecraft", "building_blocks", -100, new ItemStack(Material.BRICK), Component.translatable("itemGroup.buildingBlocks"));
        }
    }

    public static class ColoredTab extends CreativeTab
    {
        public ColoredTab()
        {
            super("minecraft", "colored_blocks", -99, new ItemStack(Material.CYAN_WOOL), Component.translatable("itemGroup.coloredBlocks"));
        }
    }

    public static class NaturalTab extends CreativeTab
    {
        public NaturalTab()
        {
            super("minecraft", "natural_blocks", -98, new ItemStack(Material.GRASS_BLOCK), Component.translatable("itemGroup.natural"));
        }
    }

    public static class FunctionalTab extends CreativeTab
    {
        public FunctionalTab()
        {
            super("minecraft", "functional_blocks", -97, new ItemStack(Material.OAK_SIGN), Component.translatable("itemGroup.functional"));
        }
    }

    public static class RedstoneTab extends CreativeTab
    {
        public RedstoneTab()
        {
            super("minecraft", "redstone_blocks", -96, new ItemStack(Material.REDSTONE), Component.translatable("itemGroup.redstone"));
        }
    }

    public static class ToolsTab extends CreativeTab
    {
        public ToolsTab()
        {
            super("minecraft", "tools_and_utilities", -95, new ItemStack(Material.DIAMOND_PICKAXE), Component.translatable("itemGroup.tools"));
        }
    }

    public static class CombatTab extends CreativeTab
    {
        public CombatTab()
        {
            super("minecraft", "combat", -94, new ItemStack(Material.NETHERITE_SWORD), Component.translatable("itemGroup.combat"));
        }
    }

    public static class FoodTab extends CreativeTab
    {
        public FoodTab()
        {
            super("minecraft", "foods_and_drinks", -93, new ItemStack(Material.GOLDEN_APPLE), Component.translatable("itemGroup.foodAndDrink"));
        }
    }

    public static class IngredientsTab extends CreativeTab
    {
        public IngredientsTab()
        {
            super("minecraft", "ingredients", -92, new ItemStack(Material.IRON_INGOT), Component.translatable("itemGroup.ingredients"));
        }
    }

    public static class SpawnEggsTab extends CreativeTab
    {
        public SpawnEggsTab()
        {
            super("minecraft", "spawn_eggs", -91, new ItemStack(Material.PIG_SPAWN_EGG), Component.translatable("itemGroup.spawnEggs"));
        }
    }
}
