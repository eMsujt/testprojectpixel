package com.skyblock.core.bazaar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class BazaarCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "buy", "sell", "cancel", "orders", "history");

    private final BazaarManager manager;

    public BazaarCommand(BazaarManager manager) {
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
            case "info"   -> handleInfo(player, args);
            case "buy"    -> handleBuy(player, args);
            case "sell"   -> handleSell(player, args);
            case "cancel" -> handleCancel(player, args);
            case "orders"  -> handleOrders(player);
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
        if (args.length == 2 && args[0].equalsIgnoreCase("cancel")) {
            String prefix = args[1].toLowerCase();
            return Arrays.asList("buy", "sell").stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bazaar info <item>");
            return;
        }
        String itemId = args[1];
        double lowestAsk = manager.getLowestAsk(itemId);
        double highestBid = manager.getHighestBid(itemId);
        player.sendMessage("=== Bazaar: " + itemId + " ===");
        player.sendMessage("Lowest Ask: " + (lowestAsk == Double.MAX_VALUE ? "none" : lowestAsk + " coins"));
        player.sendMessage("Highest Bid: " + (highestBid == 0 ? "none" : highestBid + " coins"));
        player.sendMessage("Buy Orders: " + manager.getBuyOrderCount(itemId)
                + "  |  Sell Orders: " + manager.getSellOrderCount(itemId));
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
        if (quantity <= 0 || priceEach <= 0) {
            player.sendMessage("Quantity and price must be positive.");
            return;
        }
        UUID orderId = manager.addBuyOrder(player.getUniqueId(), itemId, quantity, priceEach);
        player.sendMessage("Buy order placed for " + quantity + "x " + itemId + " at " + priceEach + " coins each.");
        player.sendMessage("Order ID: " + orderId.toString().substring(0, 8));
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
        if (quantity <= 0 || priceEach <= 0) {
            player.sendMessage("Quantity and price must be positive.");
            return;
        }
        UUID orderId = manager.addSellOrder(player.getUniqueId(), itemId, quantity, priceEach);
        player.sendMessage("Sell order placed for " + quantity + "x " + itemId + " at " + priceEach + " coins each.");
        player.sendMessage("Order ID: " + orderId.toString().substring(0, 8));
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
        boolean cancelled = switch (type) {
            case "buy"  -> manager.cancelBuyOrder(orderId, player.getUniqueId());
            case "sell" -> manager.cancelSellOrder(orderId, player.getUniqueId());
            default -> {
                player.sendMessage("Usage: /bazaar cancel <buy|sell> <orderId>");
                yield false;
            }
        };
        if (cancelled) {
            player.sendMessage(Character.toUpperCase(type.charAt(0)) + type.substring(1)
                    + " order " + orderId.toString().substring(0, 8) + " cancelled.");
        } else if (type.equals("buy") || type.equals("sell")) {
            player.sendMessage("Order not found or you are not the owner.");
        }
    }

    private void handleOrders(Player player) {
        List<BazaarManager.BazaarOrder> orders = manager.getOrdersForPlayer(player.getUniqueId());
        if (orders.isEmpty()) {
            player.sendMessage("You have no active bazaar orders.");
            return;
        }
        player.sendMessage("=== Your Bazaar Orders ===");
        for (BazaarManager.BazaarOrder order : orders) {
            player.sendMessage(order.type().getDisplayName() + " | " + order.itemId()
                    + " x" + order.quantity() + " @ " + order.priceEach() + " coins"
                    + " | ID: " + order.id().toString().substring(0, 8));
        }
    }

    private void handleHistory(Player player) {
        List<String> history = manager.getBazaarHistory(player.getUniqueId());
        if (history.isEmpty()) {
            player.sendMessage("You have no bazaar history.");
            return;
        }
        player.sendMessage("=== Bazaar History ===");
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
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
        player.sendMessage("/bazaar history — show your bazaar history");
    }
}
