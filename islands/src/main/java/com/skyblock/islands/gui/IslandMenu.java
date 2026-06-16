package com.skyblock.islands.gui;

import com.skyblock.islands.manager.IslandManager;
import com.skyblock.islands.manager.IslandManager.IslandUpgrade;
import com.skyblock.islands.manager.IslandManager.SkyBlockIsland;
import com.skyblock.core.menu.Menu;
import com.skyblock.items.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Canonical Island management menu. A 54-slot (6-row) chest titled {@code §aYour Island},
 * framed by a {@code GRAY_STAINED_GLASS_PANE} border, showing island info, all
 * {@link IslandUpgrade} levels, members, and a Close button.
 *
 * <p>All other IslandMenu/IslandGui/IslandMainMenu classes in the project are
 * deprecated stubs that delegate here.</p>
 */
public final class IslandMenu extends Menu {

    private static final int SLOT_INFO      = 4;
    private static final int SLOT_MEMBERS   = 22;
    private static final int SLOT_HISTORY   = 40;
    private static final int SLOT_CLOSE     = 49;

    private static final int[] UPGRADE_SLOTS = {10, 12, 14, 16, 28, 30, 32, 34};

    private static final Material[] UPGRADE_ICONS = {
        Material.DISPENSER,       // MINION_SLOTS
        Material.MAP,             // ISLAND_SIZE
        Material.CHEST,           // CHEST_SIZE
        Material.OAK_SIGN,        // GUEST_LIMIT
        Material.PLAYER_HEAD,     // COOP_SLOTS
        Material.REDSTONE,        // REDSTONE_LIMIT
        Material.WHEAT,           // CROP_GROWTH
        Material.ZOMBIE_HEAD,     // MOB_SPAWN_RATE
    };

    private final Player player;

    public IslandMenu(Player player) {
        super("§aYour Island", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID id = player.getUniqueId();
        IslandManager mgr = IslandManager.getInstance();
        IslandManager.IslandData data = mgr.getOrCreateIslandData(id);
        Optional<SkyBlockIsland> islandOpt = mgr.getIsland(id);

        // Island info
        String warpName = mgr.getWarpName(id);
        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7Level: §e" + data.level());
        infoLore.add("§7Blocks Placed: §e" + data.blocksPlaced());
        infoLore.add("§7Trustees: §e" + data.trustees().size());
        infoLore.add("§7Warp: §e" + (warpName != null ? warpName : "§8Not set"));
        setItem(SLOT_INFO, new ItemBuilder(Material.GRASS_BLOCK)
                .displayName("§aIsland Info")
                .lore(infoLore)
                .build(),
                e -> e.setCancelled(true));

        // Upgrades
        IslandUpgrade[] upgrades = IslandUpgrade.values();
        for (int i = 0; i < upgrades.length && i < UPGRADE_SLOTS.length; i++) {
            IslandUpgrade upgrade = upgrades[i];
            int level = islandOpt.map(isl -> isl.getUpgradeLevel(upgrade)).orElse(0);
            int maxLevel = upgrade.getMaxLevel();
            String bar = buildLevelBar(level, maxLevel);
            setItem(UPGRADE_SLOTS[i], new ItemBuilder(UPGRADE_ICONS[i])
                    .displayName("§6" + upgrade.getDisplayName())
                    .lore("§7Level: §e" + level + " §7/ §e" + maxLevel, bar)
                    .build(),
                    e -> e.setCancelled(true));
        }

        // Members
        List<UUID> members = islandOpt.map(SkyBlockIsland::getMembers).orElse(List.of());
        List<String> memberLore = new ArrayList<>();
        if (members.isEmpty()) {
            memberLore.add("§7No members.");
        } else {
            for (UUID member : members) {
                org.bukkit.entity.Player online = org.bukkit.Bukkit.getPlayer(member);
                memberLore.add("§7- §e" + (online != null ? online.getName() : member.toString()));
            }
        }
        setItem(SLOT_MEMBERS, new ItemBuilder(Material.PLAYER_HEAD)
                .displayName("§aMembers §7(" + members.size() + ")")
                .lore(memberLore)
                .build(),
                e -> e.setCancelled(true));

        // Recent history
        List<String> history = mgr.getIslandHistory(id);
        List<String> historyLore = new ArrayList<>();
        int start = Math.max(0, history.size() - 5);
        for (int i = start; i < history.size(); i++) {
            historyLore.add("§7" + history.get(i));
        }
        if (historyLore.isEmpty()) historyLore.add("§7No events recorded.");
        setItem(SLOT_HISTORY, new ItemBuilder(Material.BOOK)
                .displayName("§aIsland History")
                .lore(historyLore)
                .build(),
                e -> e.setCancelled(true));

        // Close
        setItem(SLOT_CLOSE, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                });
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);
        for (int row = 1; row < 5; row++) {
            setItem(row * 9, pane);
            setItem(row * 9 + 8, pane);
        }
    }

    private static String buildLevelBar(int level, int maxLevel) {
        int filled = maxLevel == 0 ? 0 : Math.min(level, maxLevel);
        StringBuilder sb = new StringBuilder("§7[");
        for (int i = 0; i < maxLevel; i++) {
            sb.append(i < filled ? "§a|" : "§8|");
        }
        sb.append("§7]");
        return sb.toString();
    }
}
