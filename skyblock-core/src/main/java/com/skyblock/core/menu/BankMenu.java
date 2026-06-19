package com.skyblock.core.menu;

import com.skyblock.core.coop.CoopManager;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.SkyblockUtil.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Canonical 27-slot (3-row) Bank menu with Personal and Co-op tabs.
 *
 * <p>Gray-pane border on all four edges; the two tab buttons (Personal at slot
 * 10, Co-op at slot 11) toggle which account the menu acts on. The active
 * account's balance is shown at slot 13 alongside the purse at slot 12;
 * Deposit All (EMERALD) at slot 15 and Withdraw All (DROPPER) at slot 16
 * move coins between the purse and the active account. Close barrier at
 * slot 22.</p>
 *
 * <p>The Co-op tab keys its balance off the player's island owner via
 * {@link CoopManager}; players not in a co-op see a zero balance and cannot
 * deposit or withdraw on that tab.</p>
 */
public final class BankMenu extends Menu {

    private static final int PERSONAL_TAB_SLOT = 10;
    private static final int COOP_TAB_SLOT      = 11;
    private static final int PURSE_SLOT         = 12;
    private static final int BALANCE_SLOT       = 13;
    private static final int DEPOSIT_SLOT       = 15;
    private static final int WITHDRAW_SLOT      = 16;
    private static final int CLOSE_SLOT         = 22;

    private final UUID playerId;
    private Inventory inventory;
    private boolean showingCoop;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public BankMenu(Player player) {
        this(player.getUniqueId());
    }

    public BankMenu(UUID playerId) {
        super("§6Bank Account", 3);
        this.playerId = playerId;
    }

    /** Unused: this menu manages its own inventory via {@link #open(Player)}. */
    @Override
    protected void build() {
    }

    @Override
    public void open(Player player) {
        handlers.clear();

        EconomyManager econ = EconomyManager.getInstance();
        BankManager bank = BankManager.getInstance();
        CoopManager coop = CoopManager.getInstance();

        inventory = org.bukkit.Bukkit.createInventory(this, 27, "§6Bank Account");

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 27; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 18 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        UUID coopOwner = coop.getOwner(playerId);
        String coopKey = coopOwner != null ? coopOwner.toString() : null;

        // Tabs.
        inventory.setItem(PERSONAL_TAB_SLOT, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName((showingCoop ? "§7" : "§6") + "Personal Bank")
                .lore(showingCoop ? "§7Click to view." : "§eViewing.")
                .build());
        handlers.put(PERSONAL_TAB_SLOT, e -> {
            if (showingCoop) {
                showingCoop = false;
                open(player);
            }
        });

        inventory.setItem(COOP_TAB_SLOT, new ItemBuilder(Material.EMERALD_BLOCK)
                .displayName((showingCoop ? "§6" : "§7") + "Co-op Bank")
                .lore(showingCoop ? "§eViewing." : "§7Click to view.")
                .build());
        handlers.put(COOP_TAB_SLOT, e -> {
            if (!showingCoop) {
                showingCoop = true;
                open(player);
            }
        });

        long purse = econ.getPurse(playerId);
        double balance = showingCoop
                ? (coopKey != null ? bank.getCoopBalance(coopKey) : 0.0)
                : bank.getBalance(playerId);

        inventory.setItem(PURSE_SLOT, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Purse")
                .lore("§7Balance: §6" + String.format("%,.0f", (double) purse) + " Coins")
                .build());

        inventory.setItem(BALANCE_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .displayName(showingCoop ? "§6Co-op Bank" : "§6Personal Bank")
                .lore("§7Balance: §6" + String.format("%,.0f", balance) + " Coins")
                .build());

        inventory.setItem(DEPOSIT_SLOT, new ItemBuilder(Material.EMERALD)
                .displayName("§aDeposit All")
                .lore("§7Move all purse coins into the bank.")
                .build());
        handlers.put(DEPOSIT_SLOT, e -> {
            if (showingCoop && coopKey == null) {
                player.sendMessage("§cYou are not in a co-op.");
                return;
            }
            long p = econ.getPurse(playerId);
            if (p > 0) {
                econ.withdraw(playerId, p);
                if (showingCoop) {
                    bank.depositCoop(coopKey, p);
                } else {
                    bank.deposit(playerId, p);
                }
                player.sendMessage("§aDeposited §6" + String.format("%,.0f", (double) p) + " §acoins into your bank.");
                open(player);
            }
        });

        inventory.setItem(WITHDRAW_SLOT, new ItemBuilder(Material.DROPPER)
                .displayName("§eWithdraw All")
                .lore("§7Move all bank coins to your purse.")
                .build());
        handlers.put(WITHDRAW_SLOT, e -> {
            if (showingCoop && coopKey == null) {
                player.sendMessage("§cYou are not in a co-op.");
                return;
            }
            double b = showingCoop ? bank.getCoopBalance(coopKey) : bank.getBalance(playerId);
            if (b > 0) {
                if (showingCoop) {
                    bank.withdrawCoop(coopKey, b);
                } else {
                    bank.withdraw(playerId, b);
                }
                econ.addPurse(playerId, (long) b);
                player.sendMessage("§aWithdrew §6" + String.format("%,.0f", b) + " §acoins from your bank.");
                open(player);
            }
        });

        inventory.setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the bank.")
                .build());
        handlers.put(CLOSE_SLOT, e -> player.closeInventory());

        player.openInventory(inventory);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Consumer<InventoryClickEvent> handler = handlers.get(event.getSlot());
        if (handler != null) {
            handler.accept(event);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
