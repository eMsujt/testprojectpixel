package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.calendar.SkyBlockCalendar;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CalendarMenu extends Menu {

    private static final String[][] EVENTS = {
            {"Spooky Festival", "JACK_O_LANTERN"},
            {"Jerry's Workshop", "SNOW_BLOCK"},
            {"Season of Jerry", "SNOWBALL"},
            {"New Year Celebration", "CAKE"},
            {"Traveling Zoo", "HAY_BLOCK"},
            {"Dark Auction", "SKELETON_SKULL"},
            {"Fishing Festival", "FISHING_ROD"},
            {"Mining Fiesta", "IRON_PICKAXE"},
            {"Election Over", "PAPER"}
    };

    private static final int[] SLOTS = {28, 29, 30, 31, 32, 33, 34, 37, 38};

    public CalendarMenu() {
        super("§bCalendar & Events", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        long tick = SkyBlockCalendar.getTickOfDay();
        int hour = (int) ((tick / 1000 + 6) % 24);
        int minute = (int) (tick % 1000 * 60 / 1000);
        String time = String.format("%02d:%02d %s", hour % 12 == 0 ? 12 : hour % 12, minute, hour < 12 ? "AM" : "PM");

        setItem(4, new ItemBuilder(Material.CLOCK)
                .displayName("§eSkyBlock Calendar")
                .lore(
                        "§7Day: §e" + SkyBlockCalendar.getDayOfMonth(),
                        "§7Month: §e" + SkyBlockCalendar.getMonthName(),
                        "§7Year: §e" + SkyBlockCalendar.getYear(),
                        "§7Time: §e" + time,
                        "",
                        "§e" + SkyBlockCalendar.currentSkyBlockDate())
                .build());

        for (int i = 0; i < EVENTS.length; i++) {
            setItem(SLOTS[i], new ItemBuilder(Material.valueOf(EVENTS[i][1]))
                    .displayName("§b" + EVENTS[i][0])
                    .lore("§7A recurring SkyBlock event.")
                    .build());
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }
    }
}
