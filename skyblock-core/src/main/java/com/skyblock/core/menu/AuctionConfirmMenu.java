package com.skyblock.core.menu;

import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.manager.AuctionHouseManager.AuctionListing;
import com.skyblock.core.manager.ChatInputManager;
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
    static final int CUSTOM_BID_SLOT = 15;
    static final int CANCEL_SLOT = 22;

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
            double minBid = manager.getMinimumBid(listingId);
            double topBid = manager.getHighestBid(listingId);
            setItem(CONFIRM_SLOT, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .displayName("§aPlace Minimum Bid")
                    .lore(
                            "§7Current bid: §6" + (long) topBid + " coins",
                            "§7Your bid: §6" + (long) minBid + " coins",
                            "",
                            "§7Outbid refunds and winnings",
                            "§7appear in §eYour Claims§7.")
                    .build(),
                    event -> { event.setCancelled(true); placeBid(manager, listing, minBid); });

            setItem(CUSTOM_BID_SLOT, new ItemBuilder(Material.OAK_SIGN)
                    .displayName("§aCustom Bid")
                    .lore(
                            "§7Bid a specific amount",
                            "§7(at least §6" + (long) minBid + " coins§7).",
                            "",
                            "§eClick to type an amount!")
                    .build(),
                    event -> { event.setCancelled(true); promptCustomBid(minBid); });
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

    private void placeBid(AuctionHouseManager manager, AuctionListing listing, double amount) {
        UUID bidder = player.getUniqueId();
        EconomyManager economy = EconomyManager.getInstance();

        if (!manager.isActive(listingId)) {
            player.sendMessage("§cThat listing is no longer available.");
            new AuctionHouseMenu(player).open(player);
            return;
        }
        if (bidder.equals(listing.seller())) {
            player.sendMessage("§cYou can't bid on your own listing.");
            return;
        }
        if (!economy.withdraw(bidder, amount)) {
            player.sendMessage("§cYou can't afford that bid (§6" + (long) amount + " coins§c).");
            return;
        }
        try {
            // Escrows this bid; refunds the previous leader into their claim queue.
            manager.placeBid(listingId, bidder, amount);
        } catch (IllegalArgumentException ex) {
            economy.addCoins(bidder, amount); // refund on failure
            player.sendMessage("§cUnable to bid: " + ex.getMessage());
            new AuctionHouseMenu(player).open(player);
            return;
        }
        player.sendMessage("§aBid §6" + (long) amount + " coins §aon §e" + listing.itemName()
                + "§a! If you're outbid or win, check §eYour Claims§a.");
        new AuctionHouseMenu(player).open(player);
    }

    /** Prompts for a custom bid amount in chat, then places it (re-validated against the live minimum). */
    private void promptCustomBid(double minBidAtPrompt) {
        player.closeInventory();
        player.sendMessage("§eType your bid amount in chat §7(min §6" + (long) minBidAtPrompt
                + " coins§7, e.g. §f1.5m§7), or §ccancel§7.");
        ChatInputManager.getInstance().request(player.getUniqueId(), input -> {
            if (input.equalsIgnoreCase("cancel")) {
                player.sendMessage("§cCancelled.");
                new AuctionHouseMenu(player).open(player);
                return;
            }
            AuctionHouseManager manager = AuctionHouseManager.getInstance();
            if (!manager.isActive(listingId)) {
                player.sendMessage("§cThat listing is no longer available.");
                new AuctionHouseMenu(player).open(player);
                return;
            }
            long amount = ChatInputManager.parseAmount(input);
            if (amount <= 0) {
                player.sendMessage("§c'" + input + "' is not a valid amount.");
                new AuctionConfirmMenu(player, listingId).open(player);
                return;
            }
            long minBid = (long) manager.getMinimumBid(listingId);
            if (amount < minBid) {
                player.sendMessage("§cYour bid must be at least §6" + minBid + " coins§c.");
                new AuctionConfirmMenu(player, listingId).open(player);
                return;
            }
            placeBid(manager, manager.getListing(listingId), amount);
        });
    }
}

