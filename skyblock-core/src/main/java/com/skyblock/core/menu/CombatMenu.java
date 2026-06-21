package com.skyblock.core.menu;

import com.skyblock.core.model.Stat;
import com.skyblock.core.stats.StatsManager;
import com.skyblock.core.stats.StatsManager.PlayerStats;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 6-row GUI titled {@code §cCombat Stats}. Renders the player's combat-relevant
 * {@link Stat}s (Health, Defense, Strength, Crit Chance, Crit Damage, …) read
 * from the aggregated snapshot in {@link StatsManager}.
 */
public final class CombatMenu extends AbstractSkyBlockMenu {

    /** Combat-relevant stats shown in the menu, in display order. */
    private static final Stat[] COMBAT_STATS = {
            Stat.HEALTH, Stat.DEFENSE, Stat.STRENGTH, Stat.CRIT_CHANCE,
            Stat.CRIT_DAMAGE, Stat.ATTACK_SPEED, Stat.ABILITY_DAMAGE,
            Stat.TRUE_DEFENSE, Stat.FEROCITY, Stat.COMBAT_WISDOM
    };

    private static final int[] CONTENT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21
    };

    public CombatMenu(Player player) {
        super(player, "§cCombat Stats", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);

        PlayerStats stats = StatsManager.getInstance().getStats(player.getUniqueId());
        for (int i = 0; i < COMBAT_STATS.length && i < CONTENT_SLOTS.length; i++) {
            Stat stat = COMBAT_STATS[i];
            setItem(CONTENT_SLOTS[i], new ItemBuilder(Material.PAPER)
                    .displayName("§c" + stat.getDisplayName() + " " + stat.getSymbol())
                    .lore("§7Value: §e" + format(stats.getStat(stat)) + " §6" + stat.getSymbol())
                    .build());
        }
    }

    /** Formats a stat value, dropping the decimal for whole numbers. */
    private static String format(double value) {
        if (value == Math.floor(value)) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }
}
