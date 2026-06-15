package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Recipe Book menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Recipe Book}, framed by a gray
 * glass-pane border, that lists every registered {@link ShapedRecipe} across
 * paginated pages of 28 inner slots. Previous/next navigation arrows appear at
 * slots 45 and 53 when additional pages exist.</p>
 */
public class RecipeBookMenu extends Menu {

    /** Inner slots across the four centre rows, left-to-right, top-to-bottom. */
    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    private static final int SLOTS_PER_PAGE = INNER_SLOTS.length;

    private final Player player;
    private final List<ShapedRecipe> recipes;
    private final int page;

    public RecipeBookMenu(Player player) {
        this(player, 0);
    }

    private RecipeBookMenu(Player player, int page) {
        super("§aRecipe Book", 6);
        this.player = player;
        this.page = page;
        this.recipes = loadRecipes();
    }

    private static List<ShapedRecipe> loadRecipes() {
        List<ShapedRecipe> list = new ArrayList<>();
        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            Recipe r = it.next();
            if (r instanceof ShapedRecipe sr) {
                list.add(sr);
            }
        }
        return list;
    }

    @Override
    protected void build() {
        fillBorder();

        int totalPages = Math.max(1, (int) Math.ceil((double) recipes.size() / SLOTS_PER_PAGE));
        int start = page * SLOTS_PER_PAGE;

        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            int recipeIndex = start + i;
            if (recipeIndex >= recipes.size()) break;
            ShapedRecipe recipe = recipes.get(recipeIndex);
            ItemStack result = recipe.getResult();
            setItem(INNER_SLOTS[i], new ItemBuilder(result.getType())
                    .displayName("§f" + formatName(result.getType()))
                    .lore(
                            "§7Output: §f" + result.getAmount() + "x " + formatName(result.getType()),
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
                .displayName("§aRecipe Book")
                .lore("§7Page §e" + (page + 1) + "§7/§e" + totalPages)
                .build());

        if (page > 0) {
            int prevPage = page - 1;
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Go to page §e" + (prevPage + 1))
                    .build(),
                    event -> new RecipeBookMenu(player, prevPage).open(player));
        }

        if ((page + 1) < totalPages) {
            int nextPage = page + 1;
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Go to page §e" + (nextPage + 1))
                    .build(),
                    event -> new RecipeBookMenu(player, nextPage).open(player));
        }
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
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
