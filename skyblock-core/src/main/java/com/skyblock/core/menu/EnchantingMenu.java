package com.skyblock.core.menu;

import org.bukkit.plugin.java.JavaPlugin;
import com.skyblock.core.manager.EnchantmentManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Canonical Enchanting menu. A 54-slot (6-row) chest titled {@code §5§lEnchanting Table}
 * showing the player's enchanting-table icon (item-to-enchant slot 22, center) with their
 * current level and total XP, framed by a {@code PURPLE_STAINED_GLASS_PANE} border.
 */
public final class EnchantingMenu extends AbstractMenu {

    static final int TABLE_SLOT = 22;

    public EnchantingMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "§5§lEnchanting Table", 54);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        int level = EnchantmentManager.getInstance().getEnchantingLevel(player.getUniqueId());
        long totalXP = SkillManager.getInstance().getSkillXP(player.getUniqueId(), "enchanting");

        setItem(TABLE_SLOT, new ItemBuilder(Material.ENCHANTING_TABLE)
                .displayName("§5§lEnchanting Table")
                .lore(
                        "§7Enchanting Level: §d" + level,
                        "§7Total XP: §b" + totalXP,
                        "",
                        "§eClick to enchant an item!")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
