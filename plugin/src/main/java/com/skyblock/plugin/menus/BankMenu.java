package com.skyblock.plugin.menus;

import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The "Bank Account" menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Bank Account} that frames a centred
 * {@link Material#GOLD_INGOT} with a {@code GRAY_STAINED_GLASS_PANE} border. The
 * ingot's lore summarises the player's bank balance, from
 * {@link CoinManager}.</p>
 */
public class BankMenu extends Menu {

    /** The centre slot that holds the player's bank balance ingot. */
    private static final int BALANCE_SLOT = 13;

    private final Player player;

    public BankMenu(Player player) {
        super("§6Bank Account", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID playerId = player.getUniqueId();
        CoinManager coins = CoinManager.getInstance();

        setItem(BALANCE_SLOT, balanceIngot(coins, playerId));
    }

    /** Builds the centred ingot describing the player's bank balance. */
    private ItemStack balanceIngot(CoinManager coins, UUID playerId) {
        return new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§6You")
                .lore("§7Balance: §6" + coins.getBank(playerId))
                .build();
    }

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
