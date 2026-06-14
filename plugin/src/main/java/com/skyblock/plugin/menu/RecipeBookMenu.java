package com.skyblock.plugin.menu;

import com.skyblock.core.crafting.SkyBlockRecipeManager;
import com.skyblock.core.crafting.SkyBlockRecipeManager.ShapedRecipe;
import com.skyblock.core.crafting.SkyBlockRecipeManager.ShapelessRecipe;
import com.skyblock.core.crafting.SkyBlockRecipeManager.SkyBlockRecipe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public final class RecipeBookMenu implements InventoryHolder {

    private final Inventory inventory;

    public RecipeBookMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§6Recipe Book");
        build();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build() {
        int slot = 0;
        for (SkyBlockRecipe recipe : SkyBlockRecipeManager.getInstance().getAllRecipes().values()) {
            if (slot >= 54) {
                break;
            }
            inventory.setItem(slot, makeRecipeItem(recipe));
            slot++;
        }
    }

    private ItemStack makeRecipeItem(SkyBlockRecipe recipe) {
        List<String> lore = Arrays.asList(
                "§7Type: §e" + kind(recipe),
                "§7Produces: §e" + recipe.resultAmount() + "x " + recipe.result()
        );
        return makeItem(recipe.result(), "§e" + recipe.id(), lore);
    }

    private String kind(SkyBlockRecipe recipe) {
        if (recipe instanceof ShapedRecipe) {
            return "Shaped";
        }
        if (recipe instanceof ShapelessRecipe) {
            return "Shapeless";
        }
        return "Recipe";
    }

    private ItemStack makeItem(org.bukkit.Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
