package com.skyblock.core.menu;

import com.skyblock.core.manager.AuctionManager;
import com.skyblock.core.manager.AuctionManager.Listing;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Buy-it-now confirmation prompt shown when a player left-clicks a listing in
 * the {@link AuctionHouseMenu}. The listed item sits in the centre; a green
 * confirm button (slot 11) completes the purchase via
 * {@link AuctionManager#purchase(java.util.UUID, java.util.UUID)} and a red
 * cancel button (slot 15) returns to the auction house.
 */
public final class AuctionConfirmMenu extends AbstractSkyBlockMenu {

    static final int ITEM_SLOT = 13;
    static final int CONFIRM_SLOT = 11;
    static final int CANCEL_SLOT = 15;

    private final UUID listingId;

    public AuctionConfirmMenu(Player player, UUID listingId) {
        super(player, "§6§lConfirm Purchase", 3);
        this.listingId = listingId;
    }

    @Override
    protected void populate() {
        AuctionManager manager = AuctionManager.getInstance();
        if (!manager.isActive(listingId)) {
            new AuctionHouseMenu(player).open(player);
            return;
        }
        Listing listing = manager.getListing(listingId);

        setItem(ITEM_SLOT, new ItemBuilder(listing.item())
                .displayName("§e" + listing.itemName())
                .lore(
                        "§7BIN: §6" + (long) listing.price() + " coins",
                        "§7Category: §f" + listing.category())
                .build());

        setItem(CONFIRM_SLOT, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .displayName("§aConfirm Purchase")
                .lore("§7Buy for §6" + (long) listing.price() + " coins§7.")
                .build(),
                event -> {
                    event.setCancelled(true);
                    try {
                        manager.purchase(listingId, player.getUniqueId());
                        player.sendMessage("§aYou purchased §e" + listing.itemName()
                                + " §afor §6" + (long) listing.price() + " coins§a!");
                    } catch (IllegalArgumentException ex) {
                        player.sendMessage("§cUnable to purchase: " + ex.getMessage());
                    }
                    new AuctionHouseMenu(player).open(player);
                });

        setItem(CANCEL_SLOT, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .displayName("§cCancel")
                .lore("§7Return to the auction house.")
                .build(),
                event -> {
                    event.setCancelled(true);
                    new AuctionHouseMenu(player).open(player);
                });
    }
}
