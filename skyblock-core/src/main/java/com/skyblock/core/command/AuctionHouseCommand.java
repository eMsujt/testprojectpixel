package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.auction.manager.AuctionHouseManager;
import com.skyblock.core.menu.AuctionHouseMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AuctionHouseCommand extends PlayerCommand {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "create", "bid", "view", "cancel", "mine", "history");
    private static final List<String> CATEGORY_NAMES = Arrays.stream(AuctionHouseManager.AuctionCategory.values())
            .map(c -> c.name().toLowerCase())
            .collect(Collectors.toList());

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "list"    -> handleList(player, args);
            case "create"  -> handleCreate(player, args);
            case "bid"     -> handleBid(player, args);
            case "view"    -> handleView(player, args);
            case "cancel"  -> handleCancel(player, args);
            case "mine"    -> handleMine(player);
            case "history" -> handleHistory(player);
            default        -> player.sendMessage(
                    "§cUnknown sub-command. Usage: /" + label + " [list|create|bid|view|cancel|mine|history]");
        }
        return true;
    }

    @Override
    protected void openMenu(Player player) {
        new AuctionHouseMenu(SkyBlockCore.getInstance(), player).open(player);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
            String prefix = args[1].toLowerCase();
            return CATEGORY_NAMES.stream().filter(c -> c.startsWith(prefix)).collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            String prefix = args[2].toLowerCase();
            return CATEGORY_NAMES.stream().filter(c -> c.startsWith(prefix)).collect(Collectors.toList());
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("create")) {
            return Arrays.asList("bin", "auction");
        }
        return Collections.emptyList();
    }

    private void handleList(Player player, String[] args) {
        AuctionHouseManager manager = AuctionHouseManager.getInstance();
        List<AuctionHouseManager.AuctionListing> active;
        String header;
        if (args.length >= 2) {
            AuctionHouseManager.AuctionCategory category = parseCategory(player, args[1]);
            if (category == null) return;
            active = manager.getListingsByCategory(category);
            header = "=== Auction House: " + category.getDisplayName() + " (" + active.size() + " listings) ===";
        } else {
            active = manager.getActiveListings().stream()
                    .map(manager::getListing)
                    .collect(Collectors.toList());
            header = "=== Auction House (" + active.size() + " listings) ===";
        }
        if (active.isEmpty()) {
            player.sendMessage("No active auction house listings.");
            return;
        }
        player.sendMessage(header);
        active.stream()
                .sorted((a, b) -> a.itemName().compareToIgnoreCase(b.itemName()))
                .forEach(e -> player.sendMessage(String.format(
                        "[%s] %s [%s] — %.1f coins (%s)",
                        e.id().toString().substring(0, 8),
                        e.itemName(),
                        e.category().getDisplayName(),
                        e.startingBid(),
                        e.type().name())));
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /ah create <price> <category> [bin]");
            return;
        }
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType().isAir()) {
            player.sendMessage("You must hold an item to list it.");
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
        AuctionHouseManager.AuctionCategory category = parseCategory(player, args[2]);
        if (category == null) return;
        AuctionHouseManager.AuctionType type = (args.length >= 4 && args[3].equalsIgnoreCase("bin"))
                ? AuctionHouseManager.AuctionType.BIN
                : AuctionHouseManager.AuctionType.AUCTION;
        String itemName = held.hasItemMeta() && held.getItemMeta().hasDisplayName()
                ? held.getItemMeta().getDisplayName()
                : held.getType().name();
        UUID listingId = AuctionHouseManager.getInstance().createListing(
                player.getUniqueId(), held.clone(), itemName, category, price, type);
        player.sendMessage(String.format(
                "Listed '%s' [%s] for %.1f coins as a %s listing. ID: %s",
                itemName, category.getDisplayName(), price, type.getDisplayName(),
                listingId.toString().substring(0, 8)));
    }

    private void handleBid(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /ah bid <id> <amount>");
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
            boolean purchased = AuctionHouseManager.getInstance().placeBid(listingId, player.getUniqueId(), amount);
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
            player.sendMessage("Usage: /ah view <id>");
            return;
        }
        UUID listingId = resolveId(player, args[1]);
        if (listingId == null) return;
        AuctionHouseManager manager = AuctionHouseManager.getInstance();
        AuctionHouseManager.AuctionListing listing = manager.getListing(listingId);
        double highestBid = manager.getHighestBid(listingId);
        UUID highestBidder = manager.getHighestBidder(listingId);
        player.sendMessage("=== Listing: " + listing.itemName() + " ===");
        player.sendMessage("ID:       " + listing.id());
        player.sendMessage("Category: " + listing.category().getDisplayName());
        player.sendMessage("Type:     " + listing.type().getDisplayName());
        player.sendMessage("Price:    " + listing.startingBid() + " coins");
        if (listing.type() == AuctionHouseManager.AuctionType.AUCTION) {
            player.sendMessage("Highest bid: " + highestBid + " coins"
                    + (highestBidder != null ? " (by " + highestBidder + ")" : " (no bids yet)"));
        }
    }

    private void handleCancel(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /ah cancel <id>");
            return;
        }
        UUID listingId = resolveId(player, args[1]);
        if (listingId == null) return;
        try {
            AuctionHouseManager.getInstance().cancelListing(listingId, player.getUniqueId());
            player.sendMessage("Listing cancelled.");
        } catch (IllegalArgumentException e) {
            player.sendMessage("Could not cancel: " + e.getMessage());
        }
    }

    private void handleMine(Player player) {
        AuctionHouseManager manager = AuctionHouseManager.getInstance();
        List<AuctionHouseManager.AuctionListing> mine = manager.getActiveListings().stream()
                .map(manager::getListing)
                .filter(l -> player.getUniqueId().equals(l.seller()))
                .collect(Collectors.toList());
        if (mine.isEmpty()) {
            player.sendMessage("You have no active auction house listings.");
            return;
        }
        player.sendMessage("=== Your Listings ===");
        mine.forEach(l -> player.sendMessage(String.format(
                "[%s] %s — %.1f coins (%s)",
                l.id().toString().substring(0, 8),
                l.itemName(),
                l.startingBid(),
                l.type().name())));
    }

    private void handleHistory(Player player) {
        List<String> history = AuctionHouseManager.getInstance().getAuctionHistory(player.getUniqueId());
        if (history.isEmpty()) {
            player.sendMessage("You have no auction house history.");
            return;
        }
        player.sendMessage("=== Auction House History ===");
        for (String entry : history) {
            player.sendMessage(entry);
        }
    }

    private AuctionHouseManager.AuctionCategory parseCategory(Player player, String input) {
        try {
            return AuctionHouseManager.AuctionCategory.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown category: " + input
                    + ". Valid categories: " + String.join(", ", CATEGORY_NAMES));
            return null;
        }
    }

    private UUID resolveId(Player player, String input) {
        for (UUID id : AuctionHouseManager.getInstance().getActiveListings()) {
            if (id.toString().startsWith(input) || id.toString().equals(input)) {
                return id;
            }
        }
        player.sendMessage("No active listing matches id: " + input);
        return null;
    }
}
