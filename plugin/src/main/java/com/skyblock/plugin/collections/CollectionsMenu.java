package com.skyblock.plugin.collections;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;


/**
 * Collections hub — shows five category icons; clicking one opens
 * {@link CollectionCategoryMenu} for that category.
 */
public class CollectionsMenu extends Menu {

    private enum Category {
        FARMING("Farming", Material.LIME_WOOL, 10,
                new Material[]{Material.WHEAT, Material.CARROT, Material.POTATO,
                        Material.PUMPKIN, Material.MELON_SLICE, Material.SUGAR_CANE,
                        Material.CACTUS, Material.RED_MUSHROOM, Material.NETHER_WART}),
        MINING("Mining", Material.LIGHT_GRAY_WOOL, 19,
                new Material[]{Material.COBBLESTONE, Material.COAL, Material.IRON_INGOT,
                        Material.GOLD_INGOT, Material.DIAMOND, Material.LAPIS_LAZULI,
                        Material.EMERALD, Material.REDSTONE, Material.OBSIDIAN}),
        COMBAT("Combat", Material.RED_WOOL, 28,
                new Material[]{Material.ROTTEN_FLESH, Material.BONE, Material.STRING,
                        Material.GUNPOWDER, Material.ENDER_PEARL}),
        FORAGING("Foraging", Material.GREEN_WOOL, 37,
                new Material[]{Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
                        Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG}),
        FISHING("Fishing", Material.BLUE_WOOL, 46,
                new Material[]{Material.COD, Material.SALMON, Material.PUFFERFISH,
                        Material.TROPICAL_FISH, Material.PRISMARINE_SHARD});

        final String displayName;
        final Material icon;
        final int slot;
        final Material[] items;

        Category(String displayName, Material icon, int slot, Material[] items) {
            this.displayName = displayName;
            this.icon = icon;
            this.slot = slot;
            this.items = items;
        }
    }

    private final UUID playerId;

    public CollectionsMenu(UUID playerId) {
        super("§2Collections", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        for (Category category : Category.values()) {
            setItem(category.slot,
                    new ItemBuilder(category.icon)
                            .displayName("§a" + category.displayName)
                            .lore("§7Click to view")
                            .build(),
                    event -> {
                        Player player = (Player) event.getWhoClicked();
                        new CollectionCategoryMenu(playerId, category.displayName, category.items).open(player);
                    });
        }
    }
}
