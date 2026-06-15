package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.Bukkit;

import java.util.Iterator;

/**
 * The Crafting Table menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §aCraft Item}, framed by a gray
 * glass-pane border, exposing a 3×3 input grid (slots 10–12, 19–21, 28–30),
 * an arrow indicator at slot 23, and a result display at slot 25.</p>
 */
public class CraftingTableMenu extends Menu {

    /** Slots for the 3×3 crafting input grid, row-major order. */
    private static final int[] GRID_SLOTS = {
            10, 11, 12,
            19, 20, 21,
            28, 29, 30
    };
    private static final int ARROW_SLOT  = 23;
    private static final int RESULT_SLOT = 25;

    private final Player player;
    private final ItemStack[] grid;

    public CraftingTableMenu(Player player) {
        super("§aCraft Item", 6);
        this.player = player;
        this.grid = new ItemStack[9];
    }

    @Override
    protected void build() {
        fillBorder();

        ItemStack emptyPane = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();

        for (int i = 0; i < GRID_SLOTS.length; i++) {
            ItemStack ingredient = grid[i] != null ? grid[i] : emptyPane;
            final int gridIndex = i;
            setItem(GRID_SLOTS[i], ingredient, event -> {
                ItemStack cursor = event.getCursor();
                if (cursor != null && cursor.getType() != Material.AIR) {
                    grid[gridIndex] = cursor.clone();
                } else {
                    grid[gridIndex] = null;
                }
                new CraftingTableMenu(player).open(player);
            });
        }

        setItem(ARROW_SLOT, new ItemBuilder(Material.ARROW)
                .displayName("§e➜")
                .build());

        ItemStack result = resolveResult();
        if (result != null) {
            setItem(RESULT_SLOT, new ItemBuilder(result.getType())
                    .displayName("§f" + formatName(result.getType()))
                    .lore(
                            "§7Amount: §f" + result.getAmount(),
                            "",
                            "§eClick to craft!")
                    .build(),
                    event -> {
                        player.getInventory().addItem(result.clone());
                        for (int i = 0; i < grid.length; i++) grid[i] = null;
                        new CraftingTableMenu(player).open(player);
                    });
        } else {
            setItem(RESULT_SLOT, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Result")
                    .lore("§7Fill the grid with a valid recipe.")
                    .build());
        }

        setItem(49, new ItemBuilder(Material.CRAFTING_TABLE)
                .displayName("§aCrafting Table")
                .lore("§7Place items in the grid to craft.")
                .build());
    }

    private ItemStack resolveResult() {
        boolean anyIngredient = false;
        for (ItemStack s : grid) {
            if (s != null && s.getType() != Material.AIR) {
                anyIngredient = true;
                break;
            }
        }
        if (!anyIngredient) return null;

        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            Recipe recipe = it.next();
            if (recipe instanceof ShapedRecipe shaped) {
                if (matchesGrid(shaped)) {
                    return recipe.getResult();
                }
            }
        }
        return null;
    }

    private boolean matchesGrid(ShapedRecipe recipe) {
        String[] shape = recipe.getShape();
        Material[] recipeGrid = new Material[9];
        int row = 0;
        for (String line : shape) {
            for (int col = 0; col < 3 && col < line.length(); col++) {
                char key = line.charAt(col);
                ItemStack ingredient = recipe.getIngredientMap().get(key);
                recipeGrid[row * 3 + col] = ingredient == null ? Material.AIR : ingredient.getType();
            }
            row++;
            if (row >= 3) break;
        }
        for (int i = 0; i < 9; i++) {
            Material required = recipeGrid[i] == null ? Material.AIR : recipeGrid[i];
            Material supplied = (grid[i] == null) ? Material.AIR : grid[i].getType();
            if (required != supplied) return false;
        }
        return true;
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }

    private static String formatName(Material material) {
        StringBuilder sb = new StringBuilder();
        for (String word : material.name().split("_")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase())
                  .append(' ');
            }
        }
        return sb.toString().trim();
    }
}
