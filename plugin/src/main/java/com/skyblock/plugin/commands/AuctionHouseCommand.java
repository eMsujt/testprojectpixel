package com.skyblock.plugin.commands;

import com.skyblock.core.auction.AuctionHouseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        int auctionCount = manager.getAuctionCount(id);
        long activeListings = manager.getActiveListings().stream()
                .filter(listingId -> manager.getListing(listingId).seller().equals(id))
                .count();
        player.sendMessage("=== Auction House ===");
        player.sendMessage("Total Auctions Created: " + auctionCount);
        player.sendMessage("Active Listings: " + activeListings);
        return true;
    }
}
