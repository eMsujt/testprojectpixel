package com.skyblock.core.menu;

import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The Auction House hub, laid out 1:1 with the wiki {@code Auction House/UI}: a
 * 4-row menu whose four buttons are Auctions Browser (slot 11), Manage Bids (13),
 * Create Auction (15) and Auction Stats (32), with a Close at slot 31. Opening the
 * Auction House (command or Auction Master NPC) lands here; "Auctions Browser"
 * opens the category {@link AuctionHouseMenu}.
 */
public final class AuctionHubMenu extends AbstractSkyBlockMenu {

    public AuctionHubMenu(Player player) {
        super(player, "Auction House", 4);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(" ").build();
        for (int slot = 0; slot < 36; slot++) {
            setItem(slot, pane);
        }

        setItem(11, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Auctions Browser")
                .lore("§7Find items for sale by players",
                      "§7across Hypixel SkyBlock! Items",
                      "§7offered here are for auction, meaning",
                      "§7you have to place the top bid to",
                      "§7acquire them!",
                      "",
                      "§eClick to browse!")
                .build(),
                e -> { e.setCancelled(true); new AuctionHouseMenu(player).open(player); });

        setItem(13, new ItemBuilder(Material.GOLDEN_CARROT)
                .displayName("§aManage Bids")
                .lore("§7View and manage your bids.",
                      "",
                      "§eClick to view!")
                .build(),
                e -> { e.setCancelled(true); new AuctionClaimMenu(player).open(player); });

        setItem(15, new ItemBuilder(Material.GOLDEN_HORSE_ARMOR)
                .displayName("§aCreate Auction")
                .lore("§7Set your own items on auction for",
                      "§7other players to purchase.",
                      "",
                      "§eClick to become rich!")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                    player.sendMessage("§eHold the item you want to sell and run §6/ah §eto manage your auctions.");
                });

        setItem(32, new ItemBuilder(Material.MAP)
                .displayName("§aAuction Stats")
                .lore("§7View various statistics about",
                      "§7you and the Auction House.",
                      "",
                      "§7Increase your bid limit by achieving",
                      "§7a higher SkyBlock Level!",
                      "",
                      "§eClick to view!")
                .build(), e -> e.setCancelled(true));

        setItem(31, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(),
                e -> { e.setCancelled(true); player.closeInventory(); });
    }
}
