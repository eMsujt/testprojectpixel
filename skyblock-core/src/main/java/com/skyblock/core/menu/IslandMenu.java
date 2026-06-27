package com.skyblock.core.menu;

import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.manager.IslandManager.IslandUpgrade;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The Private Island "Island Management" hub, matching the wiki
 * {@code {{UI|Island Management}}} layout: Island Category (slot 11), Island
 * Name (13), Island Ranks (15), Island Properties (29), Alex's Island (31),
 * Guests Management (33) and a Go Back arrow (49).
 *
 * <p>Island Properties surfaces the island level, biome and upgrade summary in
 * its lore; co-op/cosmetic sub-screens that don't exist yet report "coming
 * soon" rather than inventing UI. (Visiting an island is via Fast Travel.)</p>
 */
public final class IslandMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "Island Management";

    static final int CATEGORY_SLOT   = 11;
    static final int NAME_SLOT       = 13;
    static final int RANKS_SLOT      = 15;
    static final int PROPERTIES_SLOT = 29;
    static final int ALEX_SLOT       = 31;
    static final int GUESTS_SLOT     = 33;
    static final int BACK_SLOT       = 49;

    public IslandMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        UUID owner = player.getUniqueId();
        IslandManager manager = IslandManager.getInstance();

        // 2,3 — Island Category
        stub(CATEGORY_SLOT, Material.FILLED_MAP, "§aIsland Category", "Island categories are coming soon.",
                "§7Choose how your island is", "§7listed in the Island Browser.", "", "§eClick to view!");

        // 2,5 — Island Name
        stub(NAME_SLOT, Material.NAME_TAG, "§aIsland Name", "Island naming is coming soon.",
                "§7Give your island a custom name.", "", "§cNot Unlocked");

        // 2,7 — Island Ranks
        stub(RANKS_SLOT, Material.REDSTONE, "§aIsland Ranks", "Island ranks are coming soon.",
                "§7Grant ranks and permissions to", "§7your island members.", "", "§cNot Unlocked");

        // 4,3 — Island Properties (surfaces island level / biome / upgrades)
        long xp = manager.getIslandXp(owner);
        int level = IslandManager.levelFromXp(xp);
        long xpToNext = IslandManager.xpForLevel(level + 1) - xp;
        int upgradesOwned = 0;
        for (IslandUpgrade up : IslandUpgrade.values()) {
            int lvl = manager.getIsland(owner).map(i -> i.getUpgradeLevel(up)).orElse(0);
            if (lvl > 0) {
                upgradesOwned++;
            }
        }
        setItem(PROPERTIES_SLOT, new ItemBuilder(Material.NETHER_STAR)
                .displayName("§aIsland Properties")
                .lore(
                        "§7Island Level: §e" + level,
                        "§7XP to next level: §e" + Math.max(0, xpToNext),
                        "§7Biome: §e" + manager.getIslandBiome(owner),
                        "§7Upgrades owned: §e" + upgradesOwned + "§7/§e" + IslandUpgrade.values().length,
                        "",
                        "§eClick to view!")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.sendMessage("§eThe full Island Properties screen is coming soon.");
                });

        // 4,5 — Alex's Island (co-op)
        stub(ALEX_SLOT, Material.PLAYER_HEAD, "§7Alex's Island", "Co-op islands are coming soon.",
                "§7Visit and manage co-op islands.", "", "§eClick to view!");

        // 4,7 — Guests Management
        stub(GUESTS_SLOT, Material.FERN, "§aGuests Management", "Guest management is coming soon.",
                "§7Guests: §a0§7/§a1",
                "§8No online guests!",
                "", "§eClick to manage!");

        // 6,4 — Go Back
        setItem(BACK_SLOT, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To Settings")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new SettingsMenu(player).open(player);
                });
    }

    /** A labelled hub button that reports {@code comingSoon} on click (sub-screen not built yet). */
    private void stub(int slot, Material material, String name, String comingSoon, String... lore) {
        setItem(slot, new ItemBuilder(material).displayName(name).lore(lore).build(),
                e -> {
                    e.setCancelled(true);
                    player.sendMessage("§e" + comingSoon);
                });
    }
}
