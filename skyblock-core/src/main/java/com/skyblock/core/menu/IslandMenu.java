package com.skyblock.core.menu;

import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.manager.IslandManager.IslandUpgrade;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class IslandMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "Island Management";

    static final int OVERVIEW_SLOT   = 13;
    static final int VISIT_SLOT      = 20;
    static final int MANAGE_SLOT     = 22;
    static final int SETTINGS_SLOT   = 24;
    // All 10 upgrades, centered across rows 3-4 (cols 2-6) — none on a border slot.
    static final int[] UPGRADE_SLOTS = {29, 30, 31, 32, 33, 38, 39, 40, 41, 42};

    private static final IslandUpgrade[] DISPLAYED_UPGRADES = IslandUpgrade.values();

    public IslandMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
        setItem(9, pane);  setItem(17, pane);
        setItem(18, pane); setItem(26, pane);
        setItem(27, pane); setItem(36, pane); setItem(44, pane);

        UUID owner = player.getUniqueId();
        IslandManager manager = IslandManager.getInstance();
        long xp = manager.getIslandXp(owner);
        int level = IslandManager.levelFromXp(xp);
        long nextLevelXp = IslandManager.xpForLevel(level + 1);
        long xpToNext = nextLevelXp - xp;

        setItem(OVERVIEW_SLOT, new ItemBuilder(Material.GRASS_BLOCK)
                .displayName("§aIsland Level " + level)
                .lore(
                        "§7Current XP: §e" + xp,
                        "§7XP to next level: §e" + xpToNext,
                        "§7Next level: §e" + (level + 1),
                        "§7Biome: §e" + manager.getIslandBiome(owner))
                .build());

        setItem(VISIT_SLOT, new ItemBuilder(Material.OAK_DOOR)
                .displayName("§aVisit Island")
                .lore("§7Teleport to your island.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                    manager.getIslandWorld(owner).ifPresent(world ->
                            player.teleport(world.getSpawnLocation()));
                });

        int memberCount = manager.getIsland(owner)
                .map(island -> island.getMembers().size())
                .orElse(0);
        setItem(MANAGE_SLOT, new ItemBuilder(Material.EMERALD)
                .displayName("§eManage Members")
                .lore(
                        "§7Members: §e" + memberCount,
                        "§7Click to manage island members.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.sendMessage("§7Use §e/island manage §7to manage members.");
                });

        String warpName = manager.getWarpName(owner);
        setItem(SETTINGS_SLOT, new ItemBuilder(Material.COMPARATOR)
                .displayName("§bIsland Settings")
                .lore(
                        "§7Warp: " + (warpName != null ? "§e" + warpName : "§cNone"),
                        "§7Visitors: §e" + manager.getVisitorCount(owner),
                        "§7Click to view settings.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.sendMessage("§7Use §e/island settings §7to change settings.");
                });

        for (int i = 0; i < DISPLAYED_UPGRADES.length && i < UPGRADE_SLOTS.length; i++) {
            IslandUpgrade upgrade = DISPLAYED_UPGRADES[i];
            int upgradeLevel = manager.getIsland(owner)
                    .map(island -> island.getUpgradeLevel(upgrade))
                    .orElse(0);
            setItem(UPGRADE_SLOTS[i], new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .displayName("§a" + upgrade.getDisplayName())
                    .lore("§7Level: §e" + upgradeLevel + "§7/§e" + upgrade.getMaxLevel())
                    .build());
        }
    }
}
