package com.skyblock.core.menu;

import com.skyblock.core.manager.CalendarManager;
import com.skyblock.core.manager.CalendarManager.SkyBlockMonth;
import com.skyblock.core.manager.GardenManager.GardenCrop;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Canonical "SkyBlock Calendar" menu. A 54-slot (6-row) chest GUI framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border with a clock at slot 4 summarising the
 * current date, season and today's events (from {@link CalendarManager}), and
 * one tile per {@link SkyBlockMonth} showing that season's recurring events.
 */
public final class CalendarMenu extends Menu {

    private static final String TITLE = "§aSkyBlock Calendar";
    private static final int SUMMARY_SLOT = 4;

    /** One tile per month: two centred rows of six. */
    private static final int[] MONTH_SLOTS = {
            10, 11, 12, 13, 14, 15,
            19, 20, 21, 22, 23, 24
    };

    @Override
    protected void build() {
        fillBorder();

        CalendarManager calendar = CalendarManager.getInstance();
        SkyBlockMonth current = calendar.getCurrentMonth();

        List<String> summaryLore = new ArrayList<>();
        summaryLore.add("§7Date: §e" + current.getDisplayName() + " §7day §e" + calendar.getCurrentDayOfMonth());
        summaryLore.add("§7Season: §a" + current.getDisplayName());
        summaryLore.add("");
        List<String> today = calendar.getEventsToday();
        if (today.isEmpty()) {
            summaryLore.add("§7No events today.");
        } else {
            summaryLore.add("§7Today's events:");
            for (String event : today) {
                summaryLore.add("§8 • §f" + event);
            }
        }
        if (calendar.isContestToday()) {
            summaryLore.add("");
            summaryLore.add("§6Jacob's Farming Contest:");
            for (GardenCrop crop : calendar.getGardenCropsToday()) {
                summaryLore.add("§8 • §a" + crop.getDisplayName());
            }
        }
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.CLOCK)
                .displayName("§aToday")
                .lore(summaryLore)
                .build(),
                e -> e.setCancelled(true));

        SkyBlockMonth[] months = SkyBlockMonth.values();
        for (int i = 0; i < months.length && i < MONTH_SLOTS.length; i++) {
            SkyBlockMonth month = months[i];
            boolean isCurrent = month == current;

            List<String> lore = new ArrayList<>();
            List<String> events = eventsInMonth(calendar, month);
            if (events.isEmpty()) {
                lore.add("§7No scheduled events.");
            } else {
                lore.add("§7Events:");
                for (String event : events) {
                    lore.add("§8 • §f" + event);
                }
            }
            if (isCurrent) {
                lore.add("");
                lore.add("§eCurrent season");
            }

            setItem(MONTH_SLOTS[i], new ItemBuilder(isCurrent ? Material.LIME_STAINED_GLASS_PANE : Material.PAPER)
                    .displayName((isCurrent ? "§a" : "§f") + month.getDisplayName())
                    .lore(lore)
                    .build(),
                    e -> e.setCancelled(true));
        }
    }

    /** Collects the distinct recurring events scheduled anywhere in the given month, in date order. */
    private static List<String> eventsInMonth(CalendarManager calendar, SkyBlockMonth month) {
        List<String> events = new ArrayList<>();
        for (int day = 1; day <= CalendarManager.DAYS_PER_MONTH; day++) {
            for (String event : calendar.getEventsOn(month, day)) {
                if (!events.contains(event)) {
                    events.add(event);
                }
            }
        }
        return events;
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }
}
