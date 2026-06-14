package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.AuctionManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AuctionHouseMenu extends Menu {

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43};

    public AuctionHouseMenu() {
        super("§6Auction House", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        List<AuctionManager.Auction> auctions = AuctionManager.getInstance().getAuctions();
        for (int i = 0; i < auctions.size() && i < SLOTS.length; i++) {
            AuctionManager.Auction auction = auctions.get(i);
            setItem(SLOTS[i], new ItemBuilder(Material.PAPER)
                    .displayName("§a" + auction.itemName())
                    .lore(
                            "§7Price: §6" + auction.price() + " coins",
                            "§7Click to view")
                    .build());
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
