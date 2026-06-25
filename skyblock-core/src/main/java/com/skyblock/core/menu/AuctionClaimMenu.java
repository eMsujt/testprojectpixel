package com.skyblock.core.menu;

import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.manager.AuctionHouseManager.AuctionListing;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * "Your Auctions & Claims" — where a player collects auction proceeds (coins) and
 * won/returned items, and cancels their own active listings. Reached from a button
 * in the {@link AuctionHouseMenu}. Closes the loop for live bidding: outbid refunds
 * and won items land in the claim queues and are collected here.
 */
public final class AuctionClaimMenu extends AbstractSkyBlockMenu {

    private static final int[] PENDING_ITEM_SLOTS = {10, 11, 12, 13, 14, 15, 16};
    private static final int[] LISTING_SLOTS = {28, 29, 30, 31, 32, 33, 34};

    public AuctionClaimMenu(Player player) {
        super(player, "§6Your Auctions & Claims", 6);
    }

    @Override
    protected void populate() {
        AuctionHouseManager manager = AuctionHouseManager.getInstance();
        UUID id = player.getUniqueId();

        double coins = manager.getPendingCoins(id);
        List<ItemStack> pendingItems = manager.getPendingItems(id);

        setItem(4, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Claims")
                .lore(
                        "§7Coins to collect: §6" + (long) coins,
                        "§7Items to collect: §e" + pendingItems.size(),
                        "",
                        "§7Sale proceeds and won/returned items",
                        "§7wait here until you collect them.")
                .build(), e -> e.setCancelled(true));

        // Claim coins.
        if (coins > 0) {
            setItem(20, new ItemBuilder(Material.GOLD_NUGGET)
                    .displayName("§aClaim §6" + (long) coins + " coins")
                    .lore("§7Click to collect your sale proceeds.")
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        double claimed = manager.claimCoins(id);
                        if (claimed > 0) {
                            EconomyManager.getInstance().addCoins(id, claimed);
                            player.sendMessage("§aCollected §6" + (long) claimed + " coins§a.");
                        }
                        open(player);
                    });
        } else {
            setItem(20, new ItemBuilder(Material.GRAY_DYE)
                    .displayName("§7No coins to claim").build(), e -> e.setCancelled(true));
        }

        // Pending items preview + claim-all.
        for (int i = 0; i < PENDING_ITEM_SLOTS.length && i < pendingItems.size(); i++) {
            setItem(PENDING_ITEM_SLOTS[i], pendingItems.get(i).clone(), e -> e.setCancelled(true));
        }
        if (!pendingItems.isEmpty()) {
            setItem(24, new ItemBuilder(Material.CHEST)
                    .displayName("§aClaim all items §7(" + pendingItems.size() + ")")
                    .lore("§7Collect every item waiting for you.")
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        int delivered = 0;
                        for (ItemStack item : manager.claimItems(id)) {
                            delivered++;
                            for (ItemStack leftover : player.getInventory().addItem(item).values()) {
                                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                            }
                        }
                        player.sendMessage("§aCollected §e" + delivered + " §aitem(s).");
                        open(player);
                    });
        } else {
            setItem(24, new ItemBuilder(Material.GRAY_DYE)
                    .displayName("§7No items to claim").build(), e -> e.setCancelled(true));
        }

        // Your active listings (click to cancel; item returns to your claims).
        List<AuctionListing> mine = manager.getActiveListings().stream()
                .map(manager::getListing)
                .filter(l -> l != null && id.equals(l.seller()))
                .toList();
        setItem(22, new ItemBuilder(Material.PAPER)
                .displayName("§eYour Active Listings §7(" + mine.size() + ")")
                .lore("§7Click a listing below to cancel it.")
                .build(), e -> e.setCancelled(true));
        for (int i = 0; i < LISTING_SLOTS.length && i < mine.size(); i++) {
            AuctionListing listing = mine.get(i);
            UUID listingId = listing.id();
            setItem(LISTING_SLOTS[i], new ItemBuilder(listing.item())
                    .displayName("§e" + listing.itemName())
                    .lore(
                            "§7" + (listing.binListing() ? "BIN: " : "Bid: ") + "§6" + (long) listing.startingBid() + " coins",
                            "",
                            "§cClick to cancel this listing.")
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        try {
                            manager.cancelListing(listingId, id);
                            player.sendMessage("§aListing cancelled — the item is in your claims.");
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage("§cCould not cancel: " + ex.getMessage());
                        }
                        open(player);
                    });
        }

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To the Auction House")
                .build(),
                e -> { e.setCancelled(true); new AuctionHouseMenu(player).open(player); });
    }
}
