package com.skyblock.core.menu;

import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.manager.AuctionHouseManager.AuctionListing;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Buy-it-now confirmation shown when a player left-clicks a listing in the
 * {@link AuctionHouseMenu}. Uses the live {@link AuctionHouseManager} (the same
 * manager the auction house menu lists from) and completes a real purchase:
 * the buyer's coins are debited, the item is delivered to their inventory, and
 * the seller is paid. Auction (non-BIN) listings are shown read-only for now —
 * bidding needs a claim menu for outbid refunds (tracked).
 */
public final class AuctionConfirmMenu extends AbstractSkyBlockMenu {

    static final int ITEM_SLOT = 13;
    static final int CONFIRM_SLOT = 11;
    static final int CANCEL_SLOT = 15;

    private final UUID listingId;

    public AuctionConfirmMenu(Player player, UUID listingId) {
        super(player, "§6§lConfirm Purchase", 3);
        this.listingId = listingId;
    }

    @Override
    protected void populate() {
        AuctionHouseManager manager = AuctionHouseManager.getInstance();
        if (!manager.isActive(listingId)) {
            new AuctionHouseMenu(player).open(player);
            return;
        }
        AuctionListing listing = manager.getListing(listingId);
        boolean bin = listing.binListing();
        double price = listing.startingBid();

        setItem(ITEM_SLOT, new ItemBuilder(listing.item())
                .displayName("§e" + listing.itemName())
                .lore(
                        "§7" + (bin ? "Buy it now: " : "Starting bid: ") + "§6" + (long) price + " coins",
                        "§7Category: §f" + listing.category().getDisplayName())
                .build());

        if (bin) {
            setItem(CONFIRM_SLOT, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .displayName("§aConfirm Purchase")
                    .lore("§7Buy for §6" + (long) price + " coins§7.")
                    .build(),
                    event -> { event.setCancelled(true); purchase(manager, listing, price); });
        } else {
            setItem(CONFIRM_SLOT, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                    .displayName("§7Auction listing")
                    .lore("§7Live bidding isn't available yet.", "§7Only Buy-It-Now listings can be bought.")
                    .build(), event -> event.setCancelled(true));
        }

        setItem(CANCEL_SLOT, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .displayName("§cCancel")
                .lore("§7Return to the auction house.")
                .build(),
                event -> {
                    event.setCancelled(true);
                    new AuctionHouseMenu(player).open(player);
                });
    }

    private void purchase(AuctionHouseManager manager, AuctionListing listing, double price) {
        UUID buyer = player.getUniqueId();
        EconomyManager economy = EconomyManager.getInstance();

        if (!manager.isActive(listingId)) {
            player.sendMessage("§cThat listing is no longer available.");
            new AuctionHouseMenu(player).open(player);
            return;
        }
        if (buyer.equals(listing.seller())) {
            player.sendMessage("§cYou can't buy your own listing.");
            return;
        }
        if (!economy.withdraw(buyer, price)) {
            player.sendMessage("§cYou can't afford that (§6" + (long) price + " coins§c).");
            return;
        }
        try {
            manager.placeBid(listingId, buyer, price); // BIN: settles the sale into the claim queues
        } catch (IllegalArgumentException ex) {
            economy.addCoins(buyer, price); // refund on failure
            player.sendMessage("§cUnable to purchase: " + ex.getMessage());
            new AuctionHouseMenu(player).open(player);
            return;
        }

        // Deliver the bought item(s) to the buyer's inventory.
        for (ItemStack item : manager.claimItems(buyer)) {
            for (ItemStack leftover : player.getInventory().addItem(item).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
        }
        // Pay the seller their proceeds now.
        double proceeds = manager.claimCoins(listing.seller());
        if (proceeds > 0) {
            economy.addCoins(listing.seller(), proceeds);
        }

        player.sendMessage("§aYou purchased §e" + listing.itemName()
                + " §afor §6" + (long) price + " coins§a!");
        new AuctionHouseMenu(player).open(player);
    }
}
