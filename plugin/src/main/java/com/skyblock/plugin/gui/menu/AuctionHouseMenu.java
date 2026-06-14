package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.auction.AuctionManager;
import com.skyblock.plugin.auction.AuctionManager.AuctionEntry;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The Auction House menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Auction House}, framed by a gray
 * glass border, that lists every active BIN listing from {@link AuctionManager}
 * across the 28 inner slots.</p>
 */
public class AuctionHouseMenu extends Menu {

    /** Inner slots across the four centre rows, left-to-right, top-to-bottom. */
    private static final int[] INNER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final Player player;

    public AuctionHouseMenu(Player player) {
        super("§6Auction House", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        List<AuctionEntry> listings = AuctionManager.getInstance().getListings();

        for (int i = 0; i < listings.size() && i < INNER_SLOTS.length; i++) {
            AuctionEntry entry = listings.get(i);
            setItem(INNER_SLOTS[i], new ItemBuilder(Material.PAPER)
                    .displayName("§e" + entry.itemName())
                    .lore(
                            "§7Buy It Now: §6" + (long) entry.price() + " coins",
                            "",
                            "§eClick to purchase!")
                    .build());
        }

        if (listings.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Auctions Available")
                    .lore("§7There are no active listings right now.")
                    .build());
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
