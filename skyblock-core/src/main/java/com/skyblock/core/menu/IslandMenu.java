package com.skyblock.core.menu;

import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.manager.IslandManager.IslandUpgrade;
import com.skyblock.core.manager.IslandManager.SkyBlockIsland;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 6-row /island GUI showing private island stats.
 *
 * <p>Layout:
 * <ul>
 *   <li>Row 0 (0–8): glass-pane border</li>
 *   <li>Row 1 (9–17): stat tiles — members (slot 11), grass-block level (slot 13),
 *       biome (slot 15)</li>
 *   <li>Row 2 (18–26): visitor-count (slot 20), XP bar (slot 22), warp (slot 24)</li>
 *   <li>Row 3 (27–35): island upgrades (one per upgrade enum value)</li>
 *   <li>Row 4 (36–44): glass-pane fill</li>
 *   <li>Row 5 (45–53): glass-pane border</li>
 * </ul>
 */
public final class IslandMenu extends Menu {

    static final int GRASS_SLOT = 13;
    static final int MEMBERS_SLOT = 11;
    static final int BIOME_SLOT = 15;
    static final int VISITORS_SLOT = 20;
    static final int XP_SLOT = 22;
    static final int WARP_SLOT = 24;
    static final int[] UPGRADE_SLOTS = {27, 28, 29, 30, 31, 32, 33, 34};

    private static final IslandUpgrade[] DISPLAYED_UPGRADES = IslandUpgrade.values();

    private final UUID owner;

    public IslandMenu(UUID owner) {
        super("§aPrivate Island", 6);
        this.owner = owner;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 36; slot < 54; slot++) setItem(slot, pane);
        setItem(9, pane);  setItem(17, pane);
        setItem(18, pane); setItem(26, pane);

        IslandManager manager = IslandManager.getInstance();
        long xp = manager.getIslandXp(owner);
        int level = IslandManager.levelFromXp(xp);
        long nextLevelXp = (long) (level + 1) * (level + 1) * IslandManager.XP_PER_LEVEL;
        long xpToNext = nextLevelXp - xp;
        Optional<SkyBlockIsland> islandOpt = manager.getIsland(owner);

        // Slot 13: grass block — island level (primary focal tile)
        setItem(GRASS_SLOT, new ItemBuilder(Material.GRASS_BLOCK)
                .displayName("§aIsland Level " + level)
                .lore(
                        "§7Total XP: §e" + xp,
                        "§7Next Level: §e" + (level + 1),
                        "§7XP Required: §e" + xpToNext)
                .build());

        // Slot 11: members
        List<String> memberLore = new ArrayList<>();
        islandOpt.ifPresent(island -> {
            memberLore.add("§7Members: §e" + island.getMembers().size());
            for (UUID m : island.getMembers()) {
                memberLore.add("§8- §a" + m.toString().substring(0, 8) + "…");
            }
        });
        if (memberLore.isEmpty()) memberLore.add("§7No members");
        setItem(MEMBERS_SLOT, new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§aIsland Members")
                .lore(memberLore)
                .build());

        // Slot 15: biome
        setItem(BIOME_SLOT, new ItemBuilder(Material.GRASS)
                .displayName("§aIsland Biome")
                .lore("§7Biome: §e" + manager.getIslandBiome(owner))
                .build());

        // Slot 20: visitor count
        setItem(VISITORS_SLOT, new ItemBuilder(Material.OAK_SIGN)
                .displayName("§aVisitor Count")
                .lore("§7Total Visitors: §e" + manager.getVisitorCount(owner))
                .build());

        // Slot 22: XP progress
        setItem(XP_SLOT, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .displayName("§aIsland XP")
                .lore(
                        "§7Level: §e" + level,
                        "§7XP: §e" + xp + " §7/ §e" + nextLevelXp,
                        "§7Until Next: §e" + xpToNext)
                .build());

        // Slot 24: warp
        String warpName = manager.getWarpName(owner);
        setItem(WARP_SLOT, new ItemBuilder(Material.ENDER_PEARL)
                .displayName("§aIsland Warp")
                .lore(warpName != null
                        ? List.of("§7Warp: §e" + warpName)
                        : List.of("§7No warp set"))
                .build());

        // Upgrade row (slots 27–34)
        for (int i = 0; i < DISPLAYED_UPGRADES.length && i < UPGRADE_SLOTS.length; i++) {
            IslandUpgrade upgrade = DISPLAYED_UPGRADES[i];
            int upgradeLevel = islandOpt
                    .map(island -> island.getUpgradeLevel(upgrade))
                    .orElse(0);
            setItem(UPGRADE_SLOTS[i], new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .displayName("§a" + upgrade.getDisplayName())
                    .lore("§7Level: §e" + upgradeLevel + " §7/ §e" + upgrade.getMaxLevel())
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
