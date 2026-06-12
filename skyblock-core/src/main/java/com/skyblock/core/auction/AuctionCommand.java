package com.skyblock.core.auction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /auction} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /auction list}                       — list all active auctions</li>
 *   <li>{@code /auction create <item> <price> [bin]} — create a new listing</li>
 *   <li>{@code /auction bid <id> <amount>}           — place a bid or buy BIN</li>
 *   <li>{@code /auction view <id>}                   — view a specific listing</li>
 *   <li>{@code /auction cancel <id>}                 — cancel your own listing</li>
 *   <li>{@code /auction mine}                        — list your own active auctions</li>
 * </ul>
 * </p>
 */
public final class AuctionCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "create", "bid", "view", "cancel", "mine");

    private final AuctionManager auctionManager;

    public AuctionCommand(AuctionManager auctionManager) {
        this.auctionManager = auctionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /auction <list|create|bid|view|cancel|mine>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "create" -> handleCreate(player, args);
            case "bid"    -> handleBid(player, args);
            case "view"   -> handleView(player, args);
            case "cancel" -> handleCancel(player, args);
            case "mine"   -> handleMine(player);
            default       -> player.sendMessage(
                    "Unknown subcommand. Usage: /auction <list|create|bid|view|cancel|mine>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("create")) {
            return Arrays.asList("bin", "bid");
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        List<AuctionManager.AuctionEntry> active = auctionManager.getActiveListings();
        if (active.isEmpty()) {
            player.sendMessage("No active auctions.");
            return;
        }
        player.sendMessage("=== Auction House (" + active.size() + " listings) ===");
        active.stream()
                .sorted((a, b) -> a.itemName().compareToIgnoreCase(b.itemName()))
                .forEach(e -> player.sendMessage(String.format(
                        "[%s] %s — %.1f coins (%s)",
                        e.id().toString().substring(0, 8),
                        e.itemName(),
                        e.startingBid(),
                        e.binListing() ? "BIN" : "BID")));
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /auction create <item> <price> [bin]");
            return;
        }
        String itemName = args[1];
        double price;
        try {
            price = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid price: " + args[2]);
            return;
        }
        if (price < 0) {
            player.sendMessage("Price must not be negative.");
            return;
        }
        boolean bin = args.length >= 4 && args[3].equalsIgnoreCase("bin");
        UUID listingId = auctionManager.createListing(player.getUniqueId(), itemName, price, bin);
        player.sendMessage(String.format(
                "Listed '%s' for %.1f coins as a %s auction. ID: %s",
                itemName, price, bin ? "BIN" : "bid-based",
                listingId.toString().substring(0, 8)));
    }

    private void handleBid(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /auction bid <id> <amount>");
            return;
        }
        UUID listingId = resolveId(player, args[1]);
        if (listingId == null) return;
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[2]);
            return;
        }
        try {
            boolean purchased = auctionManager.placeBid(listingId, player.getUniqueId(), amount);
            if (purchased) {
                player.sendMessage("You purchased the item for " + amount + " coins!");
            } else {
                player.sendMessage("Your bid of " + amount + " coins has been placed.");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage("Could not place bid: " + e.getMessage());
        }
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /auction view <id>");
            return;
        }
        UUID listingId = resolveId(player, args[1]);
        if (listingId == null) return;
        AuctionManager.AuctionEntry entry = auctionManager.getListing(listingId);
        double highestBid = auctionManager.getHighestBid(listingId);
        UUID highestBidder = auctionManager.getHighestBidder(listingId);
        player.sendMessage("=== Auction: " + entry.itemName() + " ===");
        player.sendMessage("ID:      " + entry.id());
        player.sendMessage("Type:    " + (entry.binListing() ? "Buy It Now" : "Bid-based"));
        player.sendMessage("Price:   " + entry.startingBid() + " coins");
        if (!entry.binListing()) {
            player.sendMessage("Highest bid: " + highestBid + " coins"
                    + (highestBidder != null ? " (by " + highestBidder + ")" : " (no bids yet)"));
        }
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /auction cancel <id>");
            return;
        }
        UUID listingId = resolveId(player, args[1]);
        if (listingId == null) return;
        try {
            auctionManager.cancelListing(listingId, player.getUniqueId());
            player.sendMessage("Listing cancelled.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("Could not cancel: " + e.getMessage());
        }
    }

    private void handleMine(Player player) {
        List<AuctionManager.AuctionEntry> mine =
                auctionManager.getListingsBySeller(player.getUniqueId());
        if (mine.isEmpty()) {
            player.sendMessage("You have no active auction listings.");
            return;
        }
        player.sendMessage("=== Your Auctions ===");
        mine.forEach(e -> player.sendMessage(String.format(
                "[%s] %s — %.1f coins (%s)",
                e.id().toString().substring(0, 8),
                e.itemName(),
                e.startingBid(),
                e.binListing() ? "BIN" : "BID")));
    }

    /** Parses a short (8-char) or full UUID string; sends an error and returns null on failure. */
    private UUID resolveId(Player player, String input) {
        // Support both full UUIDs and the 8-char short-form shown in /auction list
        for (AuctionManager.AuctionEntry e : auctionManager.getActiveListings()) {
            if (e.id().toString().startsWith(input) || e.id().toString().equals(input)) {
                return e.id();
            }
        }
        player.sendMessage("No active listing matches id: " + input);
        return null;
    }
}
