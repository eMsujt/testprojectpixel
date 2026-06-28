package com.skyblock.core.menu;

import com.skyblock.core.item.SkyblockItems;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.BazaarManager.BazaarProduct;
import com.skyblock.core.manager.ChatInputManager;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.UUID;

/**
 * Per-product Bazaar view: instant-buy and instant-sell a SkyBlock material for coins.
 * Prices come from {@link BazaarManager} (live order book, falling back to the base price),
 * coins are moved through {@link EconomyManager}, and items are added to / removed from the
 * player's inventory. Reached by clicking a product in {@link BazaarMenu}.
 */
public final class BazaarProductMenu extends AbstractSkyBlockMenu {

    private final BazaarProduct product;

    public BazaarProductMenu(Player player, BazaarProduct product) {
        super(player, "§6Bazaar ➜ §f" + product.getDisplayName(), 6);
        this.product = product;
    }

    @Override
    protected void populate() {
        BazaarManager mgr = BazaarManager.getInstance();
        double buy = mgr.getDisplayBuyPrice(product);
        double sell = mgr.getDisplaySellPrice(product);
        int held = countHeld();
        long coins = EconomyManager.getInstance().getPurse(player.getUniqueId());

        setItem(4, ItemBuilder.forItem(product.getItemId())
                .displayName("§f" + product.getDisplayName())
                .lore(
                        "§7Buy price:  §6" + fmt(buy) + " coins each",
                        "§7Sell price: §6" + fmt(sell) + " coins each",
                        "",
                        "§7You have: §e" + held + " §7in your inventory",
                        "§7Your purse: §6" + String.format("%,d", coins) + " coins")
                .build(), e -> e.setCancelled(true));

        // Instant Buy (1 / 16 / 64).
        buyButton(19, 1, buy);
        buyButton(20, 16, buy);
        buyButton(21, 64, buy);

        // Instant Sell (1 / 64 / all of what you hold).
        sellButton(23, 1, sell);
        sellButton(24, 64, sell);
        sellButton(25, held, sell, "§6Sell All §7(" + held + ")");

        // Create Buy Order — choose your own quantity and price (escrows coins).
        setItem(29, new ItemBuilder(Material.BOOK)
                .displayName("§aCreate Buy Order")
                .lore("§7Set up a buy order at a price",
                      "§7you choose. It fills as sellers",
                      "§7trade into it.",
                      "",
                      "§7Top buy order: §6" + fmt(mgr.getHighestBid(product.getItemId())) + " coins",
                      "",
                      "§eClick to create!")
                .build(),
                e -> { e.setCancelled(true); createBuyOrder(); });

        // Create Sell Offer — list items you hold at a price you choose.
        setItem(33, new ItemBuilder(Material.WRITABLE_BOOK)
                .displayName("§6Create Sell Offer")
                .lore("§7List items you own for sale at",
                      "§7a price you choose. It fills as",
                      "§7buyers trade into it.",
                      "",
                      "§7Top sell order: §6" + fmt(mgr.getLowestAsk(product.getItemId())) + " coins",
                      "§7You have: §e" + held,
                      "",
                      "§eClick to create!")
                .build(),
                e -> { e.setCancelled(true); createSellOffer(); });

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To the Bazaar")
                .build(),
                e -> { e.setCancelled(true); new BazaarMenu(player).open(player); });
    }

    /** Prompts for quantity + price, escrows the coins, and places a resting buy order. */
    private void createBuyOrder() {
        player.closeInventory();
        player.sendMessage("§eEnter the §aquantity §eto buy (or type §ccancel§e):");
        ChatInputManager.getInstance().request(player.getUniqueId(), qtyRaw -> {
            if (qtyRaw.equalsIgnoreCase("cancel")) { open(player); return; }
            long qty = ChatInputManager.parseAmount(qtyRaw);
            if (qty <= 0 || qty > 71680) {
                player.sendMessage("§cInvalid quantity.");
                open(player);
                return;
            }
            player.sendMessage("§eEnter the §6price per unit §e(or type §ccancel§e):");
            ChatInputManager.getInstance().request(player.getUniqueId(), priceRaw -> {
                if (priceRaw.equalsIgnoreCase("cancel")) { open(player); return; }
                double price = parsePrice(priceRaw);
                if (price <= 0) {
                    player.sendMessage("§cInvalid price.");
                    open(player);
                    return;
                }
                double total = qty * price;
                if (!EconomyManager.getInstance().withdraw(player.getUniqueId(), total)) {
                    player.sendMessage("§cYou can't afford that (§6" + fmt(total) + " coins§c).");
                    open(player);
                    return;
                }
                BazaarManager.getInstance().addBuyOrder(player.getUniqueId(), product.getItemId(), (int) qty, price);
                player.sendMessage("§aBuy Order set up! §e" + qty + "x " + product.getDisplayName()
                        + " §7at §6" + fmt(price) + " coins §7each. Items land in your claims.");
                open(player);
            });
        });
    }

    /** Prompts for quantity + price, escrows the items, and places a resting sell order. */
    private void createSellOffer() {
        int held = countHeld();
        if (held <= 0) {
            player.sendMessage("§cYou have no " + product.getDisplayName() + " to sell.");
            return;
        }
        player.closeInventory();
        player.sendMessage("§eEnter the §aquantity §eto sell (you have §e" + held + "§e, or §ccancel§e):");
        ChatInputManager.getInstance().request(player.getUniqueId(), qtyRaw -> {
            if (qtyRaw.equalsIgnoreCase("cancel")) { open(player); return; }
            long qty = ChatInputManager.parseAmount(qtyRaw);
            int have = countHeld();
            if (qty <= 0 || qty > have) {
                player.sendMessage("§cInvalid quantity (you have " + have + ").");
                open(player);
                return;
            }
            player.sendMessage("§eEnter the §6price per unit §e(or type §ccancel§e):");
            ChatInputManager.getInstance().request(player.getUniqueId(), priceRaw -> {
                if (priceRaw.equalsIgnoreCase("cancel")) { open(player); return; }
                double price = parsePrice(priceRaw);
                if (price <= 0) {
                    player.sendMessage("§cInvalid price.");
                    open(player);
                    return;
                }
                int stillHave = countHeld();
                int sellQty = (int) Math.min(qty, stillHave);
                if (sellQty <= 0) {
                    player.sendMessage("§cYou no longer have any to sell.");
                    open(player);
                    return;
                }
                removeItems(sellQty);
                BazaarManager.getInstance().addSellOrder(player.getUniqueId(), product.getItemId(), sellQty, price);
                player.sendMessage("§aSell Offer set up! §e" + sellQty + "x " + product.getDisplayName()
                        + " §7at §6" + fmt(price) + " coins §7each. Coins land in your claims.");
                open(player);
            });
        });
    }

    /** Parses a per-unit price like "12", "12.5", "1k" into coins, or -1 if invalid. */
    private static double parsePrice(String raw) {
        long parsed = ChatInputManager.parseAmount(raw);
        if (parsed > 0) {
            return parsed;
        }
        try {
            double v = Double.parseDouble(raw.trim().replace(",", ""));
            return v > 0 ? v : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void buyButton(int slot, int amount, double price) {
        double cost = price * amount;
        setItem(slot, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§aBuy " + amount)
                .lore("§7Cost: §6" + fmt(cost) + " coins", "", "§eClick to instant-buy!")
                .build(),
                e -> { e.setCancelled(true); instantBuy(amount, price); });
    }

    private void sellButton(int slot, int amount, double price) {
        sellButton(slot, amount, price, "§6Sell " + amount);
    }

    private void sellButton(int slot, int amount, double price, String name) {
        double credit = price * amount;
        setItem(slot, new ItemBuilder(Material.HOPPER)
                .displayName(name)
                .lore("§7You receive: §6" + fmt(credit) + " coins", "", "§eClick to instant-sell!")
                .build(),
                e -> { e.setCancelled(true); instantSell(amount, price); });
    }

    private void instantBuy(int amount, double price) {
        if (amount <= 0) {
            return;
        }
        UUID id = player.getUniqueId();
        double cost = price * amount;
        if (!EconomyManager.getInstance().withdraw(id, cost)) {
            player.sendMessage("§cYou can't afford that (§6" + fmt(cost) + " coins§c).");
            return;
        }
        ItemStack item = ItemBuilder.forItem(product.getItemId()).build();
        item.setAmount(amount);
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);
        for (ItemStack l : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), l);
        }
        player.sendMessage("§aBought §e" + amount + " " + product.getDisplayName()
                + " §afor §6" + fmt(cost) + " coins§a.");
        open(player);
    }

    private void instantSell(int amount, double price) {
        if (amount <= 0) {
            player.sendMessage("§cYou have none to sell.");
            return;
        }
        int held = countHeld();
        int toSell = Math.min(amount, held);
        if (toSell <= 0) {
            player.sendMessage("§cYou have no " + product.getDisplayName() + " to sell.");
            return;
        }
        removeItems(toSell);
        double credit = price * toSell;
        EconomyManager.getInstance().addCoins(player.getUniqueId(), credit);
        player.sendMessage("§aSold §e" + toSell + " " + product.getDisplayName()
                + " §afor §6" + fmt(credit) + " coins§a.");
        open(player);
    }

    /** True if the inventory item is this product (by SkyBlock id, else by material). */
    private boolean matches(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        String id = SkyblockItems.idOf(item);
        if (id != null) {
            return id.equalsIgnoreCase(product.getItemId());
        }
        return item.getType() == productMaterial();
    }

    private Material productMaterial() {
        return ItemBuilder.forItem(product.getItemId()).build().getType();
    }

    private int countHeld() {
        int total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (matches(item)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    private void removeItems(int amount) {
        int remaining = amount;
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize() && remaining > 0; i++) {
            ItemStack item = inv.getItem(i);
            if (!matches(item)) {
                continue;
            }
            int take = Math.min(remaining, item.getAmount());
            item.setAmount(item.getAmount() - take);
            if (item.getAmount() <= 0) {
                inv.setItem(i, null);
            }
            remaining -= take;
        }
    }

    private static String fmt(double coins) {
        if (coins == Math.floor(coins)) {
            return String.format("%,d", (long) coins);
        }
        return String.format("%,.1f", coins);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        super.handleClick(event);
    }
}
