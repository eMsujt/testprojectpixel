package com.skyblock.core.menu;

import org.bukkit.plugin.java.JavaPlugin;
import com.skyblock.core.manager.EnchantingManager;
import com.skyblock.core.manager.EnchantingManager.SkyBlockEnchantment;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Enchanting Table menu — 54-slot (6-row) chest.
 *
 * <p>Layout:
 * <ul>
 *   <li>Purple glass pane border (top/bottom rows + left/right columns)</li>
 *   <li>Slot 4 (top-center): Player enchanting summary</li>
 *   <li>Slot 22 (center): Enchanting Table icon</li>
 *   <li>Interior rows 2–5 (excluding center): Applied enchantments as book items (max 24)</li>
 *   <li>Slot 49 (bottom-center): "All Enchants" catalogue count</li>
 * </ul>
 * </p>
 */
public final class EnchantingMenu extends AbstractMenu {

    static final int TABLE_SLOT = 22;
    static final int SUMMARY_SLOT = 4;
    static final int CATALOGUE_SLOT = 49;

    // Interior slots: rows 2–5, cols 1–7, excluding the center (slot 22) and summary (slot 4)
    private static final int[] ENCHANT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21,     23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

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

        EnchantingManager mgr = EnchantingManager.getInstance();
        int level = mgr.getEnchantingLevel(player.getUniqueId());
        long totalXP = SkillManager.getInstance().getSkillXP(player.getUniqueId(), "enchanting");
        Map<SkyBlockEnchantment, Integer> applied = mgr.getEnchantments(player.getUniqueId());

        // Slot 4: player summary
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .displayName("§a§lYour Enchanting Info")
                .lore(
                        "§7Enchanting Level: §a" + level,
                        "§7Total XP: §b" + totalXP,
                        "§7Active Enchants: §d" + applied.size(),
                        "",
                        "§7Use §e/enchanting §7to manage enchants.")
                .build());

        // Slot 22: Enchanting Table icon
        setItem(TABLE_SLOT, new ItemBuilder(Material.ENCHANTING_TABLE)
                .displayName("§5§lEnchanting Table")
                .lore(
                        "§7Enchanting Level: §d" + level,
                        "§7Total XP: §b" + totalXP,
                        "",
                        "§7Enchant items and apply books",
                        "§7using §e/enchanting apply§7.")
                .build());

        // Interior slots: applied enchantments as book icons
        List<Map.Entry<SkyBlockEnchantment, Integer>> entries = new ArrayList<>(applied.entrySet());
        for (int i = 0; i < ENCHANT_SLOTS.length && i < entries.size(); i++) {
            Map.Entry<SkyBlockEnchantment, Integer> e = entries.get(i);
            SkyBlockEnchantment type = e.getKey();
            int enchLevel = e.getValue();
            int maxLevel = type.getMaxLevel();
            int cost = mgr.getEnchantCost(type, enchLevel);
            boolean isUltimate = mgr.isUltimate(type);

            List<String> lore = new ArrayList<>();
            lore.add("§7Level: §e" + enchLevel + " §8/ §7" + maxLevel);
            lore.add("§7Cost per level: §6" + cost + " XP");
            if (isUltimate) {
                lore.add("§c§lULTIMATE");
            }
            lore.add("");
            lore.add("§8/enchanting remove " + type.name().toLowerCase());

            Material mat = isUltimate ? Material.GOLDEN_SWORD : Material.ENCHANTED_BOOK;
            setItem(ENCHANT_SLOTS[i], new ItemBuilder(mat)
                    .displayName((isUltimate ? "§6§l" : "§d") + type.getDisplayName())
                    .lore(lore.toArray(new String[0]))
                    .build());
        }

        // Slot 49: catalogue info
        int total = SkyBlockEnchantment.values().length;
        setItem(CATALOGUE_SLOT, new ItemBuilder(Material.BOOK)
                .displayName("§e§lAll Enchantments")
                .lore(
                        "§7Available: §e" + total + " enchantment types",
                        "§7Active on you: §d" + applied.size(),
                        "",
                        "§7Use §e/enchanting list §7to browse all.")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
