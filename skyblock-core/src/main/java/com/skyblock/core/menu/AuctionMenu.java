package com.skyblock.core.menu;

import com.skyblock.core.manager.AuctionManager;
import com.skyblock.core.manager.AuctionManager.Listing;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 6-row GUI '§6Auction House' backed by {@link AuctionManager}. Slots 0–44
 * display live listings; the bottom row (45–53) holds paged navigation.
 */
public final class AuctionMenu extends AbstractSkyBlockMenu {

    private static final int PAGE_SIZE = 45;

    private final int page;

    public AuctionMenu(Player player) {
        this(player, 0);
    }

    public AuctionMenu(Player player, int page) {
        super(player, "§6Auction House", 6);
        this.page = Math.max(0, page);
    }

    @Override
    protected void populate() {
        List<Listing> listings = AuctionManager.getInstance().getListings();

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, listings.size());

        for (int i = start; i < end; i++) {
            Listing listing = listings.get(i);
            int slot = i - start;
            ItemStack icon = new ItemBuilder(listing.item())
                    .displayName("§e" + listing.itemName())
                    .lore(
                            "§7BIN: §6" + (long) listing.price() + " coins",
                            "§7Category: §f" + listing.category(),
                            "§eClick to purchase!")
                    .build();
            setItem(slot, icon, event -> {
                event.setCancelled(true);
                if (event.isLeftClick()) {
                    new AuctionConfirmMenu(player, listing.id()).open(player);
                }
            });
        }

        if (listings.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Auctions Available")
                    .lore("§7There are no active listings right now.")
                    .build());
        }

        ItemStack pane = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }

        if (page > 0) {
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Page " + page)
                    .build(),
                    event -> {
                        event.setCancelled(true);
                        new AuctionMenu(player, page - 1).open(player);
                    });
        }

        if (end < listings.size()) {
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Page " + (page + 2))
                    .build(),
                    event -> {
                        event.setCancelled(true);
                        new AuctionMenu(player, page + 1).open(player);
                    });
        }
    }
}
