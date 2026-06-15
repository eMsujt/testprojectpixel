package com.skyblock.core.command;

import com.skyblock.core.manager.AuctionHouseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AuctionHouseCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "create", "bid", "cancel", "search", "history");

    private final AuctionHouseManager manager;

    public AuctionHouseCommand(AuctionHouseManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "create" -> handleCreate(player, args);
            case "bid"    -> handleBid(player, args);
            case "cancel" -> handleCancel(player, args);
            case "search"  -> handleSearch(player, args);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
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
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        List<AuctionHouseManager.AuctionItem> items = manager.getActiveItems();
        if (items.isEmpty()) {
            player.sendMessage("The auction house has no active listings.");
            return;
        }
        player.sendMessage("=== Auction House (" + items.size() + " listings) ===");
        items.stream()
                .sorted((a, b) -> a.itemName().compareToIgnoreCase(b.itemName()))
                .forEach(item -> player.sendMessage(String.format(
                        "%s — %d coins",
                        item.itemName(),
                        item.price())));
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /ah create <price> <itemName>");
            return;
        }
        long price;
        try {
            price = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid price: " + args[1]);
            return;
        }
        if (price < 0) {
            player.sendMessage("Price must not be negative.");
            return;
        }
        String itemName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        long endEpoch = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000;
        UUID id = manager.addItem(player.getUniqueId(), itemName, price, endEpoch);
        player.sendMessage(String.format("Listed '%s' for %d coins. ID: %s",
                itemName, price, id.toString().substring(0, 8)));
    }

    private void handleBid(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /ah bid <id> <price>");
            return;
        }
        UUID id = resolveId(player, args[1]);
        if (id == null) return;
        long amount;
        try {
            amount = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid price: " + args[2]);
            return;
        }
        AuctionHouseManager.AuctionItem item = manager.getItem(id);
        if (item == null) {
            player.sendMessage("Listing not found.");
            return;
        }
        if (item.seller().equals(player.getUniqueId())) {
            player.sendMessage("You cannot purchase your own listing.");
            return;
        }
        if (amount < item.price()) {
            player.sendMessage("Offer must meet the asking price of " + item.price() + " coins.");
            return;
        }
        manager.cancelItem(id, item.seller());
        manager.recordAuction(player.getUniqueId(), "Purchased " + item.itemName() + " for " + amount + " coins");
        player.sendMessage("You purchased '" + item.itemName() + "' for " + amount + " coins.");
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /ah cancel <id>");
            return;
        }
        UUID id = resolveId(player, args[1]);
        if (id == null) return;
        try {
            manager.cancelItem(id, player.getUniqueId());
            player.sendMessage("Listing cancelled.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("Could not cancel: " + e.getMessage());
        }
    }

    private void handleSearch(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /ah search <query>");
            return;
        }
        String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
        List<AuctionHouseManager.AuctionItem> results = manager.getActiveItems().stream()
                .filter(item -> item.itemName().toLowerCase().contains(query))
                .sorted((a, b) -> a.itemName().compareToIgnoreCase(b.itemName()))
                .collect(Collectors.toList());
        if (results.isEmpty()) {
            player.sendMessage("No listings found matching '" + query + "'.");
            return;
        }
        player.sendMessage("=== Search results for '" + query + "' (" + results.size() + ") ===");
        for (AuctionHouseManager.AuctionItem item : results) {
            player.sendMessage(String.format("%s — %d coins", item.itemName(), item.price()));
        }
    }

    private void handleHistory(Player player) {
        List<String> history = manager.getAuctionHistory(player.getUniqueId());
        if (history.isEmpty()) {
            player.sendMessage("You have no auction history.");
            return;
        }
        player.sendMessage("=== Auction History ===");
        for (String entry : history) {
            player.sendMessage(entry);
        }
    }

    private UUID resolveId(Player player, String input) {
        try {
            UUID id = UUID.fromString(input);
            if (manager.getItem(id) != null) return id;
        } catch (IllegalArgumentException ignored) {}
        player.sendMessage("No active listing matches id: " + input);
        return null;
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Auction House Commands ===");
        player.sendMessage("/ah list — show all active listings");
        player.sendMessage("/ah create <price> <itemName> — create a listing");
        player.sendMessage("/ah bid <id> <price> — purchase a listing");
        player.sendMessage("/ah cancel <id> — cancel your listing");
        player.sendMessage("/ah search <query> — search listings by name");
        player.sendMessage("/ah history — show your auction history");
    }
}
