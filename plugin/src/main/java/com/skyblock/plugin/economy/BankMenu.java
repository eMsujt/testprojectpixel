package com.skyblock.plugin.economy;

import com.skyblock.economy.CoinManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The Bank Account menu.
 *
 * <p>A 54-slot (6-row) chest GUI showing the player's bank and purse balances.
 * The bank balance sits in slot 11 ({@link Material#GOLD_BLOCK}); the purse
 * summary mirrors it in slot 15, with deposit and withdraw icons below
 * transferring coins between purse and bank via {@link BankManager}. The menu is
 * its own {@link InventoryHolder} and {@link Listener}, cancelling clicks on its
 * own inventory and dispatching the deposit/withdraw actions.</p>
 */
public final class BankMenu implements InventoryHolder, Listener {

    /** Slot holding the bank balance summary. */
    private static final int BANK_SLOT = 11;
    /** Slot holding the purse summary. */
    private static final int PURSE_SLOT = 15;
    /** Slot for the deposit action. */
    private static final int DEPOSIT_SLOT = 29;
    /** Slot for the withdraw action. */
    private static final int WITHDRAW_SLOT = 33;
    /** Default amount moved per click. */
    private static final long STEP = 1000L;

    private final Player player;
    private final CoinManager coinManager;
    private final BankManager bankManager;
    private final Inventory inventory;

    /**
     * Creates a bank menu for the given player using the shared managers.
     *
     * @param player the player whose balances are shown
     */
    public BankMenu(Player player) {
        this(player, CoinManager.getInstance(), BankManager.getInstance());
    }

    /**
     * Creates a bank menu backed by the given managers.
     *
     * @param player      the player whose balances are shown
     * @param coinManager the source of purse and bank balances
     * @param bankManager the manager moving coins between them
     */
    public BankMenu(Player player, CoinManager coinManager, BankManager bankManager) {
        this.player = player;
        this.coinManager = coinManager;
        this.bankManager = bankManager;
        this.inventory = Bukkit.createInventory(this, 54, "§6Bank");
        build();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build() {
        fillBorder();

        UUID playerId = player.getUniqueId();

        inventory.setItem(BANK_SLOT, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Bank Account")
                .lore("§7Coins in the bank:", "§6" + coinManager.getBank(playerId) + " coins")
                .build());

        inventory.setItem(PURSE_SLOT, new ItemBuilder(Material.SUNFLOWER)
                .displayName("§6Purse")
                .lore("§7Coins on hand:", "§6" + coinManager.getPurse(playerId) + " coins")
                .build());

        inventory.setItem(DEPOSIT_SLOT, new ItemBuilder(Material.HOPPER)
                .displayName("§aDeposit")
                .lore("§7Click to deposit §6" + STEP + " §7coins.")
                .build());

        inventory.setItem(WITHDRAW_SLOT, new ItemBuilder(Material.DROPPER)
                .displayName("§cWithdraw")
                .lore("§7Click to withdraw §6" + STEP + " §7coins.")
                .build());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BankMenu)) {
            return;
        }
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot == DEPOSIT_SLOT) {
            bankManager.deposit(player.getUniqueId(), STEP);
            refresh();
        } else if (slot == WITHDRAW_SLOT) {
            bankManager.withdraw(player.getUniqueId(), STEP);
            refresh();
        }
    }

    /** Rebuilds the menu in place so refreshed balances are shown. */
    private void refresh() {
        inventory.clear();
        build();
    }

    /** Fills the menu's outer edge with black glass panes. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                inventory.setItem(slot, pane);
            }
        }
    }
}
