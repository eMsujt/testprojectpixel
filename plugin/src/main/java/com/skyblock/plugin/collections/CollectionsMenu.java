package com.skyblock.plugin.collections;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Collections hub menu.
 *
 * <p>Row 0 (slots 0–8) holds the five category tab icons centred at slots 2–6,
 * with gray glass panes filling the remaining header slots. Rows 1–5 list the
 * player's per-material counts for the currently active category, reading live
 * data from {@link CollectionManager}.</p>
 */
public class CollectionsMenu extends Menu {

    private enum Category {
        FARMING("Farming", Material.WHEAT,
                new Material[]{Material.WHEAT, Material.CARROT, Material.POTATO,
                        Material.PUMPKIN, Material.MELON_SLICE, Material.SUGAR_CANE,
                        Material.CACTUS, Material.RED_MUSHROOM, Material.NETHER_WART}),
        MINING("Mining", Material.COBBLESTONE,
                new Material[]{Material.COBBLESTONE, Material.COAL, Material.IRON_INGOT,
                        Material.GOLD_INGOT, Material.DIAMOND, Material.LAPIS_LAZULI,
                        Material.EMERALD, Material.REDSTONE, Material.OBSIDIAN}),
        COMBAT("Combat", Material.ROTTEN_FLESH,
                new Material[]{Material.ROTTEN_FLESH, Material.BONE, Material.STRING,
                        Material.GUNPOWDER, Material.ENDER_PEARL}),
        FORAGING("Foraging", Material.OAK_LOG,
                new Material[]{Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
                        Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG}),
        FISHING("Fishing", Material.COD,
                new Material[]{Material.COD, Material.SALMON, Material.PUFFERFISH,
                        Material.TROPICAL_FISH, Material.PRISMARINE_SHARD});

        final String displayName;
        final Material icon;
        final Material[] items;

        Category(String displayName, Material icon, Material[] items) {
            this.displayName = displayName;
            this.icon = icon;
            this.items = items;
        }
    }

    /** Header row slots for the five category tabs (centred in row 0). */
    private static final int[] TAB_SLOTS = {2, 3, 4, 5, 6};

    private final UUID playerId;
    private final Category active;

    public CollectionsMenu(UUID playerId) {
        this(playerId, Category.FARMING);
    }

    public CollectionsMenu(UUID playerId, Category active) {
        super("§aCollections", 6);
        this.playerId = playerId;
        this.active = active;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        // Row 0: glass pane header, category tabs at slots 2-6
        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }

        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            boolean isActive = category == active;
            int tabSlot = TAB_SLOTS[i];
            setItem(tabSlot,
                    new ItemBuilder(category.icon)
                            .displayName((isActive ? "§e" : "§7") + category.displayName)
                            .lore(isActive ? "§aCurrently viewing" : "§7Click to view")
                            .build(),
                    event -> {
                        Player player = (Player) event.getWhoClicked();
                        new CollectionsMenu(playerId, category).open(player);
                    });
        }

        // Rows 1-5: collection items for the active category
        CollectionManager manager = CollectionManager.getInstance();
        for (int i = 0; i < active.items.length && i < 45; i++) {
            Material mat = active.items[i];
            long count = manager.getCollection(playerId, mat);
            int tier = manager.getTier(playerId, mat);
            String name = prettify(mat.name());
            setItem(9 + i, new ItemBuilder(mat)
                    .displayName("§a" + name)
                    .lore("§7Collected: §e" + count, "§7Tier: §e" + tier)
                    .build());
        }
    }

    private static String prettify(String name) {
        String[] words = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
