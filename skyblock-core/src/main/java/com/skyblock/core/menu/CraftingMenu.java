package com.skyblock.core.menu;

import com.skyblock.core.manager.CraftingManager;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class CraftingMenu extends Menu {

    public CraftingMenu() {
        super("§aCrafting Table", 6);
    }

    @Override
    protected void build() {
        List<ItemStack> recipes = new ArrayList<>(CraftingManager.getInstance().getRecipes().values());
        for (int i = 0; i < recipes.size() && i < 54; i++) {
            setItem(i, recipes.get(i));
        }
    }
}
