package com.skyblock.plugin.managers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class CraftingManager {

    private static final CraftingManager INSTANCE = new CraftingManager();

    private CraftingManager() {}

    public static CraftingManager getInstance() {
        return INSTANCE;
    }

    public void registerRecipes(JavaPlugin plugin) {
        registerEnchantedCobblestone(plugin);
        registerEnchantedOakLog(plugin);
        registerEnchantedIronIngot(plugin);
        registerEnchantedGoldIngot(plugin);
        registerEnchantedDiamond(plugin);
    }

    private void registerEnchantedCobblestone(JavaPlugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "enchanted_cobblestone");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.COBBLESTONE, 1));
        recipe.shape("AAA", "AAA", "AAA");
        recipe.setIngredient('A', Material.COBBLESTONE);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerEnchantedOakLog(JavaPlugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "enchanted_oak_log");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.OAK_LOG, 1));
        recipe.shape("AAA", "AAA", "AAA");
        recipe.setIngredient('A', Material.OAK_LOG);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerEnchantedIronIngot(JavaPlugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "enchanted_iron_ingot");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.IRON_INGOT, 1));
        recipe.shape("AAA", "AAA", "AAA");
        recipe.setIngredient('A', Material.IRON_INGOT);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerEnchantedGoldIngot(JavaPlugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "enchanted_gold_ingot");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.GOLD_INGOT, 1));
        recipe.shape("AAA", "AAA", "AAA");
        recipe.setIngredient('A', Material.GOLD_INGOT);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerEnchantedDiamond(JavaPlugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "enchanted_diamond");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.DIAMOND, 1));
        recipe.shape("AAA", "AAA", "AAA");
        recipe.setIngredient('A', Material.DIAMOND);
        plugin.getServer().addRecipe(recipe);
    }
}
