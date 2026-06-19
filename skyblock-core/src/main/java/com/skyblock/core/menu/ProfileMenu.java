package com.skyblock.core.menu;

import org.bukkit.plugin.java.JavaPlugin;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * Canonical "Your SkyBlock Profile" menu. A 54-slot (6-row) chest GUI framed
 * by a {@code CYAN_STAINED_GLASS_PANE} border with the viewing player's head
 * at slot 13, showing their purse and bank balances from {@link EconomyManager}.
 *
 * <p>All other ProfileMenu / PlayerMenu / PlayerStatsMenu / StatsGui classes in
 * the project are deprecated and delegate here.</p>
 */
public final class ProfileMenu extends AbstractMenu {

    private static final int HEAD_SLOT = 13;

    public ProfileMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§b§lSkyBlock Profile", 54);
    }

    @Override
    protected void populate() {
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
        ItemStack pane = new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE)
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
