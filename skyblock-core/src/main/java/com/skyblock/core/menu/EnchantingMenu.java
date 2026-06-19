package com.skyblock.core.menu;

import com.skyblock.core.manager.EnchantmentManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Canonical Enchanting menu. A 54-slot (6-row) chest titled {@code §5§lEnchanting Table}
 * showing the player's enchanting-table icon (item-to-enchant slot 22, center) with their
 * current level and total XP, framed by a {@code PURPLE_STAINED_GLASS_PANE} border.
 */
public final class EnchantingMenu extends Menu {

    static final int TABLE_SLOT = 22;

    private final UUID playerId;

    public EnchantingMenu(UUID playerId) {
        super("§5§lEnchanting Table", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        int level = EnchantmentManager.getInstance().getEnchantingLevel(playerId);
        long totalXP = SkillManager.getInstance().getSkillXP(playerId, "enchanting");

        setItem(TABLE_SLOT, new ItemBuilder(Material.ENCHANTING_TABLE)
                .displayName("§5§lEnchanting Table")
                .lore(
                        "§7Enchanting Level: §d" + level,
                        "§7Total XP: §b" + totalXP,
                        "",
                        "§eClick to enchant an item!")
                .build());
    }
}
