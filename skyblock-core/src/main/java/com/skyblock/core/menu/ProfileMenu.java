package com.skyblock.core.menu;

import com.skyblock.core.economy.manager.EconomyManager;
import com.skyblock.items.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * Canonical "Your SkyBlock Profile" menu. A 54-slot (6-row) chest GUI framed
 * by a {@code GRAY_STAINED_GLASS_PANE} border with the viewing player's head
 * at slot 13, showing their purse and bank balances from {@link EconomyManager}.
 *
 * <p>All other ProfileMenu / PlayerMenu / PlayerStatsMenu / StatsGui classes in
 * the project are deprecated and delegate here.</p>
 */
public final class ProfileMenu extends Menu {

    private static final String TITLE = "§aYour SkyBlock Profile";
    private static final int HEAD_SLOT = 13;

    private final Player player;

    public ProfileMenu(Player player) {
        super(TITLE, 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID id = player.getUniqueId();
        EconomyManager eco = EconomyManager.getInstance();

        ItemStack skull = new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§a" + player.getName())
                .lore(
                        "§7Purse: §6" + String.format("%,.0f", (double) eco.getPurse(id)) + " Coins",
                        "§7Bank: §6" + String.format("%,.0f", (double) eco.getBank(id)) + " Coins")
                .build();

        if (skull.getItemMeta() instanceof SkullMeta meta) {
            meta.setOwningPlayer(player);
            skull.setItemMeta(meta);
        }

        setItem(HEAD_SLOT, skull, e -> e.setCancelled(true));
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }
}
