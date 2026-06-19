package com.skyblock.core.menu;

import com.skyblock.core.crafting.manager.CraftingManager;
import com.skyblock.core.crafting.manager.CraftingManager.SkyBlockRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class CraftingMenu extends Menu {

    public CraftingMenu() {
        super("§aCrafting Table", 6);
    }

    @Override
    protected void build() {
        List<ItemStack> recipes = new ArrayList<>();
        for (SkyBlockRecipe recipe : CraftingManager.getInstance().getAllRecipes().values()) {
            recipes.add(new ItemStack(recipe.result(), recipe.resultAmount()));
        }
        for (int i = 0; i < recipes.size() && i < 54; i++) {
            setItem(i, recipes.get(i));
        }
    }
}
