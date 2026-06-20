package com.skyblock.core.menu;

import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.manager.IslandManager.IslandUpgrade;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 54-slot Island menu. Slot 13 shows the player's island level/XP overview.
 * Row 3 holds clickable options (Visit, Manage, Settings). Row 4 displays
 * island upgrades.
 */
public final class IslandMenu extends Menu {

    static final int OVERVIEW_SLOT   = 13;
    static final int BEACON_SLOT     = 49;
    static final int VISIT_SLOT      = 20;
    static final int MANAGE_SLOT     = 22;
    static final int SETTINGS_SLOT   = 24;
    static final int[] UPGRADE_SLOTS = {28, 29, 30, 31, 32, 33, 34, 35};

    private static final IslandUpgrade[] DISPLAYED_UPGRADES = IslandUpgrade.values();

    private final UUID owner;
    private Player viewer;

    public IslandMenu(UUID owner) {
        super("§a§lYour Island", 6);
        this.owner = owner;
    }

    @Override
    public void open(Player player) {
        this.viewer = player;
        super.open(player);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        super.handleClick(event);
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

        setItem(VISIT_SLOT, new ItemBuilder(Material.OAK_DOOR)
                .displayName("§aVisit Island")
                .lore("§7Teleport to your island.")
                .build(),
                e -> {
                    Player p = (Player) e.getWhoClicked();
                    p.closeInventory();
                    manager.getIslandWorld(owner).ifPresent(world ->
                            p.teleport(world.getSpawnLocation()));
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
                e -> ((Player) e.getWhoClicked())
                        .sendMessage("§7Use §e/island manage §7to manage members."));

        String warpName = manager.getWarpName(owner);
        setItem(SETTINGS_SLOT, new ItemBuilder(Material.COMPARATOR)
                .displayName("§bIsland Settings")
                .lore(
                        "§7Warp: " + (warpName != null ? "§e" + warpName : "§cNone"),
                        "§7Visitors: §e" + manager.getVisitorCount(owner),
                        "§7Click to view settings.")
                .build(),
                e -> ((Player) e.getWhoClicked())
                        .sendMessage("§7Use §e/island settings §7to change settings."));

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
