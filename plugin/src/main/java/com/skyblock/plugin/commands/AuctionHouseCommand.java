package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.AuctionHouseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AuctionHouseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleList(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "create" -> handleCreate(player, args);
            case "bid"    -> handleBid(player, args);
            case "cancel" -> handleCancel(player, args);
            case "search" -> handleSearch(player, args);
            case "my"      -> handleMy(player);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void handleList(Player player) {
        List<AuctionHouseManager.AuctionListing> top = AuctionHouseManager.getInstance().getAllListings().stream()
                .sorted((a, b) -> Double.compare(b.currentBid(), a.currentBid()))
                .limit(5)
                .toList();
        if (top.isEmpty()) {
            player.sendMessage("The auction house has no active listings.");
            return;
        }
        player.sendMessage("=== Auction House (Top 5) ===");
        for (AuctionHouseManager.AuctionListing item : top) {
            player.sendMessage("[" + item.id().toString().substring(0, 8) + "] "
                    + item.itemName() + " x" + item.quantity()
                    + " — Starting: " + item.startingBid()
                    + " | Current: " + item.currentBid() + " coins");
        }
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /auctionhouse create <startingBid> <itemName>");
            return;
        }
        double startingBid;
        try {
            startingBid = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid bid: " + args[1]);
            return;
        }
        if (startingBid < 0) {
            player.sendMessage("Starting bid must not be negative.");
            return;
        }
        String itemName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        long endTime = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000;
        UUID listingId = UUID.randomUUID();
        AuctionHouseManager.AuctionListing listing = new AuctionHouseManager.AuctionListing(
                listingId, player.getUniqueId(), itemName, 1, startingBid, startingBid, null, endTime);
        AuctionHouseManager.getInstance().addListing(player.getUniqueId(), listing);
        player.sendMessage(String.format("Listed '%s' starting at %.1f coins. ID: %s",
                itemName, startingBid, listingId.toString().substring(0, 8)));
    }

    private void handleBid(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /auctionhouse bid <id> <amount>");
            return;
        }
        AuctionHouseManager.AuctionListing listing = findListing(player, args[1]);
        if (listing == null) return;
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[2]);
            return;
        }
        if (listing.seller().equals(player.getUniqueId())) {
            player.sendMessage("You cannot bid on your own listing.");
            return;
        }
        if (amount <= listing.currentBid()) {
            player.sendMessage("Bid must exceed current bid of " + listing.currentBid() + " coins.");
            return;
        }
        AuctionHouseManager.getInstance().placeBid(listing.seller(), listing.id(), player.getUniqueId(), amount);
        player.sendMessage(String.format("Bid of %.1f coins placed on '%s'.", amount, listing.itemName()));
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /auctionhouse cancel <id>");
            return;
        }
        UUID id = parseId(player, args[1]);
        if (id == null) return;
        boolean removed = AuctionHouseManager.getInstance().removeListing(player.getUniqueId(), id);
        if (removed) {
            player.sendMessage("Listing cancelled.");
        } else {
            player.sendMessage("No active listing with that ID found under your account.");
        }
    }

    private void handleSearch(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /auctionhouse search <query>");
            return;
        }
        String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
        List<AuctionHouseManager.AuctionListing> results = AuctionHouseManager.getInstance().getAllListings().stream()
                .filter(item -> item.itemName().toLowerCase().contains(query))
                .sorted((a, b) -> a.itemName().compareToIgnoreCase(b.itemName()))
                .collect(Collectors.toList());
        if (results.isEmpty()) {
            player.sendMessage("No listings found matching '" + query + "'.");
            return;
        }
        player.sendMessage("=== Search: '" + query + "' (" + results.size() + ") ===");
        for (AuctionHouseManager.AuctionListing item : results) {
            player.sendMessage("[" + item.id().toString().substring(0, 8) + "] "
                    + item.itemName() + " — " + item.currentBid() + " coins");
        }
    }

    private void handleMy(Player player) {
        List<AuctionHouseManager.AuctionListing> mine = AuctionHouseManager.getInstance()
                .getListings(player.getUniqueId());
        if (mine.isEmpty()) {
            player.sendMessage("You have no active auction listings.");
            return;
        }
        player.sendMessage("=== Your Listings (" + mine.size() + ") ===");
        for (AuctionHouseManager.AuctionListing item : mine) {
            player.sendMessage("[" + item.id().toString().substring(0, 8) + "] "
                    + item.itemName() + " x" + item.quantity()
                    + " — Starting: " + item.startingBid()
                    + " | Current: " + item.currentBid() + " coins");
        }
    }

    private void handleHistory(Player player) {
        List<String> history = AuctionHouseManager.getInstance().getAuctionHistory(player.getUniqueId());
        player.sendMessage("=== Auction History ===");
        if (history.isEmpty()) {
            player.sendMessage("You have no auction history.");
            return;
        }
        for (String entry : history) {
            player.sendMessage(entry);
        }
    }

    private AuctionHouseManager.AuctionListing findListing(Player player, String input) {
        UUID id = parseId(player, input);
        if (id == null) return null;
        for (AuctionHouseManager.AuctionListing listing : AuctionHouseManager.getInstance().getAllListings()) {
            if (listing.id().equals(id)) return listing;
        }
        player.sendMessage("No active listing matches id: " + input);
        return null;
    }

    private UUID parseId(Player player, String input) {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException ignored) {
        }
        // support short 8-char prefix lookup
        for (AuctionHouseManager.AuctionListing listing : AuctionHouseManager.getInstance().getAllListings()) {
            if (listing.id().toString().startsWith(input)) return listing.id();
        }
        player.sendMessage("No active listing matches id: " + input);
        return null;
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Auction House Commands ===");
        player.sendMessage("/auctionhouse list                     — show all active listings");
        player.sendMessage("/auctionhouse create <bid> <name>      — create a listing");
        player.sendMessage("/auctionhouse bid <id> <amount>        — place a bid on a listing");
        player.sendMessage("/auctionhouse cancel <id>              — cancel your listing");
        player.sendMessage("/auctionhouse search <query>           — search listings by name");
        player.sendMessage("/auctionhouse my                       — show your active listings");
        player.sendMessage("/auctionhouse history                  — show your auction history");
    }
}
