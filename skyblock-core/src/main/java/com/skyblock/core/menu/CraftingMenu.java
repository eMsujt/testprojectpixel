package com.skyblock.core.menu;

import com.skyblock.core.crafting.manager.CraftingManager;
import com.skyblock.core.crafting.manager.CraftingManager.SkyBlockRecipe;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * SkyBlock recipe browser opened by {@code /craft}. A 54-slot (6-row) chest GUI
 * that lists every known {@link SkyBlockRecipe} result from {@link CraftingManager}
 * in the framed inner grid, paged so no recipe is hidden.
 */
public final class CraftingMenu extends AbstractMenu {

    /** 4 inner rows (rows 2–5) of 7 = 28 recipes per page; the bottom row holds paging. */
    private static final int PAGE_SIZE = 28;

    private final int page;

    public CraftingMenu(JavaPlugin plugin, Player player) {
        this(plugin, player, 0);
    }

    public CraftingMenu(JavaPlugin plugin, Player player, int page) {
        super(plugin, player, "§eCrafting Table", 54);
        this.page = Math.max(0, page);
    }

    @Override
    protected void populate() {
        List<ItemStack> recipes = new ArrayList<>();
        for (SkyBlockRecipe recipe : CraftingManager.getInstance().getAllRecipes().values()) {
            recipes.add(new ItemStack(recipe.result(), recipe.resultAmount()));
        }

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, recipes.size());
        for (int i = start; i < end; i++) {
            setItem(contentSlot(i - start), recipes.get(i));
        }

        if (recipes.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Recipes")
                    .lore("§7There are no recipes to show.")
                    .build());
        }

        if (page > 0) {
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Page " + page)
                    .build(),
                    e -> { e.setCancelled(true); new CraftingMenu(plugin, player, page - 1).open(player); });
        }
        if (end < recipes.size()) {
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Page " + (page + 2))
                    .build(),
                    e -> { e.setCancelled(true); new CraftingMenu(plugin, player, page + 1).open(player); });
        }
    }
}
