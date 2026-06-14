package com.skyblock.plugin.command.auctionhouse;

import com.skyblock.core.auctionhouse.AuctionHouseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class AuctionHouseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        AuctionHouseManager manager = AuctionHouseManager.getInstance();

        List<AuctionHouseManager.AuctionListing> myListings = manager.getListingsBySeller(id);
        player.sendMessage("=== Your Auction House Listings ===");
        if (myListings.isEmpty()) {
            player.sendMessage("  No active listings.");
        } else {
            for (AuctionHouseManager.AuctionListing listing : myListings) {
                double highestBid = manager.getHighestBid(listing.id());
                player.sendMessage("  " + listing.itemName() + " [" + listing.type().getDisplayName() + "] — Bid: " + (long) highestBid + " coins");
            }
        }
        return true;
    }
}
