package com.skyblock.core.menu;

import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.foraging.ForagingManager.ForagingZone;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 6-row chest GUI titled '§2Foraging' showing each {@link ForagingZone} as its
 * primary-tree log icon with the player's chop count for that zone.
 *
 * <p>Layout (rows 0-5, 9 columns):
 * <pre>
 *  0: [pane × 9]
 *  1: [pane][DARK_THICKET][pane][BIRCH_PARK][pane][SPRUCE_WOODS][pane][SAVANNA][pane]
 *  2: [pane × 4][JUNGLE_ISLAND][pane × 4]
 *  3: [pane × 9]
 *  4: [pane × 9]
 *  5: [pane × 8][CLOSE]
 * </pre>
 */
public final class ForagingMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§2Foraging";
    private static final int CLOSE_SLOT = 53;

    /** Inventory slots for each ForagingZone in declaration order. */
    private static final int[] ZONE_SLOTS = {10, 12, 14, 16, 22};

    public ForagingMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        ForagingManager mgr = ForagingManager.getInstance();
        ForagingZone[] zones = ForagingZone.values();

        for (int i = 0; i < zones.length && i < ZONE_SLOTS.length; i++) {
            ForagingZone zone = zones[i];
            Material mat = zone.getPrimaryTree().getMaterial();
            int chops = mgr.getChops(player.getUniqueId(), zone.getPrimaryTree());

            setItem(ZONE_SLOTS[i], new ItemBuilder(mat)
                    .displayName("§2" + zone.getDisplayName())
                    .lore(
                            "§7Chops: §a" + chops,
                            "§7Click to view zone details.")
                    .build(),
                    e -> e.setCancelled(true));
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Click to close.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                });
    }
}
