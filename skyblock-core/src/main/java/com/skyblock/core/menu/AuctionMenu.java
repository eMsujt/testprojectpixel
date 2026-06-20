package com.skyblock.core.menu;

import com.skyblock.core.manager.AuctionManager;
import com.skyblock.core.manager.AuctionManager.AuctionListing;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * 6-row chest GUI titled 'Auction House' that fetches active listings from
 * {@link AuctionManager}. A yellow-pane border frames 28 listing slots
 * (10–43); an empty book of auctions is shown when no listings exist.
 */
public final class AuctionMenu extends AbstractSkyBlockMenu {

    static final int[] LISTING_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public AuctionMenu(Player player) {
        super(player, "Auction House", 54);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int row = slot / 9;
            int col = slot % 9;
            if (row == 0 || row == 5 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        List<AuctionListing> listings = AuctionManager.getInstance().getActiveListings();

        if (listings.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Auctions Available")
                    .lore("§7There are no active listings right now.")
                    .build());
            return;
        }

        int count = Math.min(listings.size(), LISTING_SLOTS.length);
        for (int i = 0; i < count; i++) {
            AuctionListing listing = listings.get(i);
            String name = itemDisplayName(listing.item());
            setItem(LISTING_SLOTS[i], new ItemBuilder(listing.item())
                    .displayName("§e" + name)
                    .lore(
                            "§7Price: §6" + (long) listing.startingBid() + " coins",
                            "§eClick to purchase!")
                    .build(),
                    event -> event.getWhoClicked().sendMessage(
                            "§e" + name + " §7is listed for §6" + (long) listing.startingBid() + " coins§7."));
        }
    }

    private static String itemDisplayName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        String name = item.getType().name().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        for (String word : name.split(" ")) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
