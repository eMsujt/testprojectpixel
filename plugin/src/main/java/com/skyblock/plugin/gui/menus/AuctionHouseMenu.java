package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.AuctionManager;
import org.bukkit.Material;

import java.util.List;

/**
 * The Auction House browse menu.
 *
 * <p>A 54-slot (6-row) menu listing every active auction (from
 * {@link AuctionManager}), one {@code PAPER} icon per listing showing the
 * item name and current price.</p>
 */
public class AuctionHouseMenu extends Menu {

    /** Centred slots across four rows, one per listing. */
    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43};

    public AuctionHouseMenu() {
        super("Auction House", 6);
    }

    @Override
    protected void build() {
        List<AuctionManager.Auction> auctions = AuctionManager.getInstance().getAuctions();
        for (int i = 0; i < auctions.size() && i < SLOTS.length; i++) {
            AuctionManager.Auction auction = auctions.get(i);
            setItem(SLOTS[i], new ItemBuilder(Material.PAPER)
                    .displayName("§a" + auction.itemName())
                    .lore(
                            "§7Price: §6" + auction.price() + " coins",
                            "§7Click to view")
                    .build());
        }
    }
}
