package com.skyblock.core.menu;

import com.skyblock.core.manager.EnchantmentManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Canonical Enchanting menu. A 54-slot (6-row) chest titled {@code §5Enchanting}
 * showing the player's enchanting-table icon (slot 4) with their current level
 * and total XP, framed by a {@code GRAY_STAINED_GLASS_PANE} border.
 *
 * <p>All other EnchantingMenu/EnchantmentMenu/EnchantGui classes in the project
 * are deprecated stubs that delegate here.</p>
 */
public final class EnchantingMenu extends Menu {

    private static final int TABLE_SLOT = 4;

    private final UUID playerId;

    public EnchantingMenu(UUID playerId) {
        super("§5Enchanting", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        int level = EnchantmentManager.getInstance().getEnchantingLevel(playerId);
        long totalXP = SkillManager.getInstance().getSkillXP(playerId, "enchanting");

        setItem(TABLE_SLOT, new ItemBuilder(Material.ENCHANTING_TABLE)
                .displayName("§dEnchanting Table")
                .lore(
                        "§7Enchanting Level: §d" + level,
                        "§7Total XP: §b" + totalXP)
                .build());
    }
}
