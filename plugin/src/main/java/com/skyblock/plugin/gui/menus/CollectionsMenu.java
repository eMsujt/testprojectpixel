package com.skyblock.plugin.gui.menus;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The Collections hub menu.
 *
 * <p>A 54-slot (6-row) menu presenting one icon per collection category. Clicking
 * a category icon opens the corresponding {@link CollectionCategoryMenu}, listing
 * the collections in that category alongside the player's current counts.</p>
 */
public class CollectionsMenu extends Menu {

    /** A collection category: its display name, icon and the collections it contains. */
    private enum Category {
        FARMING("Farming", Material.WHEAT, List.of(
                "wheat", "carrot", "potato", "pumpkin", "melon", "sugar_cane",
                "cocoa_beans", "cactus", "brown_mushroom", "red_mushroom", "nether_wart")),
        MINING("Mining", Material.COBBLESTONE, List.of(
                "cobblestone", "coal", "iron_ingot", "gold_ingot", "diamond",
                "lapis_lazuli", "emerald", "redstone", "quartz", "obsidian")),
        COMBAT("Combat", Material.IRON_SWORD, List.of(
                "rotten_flesh", "bone", "string", "gunpowder", "ender_pearl")),
        FORAGING("Foraging", Material.OAK_LOG, List.of(
                "oak_wood", "spruce_wood", "birch_wood", "jungle_wood", "acacia_wood", "dark_oak_wood")),
        FISHING("Fishing", Material.COD, List.of(
                "cod", "salmon", "pufferfish", "tropical_fish", "prismarine_shard"));

        private final String displayName;
        private final Material icon;
        private final List<String> collections;

        Category(String displayName, Material icon, List<String> collections) {
            this.displayName = displayName;
            this.icon = icon;
            this.collections = collections;
        }
    }

    /** Centred slots in the middle row, one per category. */
    private static final int[] SLOTS = {20, 21, 22, 23, 24};

    /** All border slots (top row, bottom row, left/right edges of middle rows). */
    private static final int[] BORDER_SLOTS = {
        0,  1,  2,  3,  4,  5,  6,  7,  8,
        9,                                 17,
        18,                                26,
        27,                                35,
        36,                                44,
        45, 46, 47, 48, 49, 50, 51, 52, 53
    };

    public CollectionsMenu() {
        super("§eCollections", 6);
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot : BORDER_SLOTS) {
            setItem(slot, pane);
        }

        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            setItem(SLOTS[i], new ItemBuilder(category.icon)
                            .displayName("§a" + category.displayName)
                            .lore("§7Click to view your " + category.displayName.toLowerCase() + " collections.")
                            .build(),
                    event -> {
                        Player player = (Player) event.getWhoClicked();
                        new CollectionCategoryMenu(category.displayName, category.collections, player.getUniqueId())
                                .open(player);
                    });
        }
    }
}
