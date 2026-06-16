package com.skyblock.gui.menu;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import com.skyblock.gui.util.SkyBlockRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RecipeBookMenu extends Menu {

    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    private static final int SLOTS_PER_PAGE = INNER_SLOTS.length;

    private final Player player;
    private final List<SkyBlockRecipe> recipes;
    private final int page;

    public RecipeBookMenu(Player player, List<SkyBlockRecipe> recipes) {
        this(player, recipes, 0);
    }

    private RecipeBookMenu(Player player, List<SkyBlockRecipe> recipes, int page) {
        super("§6Recipe Book", 6);
        this.player = player;
        this.recipes = recipes;
        this.page = page;
    }

    @Override
    protected void build() {
        fillBorder();

        setItem(4, new ItemBuilder(Material.BOOK)
                .displayName("§6Recipe Book")
                .lore("§7Browse all available crafting recipes.")
                .build());

        int totalPages = Math.max(1, (int) Math.ceil((double) recipes.size() / SLOTS_PER_PAGE));
        int start = page * SLOTS_PER_PAGE;

        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            int recipeIndex = start + i;
            if (recipeIndex >= recipes.size()) break;
            SkyBlockRecipe recipe = recipes.get(recipeIndex);
            setItem(INNER_SLOTS[i], new ItemBuilder(recipe.result())
                    .displayName("§e" + recipe.id())
                    .lore(
                            "§7Produces: §f" + recipe.resultAmount() + "x " + formatName(recipe.result()),
                            "",
                            "§eClick to view!")
                    .build());
        }

        if (recipes.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Recipes Available")
                    .lore("§7No recipes have been registered.")
                    .build());
        }

        setItem(49, new ItemBuilder(Material.BOOK)
                .displayName("§6Recipe Book")
                .lore("§7Page §e" + (page + 1) + "§7/§e" + totalPages)
                .build());

        if (page > 0) {
            int prevPage = page - 1;
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Go to page §e" + (prevPage + 1))
                    .build(),
                    event -> new RecipeBookMenu(player, recipes, prevPage).open(player));
        }

        if ((page + 1) < totalPages) {
            int nextPage = page + 1;
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Go to page §e" + (nextPage + 1))
                    .build(),
                    event -> new RecipeBookMenu(player, recipes, nextPage).open(player));
        }
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
