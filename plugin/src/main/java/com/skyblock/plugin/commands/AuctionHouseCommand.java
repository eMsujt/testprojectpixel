package com.skyblock.plugin.commands;

import com.skyblock.core.manager.AuctionHouseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AuctionHouseCommand implements CommandExecutor {

    private static AuctionHouseManager manager() {
        return AuctionHouseManager.getInstance();
    }

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
            case "list"    -> handleList(player);
            case "create"  -> handleCreate(player, args);
            case "bid"     -> handleBid(player, args);
            case "cancel"  -> handleCancel(player, args);
            case "search"  -> handleSearch(player, args);
            case "mine"    -> handleMine(player);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void handleList(Player player) {
        List<AuctionHouseManager.AuctionListing> top = manager().getActiveListings().stream()
                .map(manager()::getListing)
                .sorted((a, b) -> Double.compare(b.startingBid(), a.startingBid()))
                .limit(5)
                .collect(Collectors.toList());
        if (top.isEmpty()) {
            player.sendMessage("The auction house has no active listings.");
            return;
        }
        player.sendMessage("=== Auction House (Top 5) ===");
        for (AuctionHouseManager.AuctionListing listing : top) {
            player.sendMessage("[" + listing.id().toString().substring(0, 8) + "] "
                    + listing.itemName() + " [" + listing.category().getDisplayName() + "]"
                    + " — " + listing.startingBid() + " coins (" + listing.type().name() + ")");
        }
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /auctionhouse create <startingBid> <category> [bin]");
            return;
        }
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType().isAir()) {
            player.sendMessage("You must hold an item to list it.");
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
        AuctionHouseManager.AuctionCategory category;
        try {
            category = AuctionHouseManager.AuctionCategory.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown category: " + args[2]);
            return;
        }
        AuctionHouseManager.AuctionType type = (args.length >= 4 && args[3].equalsIgnoreCase("bin"))
                ? AuctionHouseManager.AuctionType.BIN
                : AuctionHouseManager.AuctionType.AUCTION;
        String itemName = held.hasItemMeta() && held.getItemMeta().hasDisplayName()
                ? held.getItemMeta().getDisplayName()
                : held.getType().name();
        UUID listingId = manager().createListing(player.getUniqueId(), held.clone(), itemName,
                category, startingBid, type);
        player.sendMessage(String.format("Listed '%s' starting at %.1f coins. ID: %s",
                itemName, startingBid, listingId.toString().substring(0, 8)));
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
            boolean purchased = manager().placeBid(listingId, player.getUniqueId(), amount);
            if (purchased) {
                player.sendMessage("You purchased the item for " + amount + " coins!");
            } else {
                player.sendMessage("Your bid of " + amount + " coins has been placed.");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage("Could not place bid: " + e.getMessage());
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
            manager().cancelListing(listingId, player.getUniqueId());
            player.sendMessage("Listing cancelled.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("Could not cancel: " + e.getMessage());
        }
    }

    private void handleSearch(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /auctionhouse search <query>");
            return;
        }
        String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
        List<AuctionHouseManager.AuctionListing> results = manager().getActiveListings().stream()
                .map(manager()::getListing)
                .filter(l -> l.itemName().toLowerCase().contains(query))
                .sorted((a, b) -> a.itemName().compareToIgnoreCase(b.itemName()))
                .collect(Collectors.toList());
        if (results.isEmpty()) {
            player.sendMessage("No listings found matching '" + query + "'.");
            return;
        }
        player.sendMessage("=== Search: '" + query + "' (" + results.size() + ") ===");
        for (AuctionHouseManager.AuctionListing l : results) {
            player.sendMessage("[" + l.id().toString().substring(0, 8) + "] "
                    + l.itemName() + " — " + l.startingBid() + " coins");
        }
    }

    private void handleMine(Player player) {
        List<AuctionHouseManager.AuctionListing> mine = manager().getActiveListings().stream()
                .map(manager()::getListing)
                .filter(l -> player.getUniqueId().equals(l.seller()))
                .collect(Collectors.toList());
        if (mine.isEmpty()) {
            player.sendMessage("You have no active auction listings.");
            return;
        }
        player.sendMessage("=== Your Listings (" + mine.size() + ") ===");
        for (AuctionHouseManager.AuctionListing l : mine) {
            player.sendMessage("[" + l.id().toString().substring(0, 8) + "] "
                    + l.itemName() + " — " + l.startingBid() + " coins (" + l.type().name() + ")");
        }
    }

    private void handleHistory(Player player) {
        List<String> history = manager().getAuctionHistory(player.getUniqueId());
        player.sendMessage("=== Auction History ===");
        if (history.isEmpty()) {
            player.sendMessage("You have no auction history.");
            return;
        }
        for (String entry : history) {
            player.sendMessage(entry);
        }
    }

    private UUID resolveId(Player player, String input) {
        for (UUID id : manager().getActiveListings()) {
            if (id.toString().startsWith(input) || id.toString().equals(input)) return id;
        }
        player.sendMessage("No active listing matches id: " + input);
        return null;
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Auction House Commands ===");
        player.sendMessage("/auctionhouse list                         — show active listings");
        player.sendMessage("/auctionhouse create <bid> <category> [bin] — create a listing");
        player.sendMessage("/auctionhouse bid <id> <amount>            — place a bid");
        player.sendMessage("/auctionhouse cancel <id>                  — cancel your listing");
        player.sendMessage("/auctionhouse search <query>               — search listings");
        player.sendMessage("/auctionhouse mine                         — your active listings");
        player.sendMessage("/auctionhouse history                      — your auction history");
    }
}
