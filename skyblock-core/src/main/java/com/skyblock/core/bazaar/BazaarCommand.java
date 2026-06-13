package com.skyblock.core.bazaar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Handles the {@code /bazaar} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /bazaar info <item>}                          — show best bid/ask for an item</li>
 *   <li>{@code /bazaar buy <item> <qty> <price>}             — place a buy order</li>
 *   <li>{@code /bazaar sell <item> <qty> <price>}            — place a sell order</li>
 *   <li>{@code /bazaar cancel buy <orderId>}                 — cancel a buy order</li>
 *   <li>{@code /bazaar cancel sell <orderId>}                — cancel a sell order</li>
 * </ul>
 * </p>
 */
public final class BazaarCommand implements TabExecutor {

    private final BazaarManager bazaarManager;

    public BazaarCommand(BazaarManager bazaarManager) {
        this.bazaarManager = bazaarManager;
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
            case "info" -> handleInfo(player, args);
            case "buy" -> handleBuy(player, args);
            case "sell" -> handleSell(player, args);
            case "cancel" -> handleCancel(player, args);
            case "orders" -> handleOrders(player);
            default -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("info", "buy", "sell", "cancel", "orders").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        String sub = args[0].toLowerCase();
        if (args.length == 2 && (sub.equals("info") || sub.equals("buy") || sub.equals("sell"))) {
            String lower = args[1].toLowerCase();
            return Stream.of(BazaarProduct.values())
                    .map(p -> p.getItemId().toLowerCase())
                    .filter(id -> id.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && sub.equals("cancel")) {
            String lower = args[1].toLowerCase();
            return Arrays.asList("buy", "sell").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bazaar info <item>");
            return;
        }
        String itemId = args[1];
        double lowestAsk = bazaarManager.getLowestAsk(itemId);
        double highestBid = bazaarManager.getHighestBid(itemId);
        player.sendMessage("=== Bazaar: " + itemId + " ===");
        player.sendMessage("Lowest Ask: " + (lowestAsk == Double.MAX_VALUE ? "none" : lowestAsk + " coins"));
        player.sendMessage("Highest Bid: " + (highestBid == 0 ? "none" : highestBid + " coins"));
        int buyCount = bazaarManager.getBuyOrders(itemId).size();
        int sellCount = bazaarManager.getSellOrders(itemId).size();
        player.sendMessage("Buy Orders: " + buyCount + "  |  Sell Orders: " + sellCount);
    }

    private void handleBuy(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage("Usage: /bazaar buy <item> <quantity> <priceEach>");
            return;
        }
        String itemId = args[1];
        int quantity;
        double priceEach;
        try {
            quantity = Integer.parseInt(args[2]);
            priceEach = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid quantity or price.");
            return;
        }
        try {
            UUID orderId = bazaarManager.addBuyOrder(player.getUniqueId(), itemId, quantity, priceEach);
            player.sendMessage("Buy order placed for " + quantity + "x " + itemId + " at " + priceEach + " coins each.");
            player.sendMessage("Order ID: " + orderId);
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleSell(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage("Usage: /bazaar sell <item> <quantity> <priceEach>");
            return;
        }
        String itemId = args[1];
        int quantity;
        double priceEach;
        try {
            quantity = Integer.parseInt(args[2]);
            priceEach = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid quantity or price.");
            return;
        }
        try {
            UUID orderId = bazaarManager.addSellOrder(player.getUniqueId(), itemId, quantity, priceEach);
            player.sendMessage("Sell order placed for " + quantity + "x " + itemId + " at " + priceEach + " coins each.");
            player.sendMessage("Order ID: " + orderId);
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /bazaar cancel <buy|sell> <orderId>");
            return;
        }
        String type = args[1].toLowerCase();
        UUID orderId;
        try {
            orderId = UUID.fromString(args[2]);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid order ID.");
            return;
        }
        try {
            if (type.equals("buy")) {
                bazaarManager.cancelBuyOrder(orderId);
                player.sendMessage("Buy order " + orderId + " cancelled.");
            } else if (type.equals("sell")) {
                bazaarManager.cancelSellOrder(orderId);
                player.sendMessage("Sell order " + orderId + " cancelled.");
            } else {
                player.sendMessage("Usage: /bazaar cancel <buy|sell> <orderId>");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleOrders(Player player) {
        List<BazaarManager.BazaarOrder> orders = bazaarManager.getOrdersForPlayer(player.getUniqueId());
        if (orders.isEmpty()) {
            player.sendMessage("You have no active bazaar orders.");
            return;
        }
        player.sendMessage("=== Your Bazaar Orders ===");
        for (BazaarManager.BazaarOrder order : orders) {
            player.sendMessage(order.type().getDisplayName() + " | " + order.itemId()
                    + " x" + order.quantity() + " @ " + order.priceEach() + " coins | ID: " + order.id());
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Bazaar Commands ===");
        player.sendMessage("/bazaar info <item> — show best bid/ask");
        player.sendMessage("/bazaar buy <item> <qty> <price> — place a buy order");
        player.sendMessage("/bazaar sell <item> <qty> <price> — place a sell order");
        player.sendMessage("/bazaar cancel buy <orderId> — cancel a buy order");
        player.sendMessage("/bazaar cancel sell <orderId> — cancel a sell order");
        player.sendMessage("/bazaar orders — list your active orders");
    }
}
