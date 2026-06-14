package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.economy.CoinManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.BankManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

/**
 * The SkyBlock Bank menu.
 *
 * <p>A 54-slot (6-row) menu showing the viewing player's coins: their purse
 * balance (from {@link CoinManager}) as a {@code GOLD_NUGGET} and their stored
 * bank balance (from {@link BankManager}) as the Bank Account {@code GOLD_BLOCK},
 * matching Hypixel's layout.</p>
 */
public class BankMenu extends Menu {

    /** Centre slot holding the purse icon. */
    private static final int PURSE_SLOT = 13;
    /** Centre slot holding the Bank Account icon. */
    private static final int BANK_SLOT = 15;

    private final UUID playerId;
    private final CoinManager coinManager;
    private final BankManager bankManager;

    public BankMenu(UUID playerId, CoinManager coinManager, BankManager bankManager) {
        super("§6Bank Account", 6);
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
        this.bankManager = Objects.requireNonNull(bankManager, "bankManager");
    }

    @Override
    protected void build() {
        fillBorder();

        long purse = coinManager.getPurse(playerId);
        double bank = bankManager.getBalance(playerId);
        setItem(PURSE_SLOT, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Purse")
                .lore("§7Coins: §6" + purse)
                .build());
        setItem(BANK_SLOT, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Bank Account")
                .lore("§7Coins: §6" + String.format("%.1f", bank))
                .build());
    }

    /** Fills the menu's outer edge with yellow glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
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
