package com.skyblock.plugin.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Calendar &amp; Events menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §eCalendar &amp; Events}. The current
 * SkyBlock date is computed from the system clock using Hypixel timing (a
 * SkyBlock day lasts 20 real minutes, a month is 31 days and a year is 12
 * months) and shown on a {@code CLOCK} at slot 4. The twelve SkyBlock months
 * are laid out across the first two inner rows, framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border, with a close button on the bottom
 * row.</p>
 */
public class CalendarMenu extends Menu {

    /** Real milliseconds per SkyBlock day (20 minutes). */
    private static final long DAY_MS = 20L * 60L * 1000L;

    /** SkyBlock days per month. */
    private static final int DAYS_PER_MONTH = 31;

    /** SkyBlock months per year. */
    private static final int MONTHS_PER_YEAR = 12;

    /** The twelve SkyBlock month names, in order. */
    private static final String[] MONTHS = {
            "Early Spring", "Spring", "Late Spring",
            "Early Summer", "Summer", "Late Summer",
            "Early Autumn", "Autumn", "Late Autumn",
            "Early Winter", "Winter", "Late Winter"
    };

    /** A notable event per month, or {@code null} when the month has none. */
    private static final String[] EVENTS = {
            "Traveling Zoo", null, null,
            null, null, null,
            null, "Spooky Festival", null,
            "Traveling Zoo", "Jerry's Workshop", "New Year Celebration"
    };

    /** Inner slots holding the twelve months (first two inner rows). */
    private static final int[] MONTH_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22
    };

    /** Slot for the current-date clock. */
    private static final int DATE_SLOT = 4;

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 53;

    public CalendarMenu(Player player) {
        super("§eCalendar & Events", 6);
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }

        long elapsedDays = System.currentTimeMillis() / DAY_MS;
        int dayOfYear = (int) (elapsedDays % (DAYS_PER_MONTH * MONTHS_PER_YEAR));
        int currentMonth = dayOfYear / DAYS_PER_MONTH;
        int currentDay = dayOfYear % DAYS_PER_MONTH + 1;

        setItem(DATE_SLOT, new ItemBuilder(Material.CLOCK)
                .displayName("§eCurrent Date")
                .lore("§7" + MONTHS[currentMonth] + " " + ordinal(currentDay))
                .build());

        for (int i = 0; i < MONTHS.length; i++) {
            boolean current = i == currentMonth;
            String event = EVENTS[i];
            setItem(MONTH_SLOTS[i], new ItemBuilder(current ? Material.MAP : Material.PAPER)
                    .displayName((current ? "§a" : "§e") + MONTHS[i])
                    .lore(event != null ? "§6Event: §f" + event : "§7No events")
                    .build());
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }

    /** Formats a day number with its ordinal suffix (e.g. {@code 1} → {@code 1st}). */
    private static String ordinal(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
            case 1: return day + "st";
            case 2: return day + "nd";
            case 3: return day + "rd";
            default: return day + "th";
        }
    }
}
