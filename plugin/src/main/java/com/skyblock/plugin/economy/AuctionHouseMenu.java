package com.skyblock.plugin.economy;

import com.skyblock.plugin.economy.AuctionHouseManager.AuctionListing;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The Auction House browse menu.
 *
 * <p>A 54-slot (6-row) menu showing every active listing from
 * {@link AuctionHouseManager} in posting order, one icon per listing in the
 * inner slots, with a gray glass-pane border matching Hypixel.</p>
 */
public class AuctionHouseMenu extends Menu {

    public AuctionHouseMenu() {
        super("§6Auction House", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        int slot = 10;
        for (AuctionListing listing : AuctionHouseManager.getInstance().getListings()) {
            if (slot >= 44) {
                break;
            }
            int column = slot % 9;
            if (column == 0 || column == 8) {
                slot += column == 8 ? 2 : 1;
                continue;
            }
            setItem(slot, new ItemBuilder(Material.PAPER)
                    .displayName("§a" + listing.itemName())
                    .lore("§7Price: §6" + listing.price() + " coins")
                    .build());
            slot++;
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
}
