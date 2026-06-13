package com.skyblock.core.auction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AuctionHouseCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "create", "bid", "cancel", "search");

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
            case "search" -> handleSearch(player, args);
            default       -> sendHelp(player);
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
        Map<UUID, AuctionHouseManager.AuctionItem> all = manager.getItems();
        if (all.isEmpty()) {
            player.sendMessage("The auction house has no active listings.");
            return;
        }
        player.sendMessage("=== Auction House (" + all.size() + " listings) ===");
        all.entrySet().stream()
                .sorted((a, b) -> a.getValue().itemName().compareToIgnoreCase(b.getValue().itemName()))
                .forEach(e -> {
                    AuctionHouseManager.AuctionItem item = e.getValue();
                    player.sendMessage(String.format(
                            "[%s] %s — %d coins",
                            e.getKey().toString().substring(0, 8),
                            item.itemName(),
                            item.price()));
                });
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
        UUID id = manager.addItem(player.getUniqueId(), itemName, price);
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
        manager.purchase(id, player.getUniqueId());
        player.sendMessage("You purchased '" + item.itemName() + "' for " + amount + " coins.");
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /ah cancel <id>");
            return;
        }
        UUID id = resolveId(player, args[1]);
        if (id == null) return;
        if (manager.cancelItem(id, player.getUniqueId())) {
            player.sendMessage("Listing cancelled.");
        } else {
            player.sendMessage("Could not cancel: listing not found or you are not the seller.");
        }
    }

    private UUID resolveId(Player player, String input) {
        for (UUID id : manager.getItems().keySet()) {
            if (id.toString().startsWith(input) || id.toString().equals(input)) {
                return id;
            }
        }
        player.sendMessage("No active listing matches id: " + input);
        return null;
    }

    private void handleSearch(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /ah search <query>");
            return;
        }
        String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
        List<Map.Entry<UUID, AuctionHouseManager.AuctionItem>> results = manager.getItems().entrySet().stream()
                .filter(e -> e.getValue().itemName().toLowerCase().contains(query))
                .sorted((a, b) -> a.getValue().itemName().compareToIgnoreCase(b.getValue().itemName()))
                .collect(Collectors.toList());
        if (results.isEmpty()) {
            player.sendMessage("No listings found matching '" + query + "'.");
            return;
        }
        player.sendMessage("=== Search results for '" + query + "' (" + results.size() + ") ===");
        for (Map.Entry<UUID, AuctionHouseManager.AuctionItem> e : results) {
            AuctionHouseManager.AuctionItem item = e.getValue();
            player.sendMessage(String.format("[%s] %s — %d coins",
                    e.getKey().toString().substring(0, 8),
                    item.itemName(),
                    item.price()));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Auction House Commands ===");
        player.sendMessage("/ah list — show all active listings");
        player.sendMessage("/ah create <price> <itemName> — create a listing");
        player.sendMessage("/ah bid <id> <price> — purchase a listing");
        player.sendMessage("/ah cancel <id> — cancel your listing");
        player.sendMessage("/ah search <query> — search listings by name");
    }
}
