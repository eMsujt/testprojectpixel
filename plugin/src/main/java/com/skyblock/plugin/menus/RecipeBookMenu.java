package com.skyblock.plugin.menus;

import com.skyblock.core.crafting.SkyBlockRecipeManager;
import com.skyblock.core.crafting.SkyBlockRecipeManager.ShapedRecipe;
import com.skyblock.core.crafting.SkyBlockRecipeManager.ShapelessRecipe;
import com.skyblock.core.crafting.SkyBlockRecipeManager.SkyBlockRecipe;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Recipe Book menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §aRecipe Book}. Every recipe
 * registered with the {@link SkyBlockRecipeManager} is rendered as one icon —
 * the recipe's result material — across the inner slots, framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border, with a close button on the bottom
 * row. Each icon's lore shows the recipe id, its kind (shaped or shapeless)
 * and the number of items produced.</p>
 */
public class RecipeBookMenu extends Menu {

    /** Inner slots (rows 1–4, columns 1–7) available for recipe icons. */
    private static final int[] RECIPE_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 53;

    public RecipeBookMenu(Player player) {
        super("§aRecipe Book", 6);
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }

        int index = 0;
        for (SkyBlockRecipe recipe : SkyBlockRecipeManager.getInstance().getAllRecipes().values()) {
            if (index >= RECIPE_SLOTS.length) {
                break;
            }
            setItem(RECIPE_SLOTS[index], new ItemBuilder(recipe.result())
                    .displayName("§e" + recipe.id())
                    .lore("§7Type: §f" + kind(recipe),
                            "§7Produces: §f" + recipe.resultAmount() + "x " + recipe.result())
                    .build());
            index++;
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }

    /** Returns a human-readable label for the recipe's kind. */
    private static String kind(SkyBlockRecipe recipe) {
        if (recipe instanceof ShapedRecipe) {
            return "Shaped";
        }
        if (recipe instanceof ShapelessRecipe) {
            return "Shapeless";
        }
        return "Recipe";
    }
}
