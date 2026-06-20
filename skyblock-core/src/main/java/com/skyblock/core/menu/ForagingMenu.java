package com.skyblock.core.menu;

import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.foraging.ForagingManager.ForagingZone;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class ForagingMenu extends Menu {

    private static final String TITLE = "§2Foraging";
    private static final int CLOSE_SLOT = 53;

    /** Inventory slots for each ForagingZone in declaration order. */
    private static final int[] ZONE_SLOTS = {10, 12, 14, 16, 22};

    private final UUID playerId;

    public ForagingMenu(UUID playerId) {
        super(TITLE, 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        ForagingManager mgr = ForagingManager.getInstance();
        ForagingZone[] zones = ForagingZone.values();

        for (int i = 0; i < zones.length && i < ZONE_SLOTS.length; i++) {
            ForagingZone zone = zones[i];
            Material mat = zone.getPrimaryTree().getMaterial();
            int chops = mgr.getChops(playerId, zone.getPrimaryTree());

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
                    ((Player) e.getWhoClicked()).closeInventory();
                });
    }
}
