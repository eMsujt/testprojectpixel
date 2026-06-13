package com.skyblock.core.auctionhouse;

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
 * Handles the {@code /auctionhouse} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /auctionhouse list}                       — list all active auctions</li>
 *   <li>{@code /auctionhouse create <item> <price> [bin]} — create a new listing</li>
 *   <li>{@code /auctionhouse bid <id> <amount>}           — place a bid or buy BIN</li>
 *   <li>{@code /auctionhouse view <id>}                   — view a specific listing</li>
 *   <li>{@code /auctionhouse cancel <id>}                 — cancel your own listing</li>
 *   <li>{@code /auctionhouse mine}                        — list your own active auctions</li>
 * </ul>
 * </p>
 */
public final class AuctionHouseCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "create", "bid", "view", "cancel", "mine");

    private final AuctionHouseManager auctionHouseManager;

    public AuctionHouseCommand(AuctionHouseManager auctionHouseManager) {
        this.auctionHouseManager = auctionHouseManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /auctionhouse <list|create|bid|view|cancel|mine>");
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
                    "Unknown subcommand. Usage: /auctionhouse <list|create|bid|view|cancel|mine>");
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
            return Arrays.asList("bin", "auction");
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        List<AuctionHouseManager.AuctionListing> active = auctionHouseManager.getActiveListings();
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
                        e.type().name())));
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /auctionhouse create <item> <price> [bin]");
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
        AuctionHouseManager.AuctionType type = (args.length >= 4 && args[3].equalsIgnoreCase("bin"))
                ? AuctionHouseManager.AuctionType.BIN
                : AuctionHouseManager.AuctionType.AUCTION;
        UUID listingId = auctionHouseManager.createListing(player.getUniqueId(), itemName, price, type);
        player.sendMessage(String.format(
                "Listed '%s' for %.1f coins as a %s auction. ID: %s",
                itemName, price, type.getDisplayName(),
                listingId.toString().substring(0, 8)));
    }

    private void handleBid(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /auctionhouse bid <id> <amount>");
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
            boolean purchased = auctionHouseManager.placeBid(listingId, player.getUniqueId(), amount);
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
            player.sendMessage("Usage: /auctionhouse view <id>");
            return;
        }
        UUID listingId = resolveId(player, args[1]);
        if (listingId == null) return;
        AuctionHouseManager.AuctionListing entry = auctionHouseManager.getListing(listingId);
        double highestBid = auctionHouseManager.getHighestBid(listingId);
        UUID highestBidder = auctionHouseManager.getHighestBidder(listingId);
        player.sendMessage("=== Auction: " + entry.itemName() + " ===");
        player.sendMessage("ID:      " + entry.id());
        player.sendMessage("Type:    " + entry.type().getDisplayName());
        player.sendMessage("Price:   " + entry.startingBid() + " coins");
        if (entry.type() == AuctionHouseManager.AuctionType.AUCTION) {
            player.sendMessage("Highest bid: " + highestBid + " coins"
                    + (highestBidder != null ? " (by " + highestBidder + ")" : " (no bids yet)"));
        }
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /auctionhouse cancel <id>");
            return;
        }
        UUID listingId = resolveId(player, args[1]);
        if (listingId == null) return;
        try {
            auctionHouseManager.cancelListing(listingId, player.getUniqueId());
            player.sendMessage("Listing cancelled.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("Could not cancel: " + e.getMessage());
        }
    }

    private void handleMine(Player player) {
        List<AuctionHouseManager.AuctionListing> mine =
                auctionHouseManager.getListingsBySeller(player.getUniqueId());
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
                e.type().name())));
    }

    /** Parses a short (8-char) or full UUID string; sends an error and returns null on failure. */
    private UUID resolveId(Player player, String input) {
        // Support both full UUIDs and the 8-char short-form shown in /auctionhouse list
        for (AuctionHouseManager.AuctionListing e : auctionHouseManager.getActiveListings()) {
            if (e.id().toString().startsWith(input) || e.id().toString().equals(input)) {
                return e.id();
            }
        }
        player.sendMessage("No active listing matches id: " + input);
        return null;
    }
}
