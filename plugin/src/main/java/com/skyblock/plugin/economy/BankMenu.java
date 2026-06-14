package com.skyblock.plugin.economy;

import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The Bank hub menu.
 *
 * <p>A 54-slot (6-row) menu showing the player's current purse and bank
 * balances. The purse sits in slot 13 ({@link Material#SUNFLOWER}); deposit and
 * withdraw icons flank it, transferring coins between purse and bank via
 * {@link BankManager}.</p>
 */
public class BankMenu extends Menu {

    /** Centre slot holding the purse summary. */
    private static final int PURSE_SLOT = 13;
    /** Slot holding the bank balance summary. */
    private static final int BANK_SLOT = 22;
    /** Slot for the deposit action. */
    private static final int DEPOSIT_SLOT = 29;
    /** Slot for the withdraw action. */
    private static final int WITHDRAW_SLOT = 33;
    /** Default amount moved per click. */
    private static final long STEP = 1000L;

    private final Player player;
    private final CoinManager coinManager;
    private final BankManager bankManager;

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
        super("§6Bank", 6);
        this.player = player;
        this.coinManager = coinManager;
        this.bankManager = bankManager;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID playerId = player.getUniqueId();

        setItem(PURSE_SLOT, new ItemBuilder(Material.SUNFLOWER)
                .displayName("§6Purse")
                .lore("§7Coins on hand:", "§6" + coinManager.getPurse(playerId) + " coins")
                .build());

        setItem(BANK_SLOT, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Bank Account")
                .lore("§7Coins in the bank:", "§6" + coinManager.getBank(playerId) + " coins")
                .build());

        setItem(DEPOSIT_SLOT, new ItemBuilder(Material.HOPPER)
                        .displayName("§aDeposit")
                        .lore("§7Click to deposit §6" + STEP + " §7coins.")
                        .build(),
                event -> bankManager.deposit(player, STEP).thenAccept(ok -> reopen()));

        setItem(WITHDRAW_SLOT, new ItemBuilder(Material.DROPPER)
                        .displayName("§cWithdraw")
                        .lore("§7Click to withdraw §6" + STEP + " §7coins.")
                        .build(),
                event -> bankManager.withdraw(player, STEP).thenAccept(ok -> reopen()));
    }

    /** Re-opens the menu so refreshed balances are shown. */
    private void reopen() {
        new BankMenu(player, coinManager, bankManager).open(player);
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
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
