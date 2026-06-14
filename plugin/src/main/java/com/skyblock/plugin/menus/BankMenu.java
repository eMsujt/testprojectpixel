package com.skyblock.plugin.menus;

import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * The "Bank Account" menu.
 *
 * <p>A 27-slot (3-row) menu titled {@code §6Bank Account} that frames a centred
 * {@link Material#PLAYER_HEAD} with a {@code GRAY_STAINED_GLASS_PANE} border. The
 * head's lore summarises the player's bank balance, from
 * {@link CoinManager}.</p>
 */
public class BankMenu extends Menu {

    /** The centre slot that holds the player's bank balance head. */
    private static final int BALANCE_SLOT = 4;

    private final Player player;

    public BankMenu(Player player) {
        super("§6Bank Account", 3);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID playerId = player.getUniqueId();
        CoinManager coins = CoinManager.getInstance();

        setItem(BALANCE_SLOT, balanceHead(coins, playerId));
    }

    /** Builds the centred player head describing the player's bank balance. */
    private ItemStack balanceHead(CoinManager coins, UUID playerId) {
        ItemStack head = new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§a" + player.getName())
                .lore("§7Balance: §6" + coins.getBank(playerId) + " coins")
                .build();

        if (head.getItemMeta() instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(player);
            head.setItemMeta(meta);
        }
        return head;
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
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
