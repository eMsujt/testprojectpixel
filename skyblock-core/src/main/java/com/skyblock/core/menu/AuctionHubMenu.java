package com.skyblock.core.menu;

import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.manager.AuctionHouseManager.AuctionCategory;
import com.skyblock.core.manager.AuctionHouseManager.AuctionType;
import com.skyblock.core.manager.ChatInputManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The Auction House hub, laid out 1:1 with the wiki {@code Auction House/UI}: a
 * 4-row menu whose four buttons are Auctions Browser (slot 11), Manage Bids (13),
 * Create Auction (15) and Auction Stats (32), with a Close at slot 31. Opening the
 * Auction House (command or Auction Master NPC) lands here; "Auctions Browser"
 * opens the category {@link AuctionHouseMenu}, and "Create Auction" lists the item
 * in the player's hand as a Buy-It-Now auction.
 */
public final class AuctionHubMenu extends AbstractSkyBlockMenu {

    public AuctionHubMenu(Player player) {
        super(player, "Auction House", 4);
    }

    @Override
    protected void populate() {
        AuctionHouseManager mgr = AuctionHouseManager.getInstance();
        UUID id = player.getUniqueId();

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
                .lore("§7View and manage your bids,",
                      "§7auctions and claims.",
                      "",
                      "§eClick to view!")
                .build(),
                e -> { e.setCancelled(true); new AuctionClaimMenu(player).open(player); });

        setItem(15, new ItemBuilder(Material.GOLDEN_HORSE_ARMOR)
                .displayName("§aCreate Auction")
                .lore("§7Set your own items on auction for",
                      "§7other players to purchase.",
                      "",
                      "§7Hold the item you want to sell,",
                      "§7then click here.",
                      "",
                      "§eClick to become rich!")
                .build(),
                e -> { e.setCancelled(true); createAuction(); });

        int pendingItems = mgr.getPendingItems(id).size();
        setItem(32, new ItemBuilder(Material.MAP)
                .displayName("§aAuction Stats")
                .lore("§7View various statistics about",
                      "§7you and the Auction House.",
                      "",
                      "§7Your active auctions: §e" + mgr.getAuctionCount(id),
                      "§7Coins to claim: §6" + String.format("%,d", (long) mgr.getPendingCoins(id)),
                      "§7Items to claim: §e" + pendingItems,
                      "§7Total live listings: §e" + mgr.getActiveListings().size())
                .build(), e -> e.setCancelled(true));

        setItem(31, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(),
                e -> { e.setCancelled(true); player.closeInventory(); });
    }

    /** Lists the item in the player's hand as a Buy-It-Now auction after a chat price prompt. */
    private void createAuction() {
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held == null || held.getType().isAir()) {
            player.closeInventory();
            player.sendMessage("§cHold the item you want to sell in your hand, then click Create Auction.");
            return;
        }
        ItemStack snapshot = held.clone();
        player.closeInventory();
        player.sendMessage("§eEnter the §6Buy It Now price §efor §f" + nameOf(snapshot) + " §e(or §ccancel§e):");
        ChatInputManager.getInstance().request(player.getUniqueId(), priceRaw -> {
            if (priceRaw.equalsIgnoreCase("cancel")) {
                new AuctionHubMenu(player).open(player);
                return;
            }
            long price = ChatInputManager.parseAmount(priceRaw);
            if (price <= 0) {
                player.sendMessage("§cInvalid price.");
                new AuctionHubMenu(player).open(player);
                return;
            }
            ItemStack current = player.getInventory().getItemInMainHand();
            if (current == null || current.getType().isAir() || !current.isSimilar(snapshot)) {
                player.sendMessage("§cYou're no longer holding that item — auction cancelled.");
                new AuctionHubMenu(player).open(player);
                return;
            }
            AuctionHouseManager mgr = AuctionHouseManager.getInstance();
            mgr.createListing(player.getUniqueId(), current.clone(), nameOf(current),
                    categorize(current), price, AuctionType.BIN);
            player.getInventory().setItemInMainHand(null); // escrow the listed item
            player.sendMessage("§aListed §f" + nameOf(current) + " §afor §6" + String.format("%,d", price)
                    + " coins §a(Buy It Now). Collect proceeds from Manage Bids.");
            new AuctionHubMenu(player).open(player);
        });
    }

    private static String nameOf(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        String n = item.getType().name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(n.charAt(0)) + n.substring(1);
    }

    /** Best-effort auto-categorisation of a listed item for the browser tabs. */
    private static AuctionCategory categorize(ItemStack item) {
        String n = item.getType().name();
        if (n.endsWith("_SWORD") || n.equals("BOW") || n.equals("CROSSBOW") || n.equals("TRIDENT")
                || n.endsWith("_AXE")) {
            return AuctionCategory.WEAPONS;
        }
        if (n.endsWith("_HELMET") || n.endsWith("_CHESTPLATE") || n.endsWith("_LEGGINGS")
                || n.endsWith("_BOOTS")) {
            return AuctionCategory.ARMOR;
        }
        if (item.getType().isEdible() || n.endsWith("POTION") || n.equals("ENCHANTED_BOOK")) {
            return AuctionCategory.CONSUMABLES;
        }
        if (item.getType().isBlock()) {
            return AuctionCategory.BLOCKS;
        }
        return AuctionCategory.MISC;
    }
}
