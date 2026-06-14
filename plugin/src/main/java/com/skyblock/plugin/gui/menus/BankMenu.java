package com.skyblock.plugin.gui.menus;

import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.BankManager;
import org.bukkit.Material;

import java.util.Objects;
import java.util.UUID;

/**
 * The SkyBlock Bank menu.
 *
 * <p>A 54-slot (6-row) menu showing the viewing player's coins: their purse
 * balance (from {@link CoinManager}) as a {@code GOLD_INGOT} and their stored
 * bank balance (from {@link BankManager}) as the Bank Account {@code GOLD_BLOCK},
 * matching Hypixel's layout.</p>
 */
public class BankMenu extends Menu {

    /** Centre slot holding the purse icon. */
    private static final int PURSE_SLOT = 11;
    /** Centre slot holding the Bank Account icon. */
    private static final int BANK_SLOT = 15;

    private final UUID playerId;
    private final CoinManager coinManager;
    private final BankManager bankManager;

    public BankMenu(UUID playerId, CoinManager coinManager, BankManager bankManager) {
        super("Bank Account", 6);
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
        this.bankManager = Objects.requireNonNull(bankManager, "bankManager");
    }

    @Override
    protected void build() {
        long purse = coinManager.getBalance(playerId);
        double bank = bankManager.getBalance(playerId);
        setItem(PURSE_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§6Purse")
                .lore("§7Coins: §6" + purse)
                .build());
        setItem(BANK_SLOT, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Bank Account")
                .lore("§7Coins: §6" + String.format("%.1f", bank))
                .build());
    }
}
