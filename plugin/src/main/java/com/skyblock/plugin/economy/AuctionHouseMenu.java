package com.skyblock.plugin.economy;

import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.economy.AuctionHouseManager.AuctionListing;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * 54-slot (6-row) Buy-It-Now auction house menu.
 *
 * <p>Renders every active {@link AuctionListing} from the {@link
 * AuctionHouseManager} as an item stack, laid out left-to-right starting at
 * slot 0 (capped at the 54 available slots), each showing its BIN price as a
 * lore line. Clicking a listing withdraws its price from the clicking player's
 * purse (via {@link CoinManager}) and removes the listing; if they can't afford
 * it, the purchase is rejected with a message.</p>
 */
public class AuctionHouseMenu extends Menu {

    private final AuctionHouseManager auctionHouse;
    private final CoinManager coinManager;

    /**
     * Creates an auction house menu using the shared manager instances.
     */
    public AuctionHouseMenu() {
        this(AuctionHouseManager.getInstance(), CoinManager.getInstance());
    }

    /**
     * Creates an auction house menu backed by the given managers.
     *
     * @param auctionHouse the source of active listings
     * @param coinManager  the coin source charged on purchase
     */
    public AuctionHouseMenu(AuctionHouseManager auctionHouse, CoinManager coinManager) {
        super("§6Auction House", 6);
        this.auctionHouse = Objects.requireNonNull(auctionHouse, "auctionHouse");
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
    }

    @Override
    protected void build() {
        List<AuctionListing> listings = List.copyOf(auctionHouse.getListings());
        for (int i = 0; i < listings.size() && i < 54; i++) {
            AuctionListing listing = listings.get(i);
            ItemStack display = new ItemBuilder(iconFor(listing.itemName()))
                    .displayName("§6" + listing.itemName())
                    .addLore("§7Buy It Now: §6" + (long) listing.price() + " coins")
                    .addLore("§eClick to buy!")
                    .build();
            setItem(i, display, event -> purchase((Player) event.getWhoClicked(), listing));
        }
    }

    private void purchase(Player player, AuctionListing listing) {
        if (auctionHouse.getListing(listing.id()) == null) {
            player.sendMessage("§cThat auction is no longer available!");
            return;
        }
        if (coinManager.withdraw(player.getUniqueId(), (long) listing.price())) {
            auctionHouse.removeListing(listing.id());
            player.sendMessage("§aBought §6" + listing.itemName() + " §afor §6" + (long) listing.price() + " coins§a!");
            open(player);
        } else {
            player.sendMessage("§cYou don't have enough coins!");
        }
    }

    /** Resolves the listing's item name to a {@link Material}, defaulting to paper. */
    private static Material iconFor(String itemName) {
        Material material = Material.matchMaterial(itemName);
        return material != null ? material : Material.PAPER;
    }
}
