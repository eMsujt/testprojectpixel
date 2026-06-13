package com.skyblock.core.shop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /shop} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /shop list}                          — list all shop types</li>
 *   <li>{@code /shop browse <shopType>}             — list items sold at a shop</li>
 *   <li>{@code /shop info <shopType> <itemName>}    — show details for a specific item</li>
 * </ul>
 * </p>
 */
public final class ShopCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "browse", "info");

    private final ShopManager shopManager;

    public ShopCommand(ShopManager shopManager) {
        this.shopManager = shopManager;
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
            case "browse" -> handleBrowse(player, args);
            case "info"   -> handleInfo(player, args);
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("browse") || args[0].equalsIgnoreCase("info"))) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(ShopManager.ShopType.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("info")) {
            ShopManager.ShopType type;
            try {
                type = ShopManager.ShopType.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                return Collections.emptyList();
            }
            String prefix = args[2].toLowerCase();
            return shopManager.getItems(type).stream()
                    .map(ShopManager.ShopItem::name)
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== Shop Types ===");
        for (ShopManager.ShopType type : ShopManager.ShopType.values()) {
            player.sendMessage("  " + type.getDisplayName() + " (" + type.name() + ")");
        }
    }

    private void handleBrowse(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /shop browse <shopType>");
            return;
        }
        ShopManager.ShopType type;
        try {
            type = ShopManager.ShopType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown shop type: " + args[1]);
            return;
        }
        List<ShopManager.ShopItem> shopItems = shopManager.getItems(type);
        player.sendMessage("=== " + type.getDisplayName() + " ===");
        if (shopItems.isEmpty()) {
            player.sendMessage("  No items available.");
            return;
        }
        for (ShopManager.ShopItem item : shopItems) {
            player.sendMessage("  " + item.name() + " — " + item.price() + " coins");
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /shop info <shopType> <itemName>");
            return;
        }
        ShopManager.ShopType type;
        try {
            type = ShopManager.ShopType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown shop type: " + args[1]);
            return;
        }
        String itemName = args[2];
        ShopManager.ShopItem item = shopManager.findByName(type, itemName);
        if (item == null) {
            player.sendMessage("Item not found: " + itemName);
            return;
        }
        player.sendMessage("=== " + item.name() + " ===");
        player.sendMessage("  Material : " + item.material());
        player.sendMessage("  Price    : " + item.price() + " coins");
        player.sendMessage("  Shop     : " + type.getDisplayName());
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Shop Commands ===");
        player.sendMessage("/shop list                       — list all shop types");
        player.sendMessage("/shop browse <shopType>          — list items sold at a shop");
        player.sendMessage("/shop info <shopType> <itemName> — show item details");
    }
}
