package com.skyblock.plugin.gui.menus;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.EnchantingManager;
import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.Material;

import java.util.UUID;

/**
 * The SkyBlock Enchanting menu.
 *
 * <p>A 54-slot (6-row) menu matching Hypixel's Enchanting layout: slot 4 holds
 * the {@code ENCHANTING_TABLE} icon showing the player's current enchanting
 * level and total XP; slot 22 shows their accumulated bookshelf power.</p>
 */
public class EnchantingMenu extends Menu {

    private static final int TABLE_SLOT     = 4;
    private static final int BOOKSHELF_SLOT = 22;

    private final UUID playerId;

    public EnchantingMenu(UUID playerId) {
        super("Enchanting", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        SkillsManager sm = SkillsManager.getInstance();
        EnchantingManager em = EnchantingManager.getInstance();

        long totalXP = sm.getSkillXP(playerId, "enchanting");
        int level = computeLevel(totalXP);
        int bookshelfPower = em.getBookshelfPower(playerId);

        setItem(TABLE_SLOT, new ItemBuilder(Material.ENCHANTING_TABLE)
                .displayName("§dEnchanting Table")
                .lore(
                        "§7Enchanting Level: §d" + level,
                        "§7Total XP: §b" + totalXP)
                .build());

        setItem(BOOKSHELF_SLOT, new ItemBuilder(Material.BOOKSHELF)
                .displayName("§6Bookshelf Power")
                .lore("§7Power: §6" + bookshelfPower)
                .build());
    }

    private static int computeLevel(long totalXP) {
        long[] table = SkillsManager.SKILL_XP_TABLE.get("enchanting");
        if (table == null) {
            return 0;
        }
        long cumulative = 0;
        int level = 0;
        for (long threshold : table) {
            cumulative += threshold;
            if (totalXP < cumulative) {
                break;
            }
            level++;
        }
        return level;
    }
}
