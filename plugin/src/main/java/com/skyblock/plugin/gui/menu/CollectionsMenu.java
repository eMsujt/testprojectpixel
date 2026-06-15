package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.collections.CollectionCategoryMenu;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The Collections hub menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §aCollections} with a gray glass-pane
 * border. Five category icons form a horizontal selector row (slots 20–24);
 * clicking one opens {@link CollectionCategoryMenu} for that category.</p>
 */
public class CollectionsMenu extends Menu {

    private enum Category {
        FARMING("Farming", "§a", Material.WHEAT, 11,
                new Material[]{Material.WHEAT, Material.CARROT, Material.POTATO,
                        Material.PUMPKIN, Material.MELON_SLICE, Material.SUGAR_CANE,
                        Material.CACTUS, Material.RED_MUSHROOM, Material.NETHER_WART}),
        MINING("Mining", "§7", Material.COBBLESTONE, 12,
                new Material[]{Material.COBBLESTONE, Material.COAL, Material.IRON_INGOT,
                        Material.GOLD_INGOT, Material.DIAMOND, Material.LAPIS_LAZULI,
                        Material.EMERALD, Material.REDSTONE, Material.OBSIDIAN}),
        COMBAT("Combat", "§c", Material.ROTTEN_FLESH, 13,
                new Material[]{Material.ROTTEN_FLESH, Material.BONE, Material.STRING,
                        Material.GUNPOWDER, Material.ENDER_PEARL}),
        FORAGING("Foraging", "§2", Material.OAK_LOG, 14,
                new Material[]{Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
                        Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG}),
        FISHING("Fishing", "§9", Material.COD, 15,
                new Material[]{Material.COD, Material.SALMON, Material.PUFFERFISH,
                        Material.TROPICAL_FISH, Material.PRISMARINE_SHARD});

        private final String displayName;
        private final String color;
        private final Material icon;
        private final int slot;
        private final Material[] items;

        Category(String displayName, String color, Material icon, int slot, Material[] items) {
            this.displayName = displayName;
            this.color = color;
            this.icon = icon;
            this.slot = slot;
            this.items = items;
        }
    }

    private final UUID playerId;

    public CollectionsMenu(UUID playerId) {
        super("§eCollections", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        for (Category category : Category.values()) {
            final Category cat = category;
            setItem(category.slot, new ItemBuilder(category.icon)
                            .displayName(category.color + category.displayName + " Collections")
                            .lore(
                                    "§7View your " + category.displayName.toLowerCase() + " collections.",
                                    "§eClick to view!")
                            .build(),
                    event -> {
                        event.setCancelled(true);
                        new CollectionCategoryMenu(playerId, cat.displayName, cat.items)
                                .open((Player) event.getWhoClicked());
                    });
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
    }
}
