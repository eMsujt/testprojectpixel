package com.skyblock.plugin.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.AuctionManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The Auction House menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Auction House}. Slots 0-44 display
 * the active auction listings one icon each, paged in document order; the bottom
 * row (45-53) is a {@code GRAY_STAINED_GLASS_PANE} footer. Clicking a listing
 * tells the player its current price.</p>
 */
public class AuctionHouseMenu extends Menu {

    /** Number of listing slots available across the top five rows. */
    private static final int LISTING_SLOTS = 45;

    public AuctionHouseMenu() {
        super("§6Auction House", 6);
    }

    @Override
    protected void build() {
        fillFooter();

        List<AuctionManager.Auction> auctions = AuctionManager.getInstance().getAuctions();
        for (int i = 0; i < auctions.size() && i < LISTING_SLOTS; i++) {
            AuctionManager.Auction auction = auctions.get(i);
            setItem(i, new ItemBuilder(Material.PAPER)
                            .displayName("§a" + auction.itemName())
                            .lore(
                                    "§7Price: §6" + auction.price() + " coins",
                                    "§7Click to view")
                            .build(),
                    event -> event.getWhoClicked().sendMessage(
                            "§a" + auction.itemName() + " §7is going for §6"
                                    + auction.price() + " coins§7."));
        }
    }

    /** Fills the menu's bottom row with gray glass panes, matching Hypixel. */
    private void fillFooter() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }
    }
}
