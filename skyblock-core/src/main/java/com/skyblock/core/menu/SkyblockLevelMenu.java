package com.skyblock.core.menu;

import com.skyblock.core.manager.SkyblockLevelManager;
import com.skyblock.core.manager.SkyblockLevelManager.Category;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * Canonical "SkyBlock Level" menu. A 54-slot (6-row) chest GUI framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border with the viewing player's head at
 * slot 13 summarising their level, total XP and XP to the next level
 * (from {@link SkyblockLevelManager}), and one tile per XP
 * {@link Category source} showing the per-category breakdown.
 */
public final class SkyblockLevelMenu extends Menu {

    private static final String TITLE = "§aSkyBlock Leveling";
    private static final int HEAD_SLOT = 13;

    /** One breakdown tile per category, laid out across the third row. */
    private static final int[] CATEGORY_SLOTS = {19, 21, 23, 25, 29, 31};

    private static final Material[] CATEGORY_ICONS = {
            Material.DIAMOND_SWORD,   // SKILL
            Material.GOLDEN_SWORD,    // SLAYER
            Material.OAK_DOOR,        // DUNGEON
            Material.CAKE,            // EVENT
            Material.GOLD_BLOCK,      // MUSEUM
            Material.PAPER,           // MISC
    };

    private final Player player;

    public SkyblockLevelMenu(Player player) {
        super(TITLE, 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID id = player.getUniqueId();
        SkyblockLevelManager levels = SkyblockLevelManager.getInstance();
        int level = levels.getLevel(id);
        long totalXP = levels.getXP(id);
        long toNext = levels.xpToNextLevel(id);

        // Hypixel shows the level summary as a Painting at slot 4.
        setItem(4, new ItemBuilder(Material.PAINTING)
                .displayName("§aYour SkyBlock Level")
                .lore(
                        "§7Level: §e" + level + " §7/ §e" + SkyblockLevelManager.MAX_LEVEL,
                        "§7Total XP: §e" + String.format("%,d", totalXP),
                        level < SkyblockLevelManager.MAX_LEVEL
                                ? "§7Progress to next: §e" + String.format("%,d", toNext) + " §7XP"
                                : "§6Maximum level reached!")
                .build(), e -> e.setCancelled(true));

        Category[] categories = Category.values();
        for (int i = 0; i < categories.length && i < CATEGORY_SLOTS.length; i++) {
            Category category = categories[i];
            long xp = levels.getCategoryXP(id, category);
            setItem(CATEGORY_SLOTS[i], new ItemBuilder(CATEGORY_ICONS[i])
                    .displayName("§a" + displayName(category))
                    .lore("§7XP: §e" + String.format("%,d", xp))
                    .build(),
                    e -> e.setCancelled(true));
        }

        setItem(34, new ItemBuilder(Material.CHEST)
                .displayName("§aLeveling Rewards")
                .lore("§7View the rewards you unlock", "§7by leveling up your SkyBlock Level.")
                .build(), e -> e.setCancelled(true));

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
    }

    /** Title-cases an enum constant name, e.g. {@code SKILL} -> {@code Skill}. */
    private static String displayName(Category category) {
        String name = category.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
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
