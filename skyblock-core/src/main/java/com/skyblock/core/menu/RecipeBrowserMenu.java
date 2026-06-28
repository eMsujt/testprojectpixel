package com.skyblock.core.menu;

import com.skyblock.core.crafting.manager.CraftingManager;
import com.skyblock.core.util.ItemData;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The Recipe Book: a browser of every craftable SkyBlock item. Clicking one
 * opens its {@link RecipeMenu}. Replaces the old behaviour of opening the admin
 * item-spawner ({@link ItemsMenu}).
 */
public final class RecipeBrowserMenu extends AbstractSkyBlockMenu {

    private final int page;

    public RecipeBrowserMenu(Player player) {
        this(player, 0);
    }

    public RecipeBrowserMenu(Player player, int page) {
        super(player, "Recipe Book", 6);
        this.page = Math.max(0, page);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }

        List<String> ids = new ArrayList<>(CraftingManager.getInstance().getAllRecipes().keySet());
        ids.sort(String::compareTo);

        int perPage = contentCapacity(); // inner 7-wide grid (28 in a 6-row)
        int from = page * perPage;
        for (int i = 0; from + i < ids.size() && i < perPage; i++) {
            String id = ids.get(from + i);
            setItem(contentSlot(i), new ItemBuilder(iconFor(id))
                            .displayName("§a" + displayName(id))
                            .lore("§7Recipe for §f" + displayName(id) + "§7.", "", "§eClick to view!")
                            .build(),
                    e -> {
                        e.setCancelled(true);
                        new RecipeMenu(id).open(player);
                    });
        }

        // Page navigation.
        int pages = Math.max(1, (ids.size() + perPage - 1) / perPage);
        if (page > 0) {
            setItem(45, new ItemBuilder(Material.ARROW).displayName("§aPrevious Page")
                    .lore("§7Page " + page + "/" + pages).build(),
                    e -> { e.setCancelled(true); new RecipeBrowserMenu(player, page - 1).open(player); });
        }
        if (from + perPage < ids.size()) {
            setItem(53, new ItemBuilder(Material.ARROW).displayName("§aNext Page")
                    .lore("§7Page " + (page + 2) + "/" + pages).build(),
                    e -> { e.setCancelled(true); new RecipeBrowserMenu(player, page + 1).open(player); });
        }

        setItem(49, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
    }

    private static String displayName(String id) {
        String name = ItemData.name(id);
        return name != null ? name : prettify(id);
    }

    private static Material iconFor(String id) {
        try {
            return Material.valueOf(id.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return Material.CRAFTING_TABLE;
        }
    }

    private static String prettify(String id) {
        StringBuilder sb = new StringBuilder();
        for (String part : id.replace('_', ' ').toLowerCase(Locale.ROOT).split(" ")) {
            if (part.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }
}
