package com.skyblock.core.menu;

import com.skyblock.core.model.Stat;
import com.skyblock.core.stats.StatsManager;
import com.skyblock.core.stats.StatsManager.PlayerStats;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * GUI menu opened by {@code /stats}. Renders every {@link Stat} tracked in
 * SkyBlock (Health, Defense, Strength, Speed, Crit Chance, Crit Damage, …) as a
 * named-item stack showing the player's current effective value, read from the
 * aggregated snapshot in {@link StatsManager}.
 *
 * <p>Each stat shows its lore-symbol and effective value (base + bonus); stats
 * are laid out in declaration order starting at {@link #FIRST_STAT_SLOT}.</p>
 */
public final class StatsMenu extends Menu {

    static final int SUMMARY_SLOT = 4;
    /** First slot of the stat grid; stats fill consecutive slots from here. */
    static final int FIRST_STAT_SLOT = 9;

    private final UUID playerId;

    public StatsMenu(Player player) {
        this(player.getUniqueId());
    }

    public StatsMenu(UUID playerId) {
        super("§aYour Stats", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        drawBorder();

        PlayerStats stats = StatsManager.getInstance().getStats(playerId);

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.NETHER_STAR)
                .displayName("§aYour Stats")
                .lore(
                        "§7Health: §c" + format(stats.getStat(Stat.HEALTH)) + " ❤",
                        "§7Defense: §a" + format(stats.getStat(Stat.DEFENSE)) + " ❈",
                        "§7Strength: §c" + format(stats.getStat(Stat.STRENGTH)) + " ❁",
                        "",
                        "§7Your combined stats from all sources.")
                .build());

        int index = 0;
        for (Stat stat : Stat.values()) {
            if (index >= contentCapacity()) break;
            setItem(contentSlot(index), new ItemBuilder(Material.PAPER)
                    .displayName("§a" + stat.getDisplayName() + " " + stat.getSymbol())
                    .lore("§7Value: §e" + format(stats.getStat(stat)) + " §6" + stat.getSymbol())
                    .build());
            index++;
        }
    }

    /** Formats a stat value, dropping the decimal for whole numbers. */
    private static String format(double value) {
        if (value == Math.floor(value)) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
