package com.skyblock.core.menu;

import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.DungeonManager.DungeonFloor;
import com.skyblock.core.manager.DungeonStatsManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 54-slot Catacombs statistics menu opened by {@code /dungeonstats}.
 *
 * <p>Centre tile (slot 13) summarises the player's overall Catacombs level and
 * XP from {@link DungeonStatsManager}; the surrounding tiles break down total
 * floor completions, secrets found and bosses killed. Top and bottom edges are
 * gray-pane borders.</p>
 */
public final class DungeonStatsMenu extends Menu {

    private static final int SUMMARY_SLOT = 13;
    private static final int COMPLETIONS_SLOT = 20;
    private static final int SECRETS_SLOT = 22;
    private static final int BOSSES_SLOT = 24;

    private final UUID playerId;

    public DungeonStatsMenu(UUID playerId) {
        super("§5Catacombs Stats", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        DungeonStatsManager stats = DungeonStatsManager.getInstance();
        DungeonManager dungeons = DungeonManager.getInstance();

        int level = stats.getCatacombsLevel(playerId);
        double xp = stats.getCatacombsXp(playerId);
        double toNext = stats.getXpToNextLevel(playerId);

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.NETHER_STAR)
                .displayName("§5Catacombs Level " + level)
                .lore(
                        "§7Total XP: §b" + (long) xp,
                        "§7To next level: §b" + (level >= DungeonStatsManager.MAX_CATACOMBS_LEVEL
                                ? "MAX" : (long) toNext))
                .build());

        int totalCompletions = 0;
        for (DungeonFloor floor : DungeonFloor.values()) {
            totalCompletions += dungeons.getFloorCompletionCount(playerId, floor);
        }

        setItem(COMPLETIONS_SLOT, new ItemBuilder(Material.WITHER_SKELETON_SKULL)
                .displayName("§fFloor Completions")
                .lore("§7Total runs cleared: §e" + totalCompletions)
                .build());

        setItem(SECRETS_SLOT, new ItemBuilder(Material.TRIPWIRE_HOOK)
                .displayName("§fSecrets Found")
                .lore("§7Lifetime secrets: §e" + stats.getSecretsFound(playerId))
                .build());

        setItem(BOSSES_SLOT, new ItemBuilder(Material.DRAGON_HEAD)
                .displayName("§fBosses Killed")
                .lore("§7Lifetime boss kills: §e" + stats.getBossKills(playerId))
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
