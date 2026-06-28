package com.skyblock.core.menu;

import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.BazaarManager.BazaarOrder;
import com.skyblock.core.manager.BazaarManager.BazaarProduct;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * "Manage Orders" — lists the player's resting Bazaar buy/sell orders across every
 * product, lets them cancel one (the escrow is refunded to their claims), and
 * collect claimable coins (filled sell orders) and items (filled buy orders).
 */
public final class BazaarOrdersMenu extends AbstractSkyBlockMenu {

    private static final int[] ORDER_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    /** One of the player's resting orders, with the product it belongs to. */
    private record OwnedOrder(BazaarProduct product, BazaarOrder order, boolean isBuy) {}

    public BazaarOrdersMenu(Player player) {
        super(player, "§6Bazaar ➜ §fOrders", 6);
    }

    @Override
    protected void populate() {
        BazaarManager mgr = BazaarManager.getInstance();
        UUID id = player.getUniqueId();
        List<OwnedOrder> orders = collectOrders(mgr, id);

        setItem(4, new ItemBuilder(Material.BOOK)
                .displayName("§aYour Orders")
                .lore("§7Active orders: §e" + orders.size(),
                      "",
                      "§7Filled sell orders pay §6coins§7,",
                      "§7filled buy orders deliver §eitems§7.",
                      "§7Collect them below.")
                .build(), e -> e.setCancelled(true));

        for (int i = 0; i < orders.size() && i < ORDER_SLOTS.length; i++) {
            OwnedOrder owned = orders.get(i);
            BazaarOrder o = owned.order();
            String kind = owned.isBuy() ? "§aBuy Order" : "§6Sell Order";
            setItem(ORDER_SLOTS[i], ItemBuilder.forItem(owned.product().getItemId())
                    .displayName(kind + " §7— §f" + owned.product().getDisplayName())
                    .lore("§7Amount: §e" + o.quantity(),
                          "§7Price each: §6" + fmt(o.priceEach()) + " coins",
                          "§7Total: §6" + fmt(o.quantity() * o.priceEach()) + " coins",
                          "",
                          "§cClick to cancel §7(escrow refunded to claims)")
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        if (mgr.cancelOrder(id, owned.isBuy(), o.id())) {
                            player.sendMessage("§aOrder cancelled — the escrow is in your claims.");
                        }
                        new BazaarOrdersMenu(player).open(player);
                    });
        }

        if (orders.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Active Orders")
                    .lore("§7Create buy/sell orders from a", "§7product's page in the Bazaar.")
                    .build(), e -> e.setCancelled(true));
        }

        // Claim coins (filled sell orders).
        double coins = mgr.getClaimableCoins(id);
        if (coins > 0) {
            setItem(47, new ItemBuilder(Material.GOLD_NUGGET)
                    .displayName("§6Claim Coins")
                    .lore("§7You have §6" + fmt(coins) + " coins §7to collect.", "", "§eClick to claim!")
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        double claimed = mgr.claimCoins(id);
                        if (claimed > 0) {
                            EconomyManager.getInstance().addCoins(id, claimed);
                            player.sendMessage("§aCollected §6" + fmt(claimed) + " coins§a.");
                        }
                        new BazaarOrdersMenu(player).open(player);
                    });
        } else {
            setItem(47, new ItemBuilder(Material.GRAY_DYE).displayName("§7No coins to claim").build(),
                    e -> e.setCancelled(true));
        }

        // Claim items (filled buy orders), across every product.
        int claimableItems = totalClaimableItems(mgr, id);
        if (claimableItems > 0) {
            setItem(51, new ItemBuilder(Material.CHEST)
                    .displayName("§aClaim Items")
                    .lore("§7You have §e" + claimableItems + " item(s) §7to collect.", "", "§eClick to claim!")
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        claimAllItems(mgr, id);
                        new BazaarOrdersMenu(player).open(player);
                    });
        } else {
            setItem(51, new ItemBuilder(Material.GRAY_DYE).displayName("§7No items to claim").build(),
                    e -> e.setCancelled(true));
        }

        setItem(49, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To the Bazaar")
                .build(),
                e -> { e.setCancelled(true); new BazaarMenu(player).open(player); });
    }

    private static List<OwnedOrder> collectOrders(BazaarManager mgr, UUID id) {
        List<OwnedOrder> result = new ArrayList<>();
        for (BazaarProduct product : BazaarProduct.values()) {
            for (BazaarOrder o : mgr.getBuyOrders(product.getItemId())) {
                if (o.owner().equals(id)) {
                    result.add(new OwnedOrder(product, o, true));
                }
            }
            for (BazaarOrder o : mgr.getSellOrders(product.getItemId())) {
                if (o.owner().equals(id)) {
                    result.add(new OwnedOrder(product, o, false));
                }
            }
        }
        return result;
    }

    private static int totalClaimableItems(BazaarManager mgr, UUID id) {
        int total = 0;
        for (BazaarProduct product : BazaarProduct.values()) {
            total += mgr.getClaimableItems(id, product.getItemId());
        }
        return total;
    }

    private void claimAllItems(BazaarManager mgr, UUID id) {
        int delivered = 0;
        for (BazaarProduct product : BazaarProduct.values()) {
            int qty = mgr.getClaimableItems(id, product.getItemId());
            if (qty <= 0) {
                continue;
            }
            mgr.claimItems(id, product.getItemId());
            int remaining = qty;
            while (remaining > 0) {
                int stack = Math.min(remaining, 64);
                ItemStack item = ItemBuilder.forItem(product.getItemId()).build();
                item.setAmount(stack);
                for (ItemStack overflow : player.getInventory().addItem(item).values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), overflow);
                }
                remaining -= stack;
            }
            delivered += qty;
        }
        if (delivered > 0) {
            player.sendMessage("§aCollected §e" + delivered + " §aitem(s) from filled orders.");
        }
    }

    private static String fmt(double coins) {
        if (coins == Math.floor(coins)) {
            return String.format("%,d", (long) coins);
        }
        return String.format("%,.1f", coins);
    }
}
