package com.skyblock.core.menu;

import com.skyblock.core.crafting.manager.CraftingManager;
import com.skyblock.core.crafting.manager.CraftingManager.SkyBlockRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * SkyBlock recipe browser opened by {@code /craft}. A 54-slot (6-row) chest GUI
 * that lists every known {@link SkyBlockRecipe} result from {@link CraftingManager}.
 */
public final class CraftingMenu extends AbstractMenu {

    public CraftingMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§eCrafting Table", 54);
    }

    @Override
    protected void populate() {
        List<ItemStack> recipes = new ArrayList<>();
        for (SkyBlockRecipe recipe : CraftingManager.getInstance().getAllRecipes().values()) {
            recipes.add(new ItemStack(recipe.result(), recipe.resultAmount()));
        }
        for (int i = 0; i < recipes.size() && i < 54; i++) {
            setItem(i, recipes.get(i));
        }
    }
}
