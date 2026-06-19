package com.skyblock.core.manager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CraftingManager {

    private static final CraftingManager INSTANCE = new CraftingManager();

    private final Map<String, ItemStack> recipes = new HashMap<>();

    private CraftingManager() {
        recipes.put("ENCHANTED_BREAD", new ItemStack(Material.BREAD));
        recipes.put("ENCHANTED_COAL", new ItemStack(Material.COAL));
        recipes.put("ENCHANTED_IRON", new ItemStack(Material.IRON_INGOT));
        recipes.put("ENCHANTED_GOLD", new ItemStack(Material.GOLD_INGOT));
        recipes.put("ENCHANTED_DIAMOND", new ItemStack(Material.DIAMOND));
        recipes.put("ENCHANTED_EMERALD", new ItemStack(Material.EMERALD));
        recipes.put("ENCHANTED_REDSTONE", new ItemStack(Material.REDSTONE));
        recipes.put("ENCHANTED_LAPIS", new ItemStack(Material.LAPIS_LAZULI));
        recipes.put("ENCHANTED_QUARTZ", new ItemStack(Material.QUARTZ));
        recipes.put("ENCHANTED_OBSIDIAN", new ItemStack(Material.OBSIDIAN));
    }

    public static CraftingManager getInstance() {
        return INSTANCE;
    }

    public ItemStack getRecipe(String key) {
        return recipes.get(key);
    }

    public Map<String, ItemStack> getRecipes() {
        return Collections.unmodifiableMap(recipes);
    }

    public void registerRecipe(String key, ItemStack output) {
        recipes.put(key, output);
    }

    public boolean hasRecipe(String key) {
        return recipes.containsKey(key);
    }
}
