package com.skyblock.plugin.menu;

import com.skyblock.core.crafting.SkyBlockRecipeManager;
import com.skyblock.core.crafting.SkyBlockRecipeManager.ShapedRecipe;
import com.skyblock.core.crafting.SkyBlockRecipeManager.ShapelessRecipe;
import com.skyblock.core.crafting.SkyBlockRecipeManager.SkyBlockRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public final class RecipeBookMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    public RecipeBookMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§aRecipe Book");
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
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r", List.of());
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        int slot = 10;
        for (SkyBlockRecipe recipe : SkyBlockRecipeManager.getInstance().getAllRecipes().values()) {
            while (slot < 45 && (slot % 9 == 0 || slot % 9 == 8)) {
                slot++;
            }
            if (slot >= 45) {
                break;
            }
            inventory.setItem(slot, makeRecipeItem(recipe));
            slot++;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof RecipeBookMenu) {
            event.setCancelled(true);
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
