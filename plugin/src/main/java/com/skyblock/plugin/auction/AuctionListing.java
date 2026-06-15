package com.skyblock.plugin.auction;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

/**
 * A single active Buy-It-Now auction house listing backed by a real {@link ItemStack}.
 *
 * @param id     the listing's unique string id
 * @param seller the selling player's UUID
 * @param item   a snapshot of the item being sold
 * @param price  the buy-it-now price in coins
 */
public record AuctionListing(String id, UUID seller, ItemStack item, double price) {
    public AuctionListing {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(seller, "seller");
        Objects.requireNonNull(item, "item");
    }
}
