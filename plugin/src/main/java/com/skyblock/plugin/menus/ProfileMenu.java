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
 * The "Your SkyBlock Profile" menu.
 *
 * <p>A 45-slot (5-row) menu titled {@code §aYour SkyBlock Profile} that frames a
 * centred {@link Material#PLAYER_HEAD} of the viewing player with a
 * {@code GRAY_STAINED_GLASS_PANE} border. The head's lore summarises the
 * player's profile stats (purse and bank balances, from
 * {@link CoinManager}).</p>
 */
public class ProfileMenu extends Menu {

    /** The centre slot that holds the player's profile head. */
    private static final int HEAD_SLOT = 13;

    private final Player player;

    public ProfileMenu(Player player) {
        super("§aYour SkyBlock Profile", 5);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID playerId = player.getUniqueId();
        CoinManager coins = CoinManager.getInstance();

        setItem(HEAD_SLOT, profileHead(coins, playerId));
    }

    /** Builds the centred player head describing the player's profile. */
    private ItemStack profileHead(CoinManager coins, UUID playerId) {
        ItemStack head = new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§a" + player.getName())
                .lore(
                        "§7Purse: §6" + coins.getPurse(playerId),
                        "§7Bank: §6" + coins.getBank(playerId))
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
        for (int slot = 0; slot < 45; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 36 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
