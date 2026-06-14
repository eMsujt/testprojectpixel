package com.skyblock.plugin.gui.menu;

import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

/**
 * The SkyBlock Bank menu.
 *
 * <p>A 27-slot (3-row) menu showing the viewing player's purse balance (from
 * {@link CoinManager}) as a {@code GOLD_BLOCK}, matching Hypixel's layout.</p>
 */
public class BankMenu extends Menu {

    /** Centre slot holding the purse icon. */
    private static final int PURSE_SLOT = 11;

    private final UUID playerId;
    private final CoinManager coinManager;

    public BankMenu(UUID playerId, CoinManager coinManager) {
        super("§6Bank Account", 3);
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
    }

    @Override
    protected void build() {
        fillBorder();

        long purse = coinManager.getPurse(playerId);
        setItem(PURSE_SLOT, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Purse: §e" + purse)
                .lore("§7Coins: §6" + purse)
                .build());
    }

    /** Fills the menu's outer edge with yellow glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 27; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 18 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
