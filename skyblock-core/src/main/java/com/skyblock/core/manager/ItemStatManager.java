package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton for reading and writing SkyBlock stat lines on {@link ItemStack} lore.
 *
 * <p>Each stat is stored as a lore line in the form {@code §a+{value} {symbol} {name}}.
 * {@link #applyStats} appends the given stats; {@link #getStats} parses them back;
 * {@link #clearStats} strips any previously written stat lines.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class ItemStatManager {

    /** Prefix added to every stat lore line so they can be identified and stripped. */
    private static final String STAT_PREFIX = "§a+";

    /** Matches a real Hypixel-format stat line (color codes stripped), e.g. "Strength: +100". */
    private static final Pattern HYPIXEL_STAT = Pattern.compile("^(.+?): ([+-]?[0-9,]+)");

    /** Stat display name -> Stat, for parsing real Hypixel item lore. */
    private static final Map<String, Stat> STAT_BY_NAME = new HashMap<>();

    static {
        for (Stat stat : Stat.values()) {
            STAT_BY_NAME.put(stat.getDisplayName(), stat);
        }
    }

    private static final ItemStatManager INSTANCE = new ItemStatManager();

    private ItemStatManager() {}

    public static ItemStatManager getInstance() {
        return INSTANCE;
    }

    /**
     * Appends stat lore lines for the given stats to the item.
     * Existing stat lines (previously written by this manager) are stripped first
     * so re-applying does not duplicate them.
     *
     * @param item  the item to modify; must not be null
     * @param stats the stats to apply; entries with a value of 0 are skipped
     */
    public void applyStats(ItemStack item, Map<Stat, Integer> stats) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(stats, "stats");
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();

        stripStatLines(lore);

        for (Stat stat : Stat.values()) {
            Integer value = stats.get(stat);
            if (value == null || value == 0) continue;
            lore.add(formatStatLine(stat, value));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /**
     * Parses the stat lore lines from the item and returns the stat map.
     * Returns an empty map if the item has no meta or no stat lines.
     *
     * @param item the item to read; must not be null
     * @return a map from {@link Stat} to its value; never null
     */
    public Map<Stat, Integer> getStats(ItemStack item) {
        Objects.requireNonNull(item, "item");
        Map<Stat, Integer> result = new EnumMap<>(Stat.class);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return result;
        List<String> lore = meta.getLore();
        if (lore == null) return result;

        for (String line : lore) {
            if (!line.startsWith(STAT_PREFIX)) continue;
            for (Stat stat : Stat.values()) {
                String expected = STAT_PREFIX + "%d " + stat.getSymbol() + " " + stat.getDisplayName();
                // find the value by matching symbol + name suffix
                String suffix = " " + stat.getSymbol() + " " + stat.getDisplayName();
                if (!line.endsWith(suffix)) continue;
                String valuePart = line.substring(STAT_PREFIX.length(), line.length() - suffix.length());
                try {
                    result.put(stat, Integer.parseInt(valuePart));
                } catch (NumberFormatException ignored) {
                }
                break;
            }
        }

        // Also parse real Hypixel-format stat lines (e.g. "§7Strength: §c+100") so 1:1 items
        // contribute stats too. Only exact stat-name matches count, so non-stat lore is ignored;
        // putIfAbsent keeps any project-format value already parsed above.
        for (String raw : lore) {
            if (raw == null) continue;
            Matcher m = HYPIXEL_STAT.matcher(ChatColor.stripColor(raw).trim());
            if (!m.find()) continue;
            Stat stat = STAT_BY_NAME.get(m.group(1).trim());
            if (stat == null) continue;
            try {
                int value = Integer.parseInt(m.group(2).replace(",", ""));
                if (value != 0) result.putIfAbsent(stat, value);
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    /**
     * Removes all stat lore lines previously written by {@link #applyStats}.
     *
     * @param item the item to modify; must not be null
     */
    public void clearStats(ItemStack item) {
        Objects.requireNonNull(item, "item");
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.getLore();
        if (lore == null) return;
        stripStatLines(lore);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private static String formatStatLine(Stat stat, int value) {
        return STAT_PREFIX + value + " " + stat.getSymbol() + " " + stat.getDisplayName();
    }

    private static void stripStatLines(List<String> lore) {
        lore.removeIf(line -> line != null && line.startsWith(STAT_PREFIX));
    }
}
