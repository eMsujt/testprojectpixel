package com.skyblock.core.command;

import com.skyblock.core.auction.AuctionHouseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /auction} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /auction create <price> [bin]} — list held item at starting bid/BIN price</li>
 *   <li>{@code /auction view}                 — list all active auction IDs</li>
 *   <li>{@code /auction cancel <id>}          — cancel one of your listings</li>
 *   <li>{@code /auction bid <id> <amount>}    — place a bid or purchase a BIN listing</li>
 * </ul>
 * </p>
 */
/**
 * @deprecated Use {@link com.skyblock.core.auction.AuctionCommand} instead.
 */
@Deprecated
public final class AuctionCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("create", "view", "cancel", "bid");

    private final AuctionHouseManager auctionManager;

    public AuctionCommand(AuctionHouseManager auctionManager) {
        this.auctionManager = auctionManager;
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
            case "create" -> handleCreate(player, args);
            case "view"   -> handleView(player);
            case "cancel" -> handleCancel(player, args);
            case "bid"    -> handleBid(player, args);
            default -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("bid"))) {
            Set<UUID> active = auctionManager.getActiveListings();
            return active.stream()
                    .map(UUID::toString)
                    .filter(id -> id.startsWith(args[1]))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /auction create <price> [bin]");
            return;
        }
        double price;
        try {
            price = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid price: " + args[1]);
            return;
        }
        if (price < 0) {
            player.sendMessage("Price must not be negative.");
            return;
        }
        boolean bin = args.length >= 3 && args[2].equalsIgnoreCase("bin");
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held == null || held.getType().isAir()) {
            player.sendMessage("You must hold an item to create an auction listing.");
            return;
        }
        String itemName = held.hasItemMeta() && held.getItemMeta().hasDisplayName()
                ? held.getItemMeta().getDisplayName()
                : capitalize(held.getType().name());
        UUID listingId = auctionManager.createListing(player.getUniqueId(), held.clone(), itemName, price, bin);
        String type = bin ? "BIN" : "bid";
        player.sendMessage("Listed " + itemName + " as a " + type + " auction for " + price + " coins. ID: " + listingId);
    }

    private void handleView(Player player) {
        Set<UUID> active = auctionManager.getActiveListings();
        if (active.isEmpty()) {
            player.sendMessage("There are no active auction listings.");
            return;
        }
        player.sendMessage("=== Active Auctions ===");
        for (UUID id : active) {
            AuctionHouseManager.AuctionListing listing = auctionManager.getListing(id);
            double bid = auctionManager.getHighestBid(id);
            String type = listing.binListing() ? "BIN" : "bid";
            player.sendMessage(id + " | " + listing.itemName() + " | " + type + " | " + bid + " coins");
        }
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /auction cancel <id>");
            return;
        }
        UUID listingId = parseUuid(player, args[1]);
        if (listingId == null) return;
        try {
            auctionManager.cancelListing(listingId, player.getUniqueId());
            player.sendMessage("Auction listing " + listingId + " cancelled.");
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleBid(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /auction bid <id> <amount>");
            return;
        }
        UUID listingId = parseUuid(player, args[1]);
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
                player.sendMessage("You purchased the item for " + amount + " coins.");
            } else {
                player.sendMessage("Bid of " + amount + " coins placed on listing " + listingId + ".");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Auction House ===");
        player.sendMessage("/auction create <price> [bin] — list held item");
        player.sendMessage("/auction view               — browse active listings");
        player.sendMessage("/auction bid <id> <amount>  — place a bid or buy BIN");
        player.sendMessage("/auction cancel <id>        — cancel your listing");
    }

    private UUID parseUuid(Player player, String input) {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid listing ID: " + input);
            return null;
        }
    }

    private static String capitalize(String name) {
        if (name.isEmpty()) return name;
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase().replace('_', ' ');
    }
}
