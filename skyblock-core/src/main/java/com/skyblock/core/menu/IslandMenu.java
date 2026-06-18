package com.skyblock.core.menu;

import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 27-slot Island overview menu. Shows the player's private-island level as a
 * beacon item, along with the cumulative island XP and the XP still required to
 * reach the next level (SkyBlock formula {@code level = floor(sqrt(xp / 100))}).
 */
public final class IslandMenu extends Menu {

    private static final int LEVEL_SLOT = 13;

    private final UUID playerId;

    public IslandMenu(UUID playerId) {
        super("§aYour Island", 3);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 27; slot++) setItem(slot, pane);

        IslandManager manager = IslandManager.getInstance();
        long xp = manager.getIslandXp(playerId);
        int level = IslandManager.levelFromXp(xp);
        long nextThreshold = (long) (level + 1) * (level + 1) * IslandManager.XP_PER_LEVEL;
        long toNext = nextThreshold - xp;

        setItem(LEVEL_SLOT, new ItemBuilder(Material.BEACON)
                .displayName("§aIsland Level §e" + level)
                .lore(
                        "§7Island XP: §e" + xp,
                        "§7XP to level §e" + (level + 1) + "§7: §e" + toNext)
                .build());
    }
}
