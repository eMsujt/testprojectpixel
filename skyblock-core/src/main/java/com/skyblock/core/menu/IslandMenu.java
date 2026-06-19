package com.skyblock.core.menu;

import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.manager.IslandManager.IslandUpgrade;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 54-slot Island menu. A grass block at slot 13 shows the player's current
 * island level, total XP, and XP required to reach the next level. Island
 * upgrades are displayed across row 4 (slots 28–35).
 */
public final class IslandMenu extends Menu {

    static final int OVERVIEW_SLOT = 13;
    static final int BEACON_SLOT = 22;
    static final int[] UPGRADE_SLOTS = {28, 29, 30, 31, 32, 33, 34, 35};

    private static final IslandUpgrade[] DISPLAYED_UPGRADES = IslandUpgrade.values();

    private final UUID owner;

    public IslandMenu(UUID owner) {
        super("§bYour Island", 6);
        this.owner = owner;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
        setItem(9, pane);  setItem(17, pane);
        setItem(18, pane); setItem(26, pane);
        setItem(27, pane); setItem(36, pane); setItem(44, pane);

        IslandManager manager = IslandManager.getInstance();
        long xp = manager.getIslandXp(owner);
        int level = IslandManager.levelFromXp(xp);
        long nextLevelXp = (long) (level + 1) * (level + 1) * IslandManager.XP_PER_LEVEL;
        long xpToNext = nextLevelXp - xp;

        setItem(OVERVIEW_SLOT, new ItemBuilder(Material.GRASS_BLOCK)
                .displayName("§aIsland Level " + level)
                .lore(
                        "§7Current XP: §e" + xp,
                        "§7XP to next level: §e" + xpToNext,
                        "§7Next level: §e" + (level + 1),
                        "§7Biome: §e" + manager.getIslandBiome(owner))
                .build());

        for (int i = 0; i < DISPLAYED_UPGRADES.length && i < UPGRADE_SLOTS.length; i++) {
            IslandUpgrade upgrade = DISPLAYED_UPGRADES[i];
            int upgradeLevel = manager.getIsland(owner)
                    .map(island -> island.getUpgradeLevel(upgrade))
                    .orElse(0);
            setItem(UPGRADE_SLOTS[i], new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .displayName("§a" + upgrade.getDisplayName())
                    .lore(
                            "§7Level: §e" + upgradeLevel + "§7/§e" + upgrade.getMaxLevel())
                    .build());
        }
    }
}
