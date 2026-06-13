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
 *   <li>{@code /auction list}                                    — list all active auctions</li>
 *   <li>{@code /auction create <item> <price> [bin] [category]}  — create a new listing</li>
 *   <li>{@code /auction bid <id> <amount>}                       — place a bid or buy BIN</li>
 *   <li>{@code /auction view <id>}                               — view a specific listing</li>
 *   <li>{@code /auction cancel <id>}                             — cancel your own listing</li>
 *   <li>{@code /auction mine}                                    — list your own active auctions</li>
 *   <li>{@code /auction category <name>}                         — list auctions by category</li>
 * </ul>
 * </p>
 */
public final class AuctionCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "create", "bid", "view", "cancel", "mine", "category");

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
            player.sendMessage("Usage: /auction <list|create|bid|view|cancel|mine|category>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"     -> handleList(player);
            case "create"   -> handleCreate(player, args);
            case "bid"      -> handleBid(player, args);
            case "view"     -> handleView(player, args);
            case "cancel"   -> handleCancel(player, args);
            case "mine"     -> handleMine(player);
            case "category" -> handleCategory(player, args);
            default         -> player.sendMessage(
                    "Unknown subcommand. Usage: /auction <list|create|bid|view|cancel|mine|category>");
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
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length == 4) {
                return Arrays.asList("bin", "auction");
            }
            if (args.length == 5) {
                String prefix = args[4].toLowerCase();
                return Arrays.stream(AuctionManager.AuctionCategory.values())
                        .map(c -> c.name().toLowerCase())
                        .filter(n -> n.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("category")) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(AuctionManager.AuctionCategory.values())
                    .map(c -> c.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        List<AuctionManager.AuctionListing> active = auctionManager.getActiveListings();
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
            player.sendMessage("Usage: /auction create <item> <price> [bin] [category]");
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
        AuctionManager.AuctionType type = (args.length >= 4 && args[3].equalsIgnoreCase("bin"))
                ? AuctionManager.AuctionType.BIN
                : AuctionManager.AuctionType.AUCTION;
        AuctionManager.AuctionCategory category = AuctionManager.AuctionCategory.MISC;
        if (args.length >= 5) {
            try {
                category = AuctionManager.AuctionCategory.valueOf(args[4].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown category: " + args[4]);
                return;
            }
        }
        UUID listingId = auctionManager.createListing(player.getUniqueId(), itemName, price, type, category);
        player.sendMessage(String.format(
                "Listed '%s' for %.1f coins as a %s auction [%s]. ID: %s",
                itemName, price, type.getDisplayName(), category.getDisplayName(),
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
        AuctionManager.AuctionListing entry = auctionManager.getListing(listingId);
        double highestBid = auctionManager.getHighestBid(listingId);
        UUID highestBidder = auctionManager.getHighestBidder(listingId);
        player.sendMessage("=== Auction: " + entry.itemName() + " ===");
        player.sendMessage("ID:       " + entry.id());
        player.sendMessage("Type:     " + entry.type().getDisplayName());
        player.sendMessage("Category: " + entry.category().getDisplayName());
        player.sendMessage("Price:    " + entry.startingBid() + " coins");
        if (entry.type() == AuctionManager.AuctionType.AUCTION) {
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
        List<AuctionManager.AuctionListing> mine =
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
                e.type().name())));
    }

    private void handleCategory(Player player, String[] args) {
        if (args.length < 2) {
            StringBuilder sb = new StringBuilder("Usage: /auction category <");
            AuctionManager.AuctionCategory[] cats = AuctionManager.AuctionCategory.values();
            for (int i = 0; i < cats.length; i++) {
                if (i > 0) sb.append('|');
                sb.append(cats[i].name().toLowerCase());
            }
            sb.append('>');
            player.sendMessage(sb.toString());
            return;
        }
        AuctionManager.AuctionCategory category;
        try {
            category = AuctionManager.AuctionCategory.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown category: " + args[1]);
            return;
        }
        List<AuctionManager.AuctionListing> listings = auctionManager.getListingsByCategory(category);
        if (listings.isEmpty()) {
            player.sendMessage("No active auctions in category: " + category.getDisplayName());
            return;
        }
        player.sendMessage("=== " + category.getDisplayName() + " (" + listings.size() + " listings) ===");
        listings.stream()
                .sorted((a, b) -> a.itemName().compareToIgnoreCase(b.itemName()))
                .forEach(e -> player.sendMessage(String.format(
                        "[%s] %s — %.1f coins (%s)",
                        e.id().toString().substring(0, 8),
                        e.itemName(),
                        e.startingBid(),
                        e.type().name())));
    }

    /** Parses a short (8-char) or full UUID string; sends an error and returns null on failure. */
    private UUID resolveId(Player player, String input) {
        // Support both full UUIDs and the 8-char short-form shown in /auction list
        for (AuctionManager.AuctionListing e : auctionManager.getActiveListings()) {
            if (e.id().toString().startsWith(input) || e.id().toString().equals(input)) {
                return e.id();
            }
        }
        player.sendMessage("No active listing matches id: " + input);
        return null;
    }
}
