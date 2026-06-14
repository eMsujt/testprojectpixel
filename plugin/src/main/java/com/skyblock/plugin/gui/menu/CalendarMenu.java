package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The Calendar &amp; Events menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §bCalendar &amp; Events} listing the
 * recurring SkyBlock events across the inner slots, framed by a gray glass
 * border. Each event is rendered with a thematic icon describing when it occurs,
 * matching Hypixel's layout.</p>
 */
public class CalendarMenu extends Menu {

    /** A recurring SkyBlock event shown in the menu. */
    private enum Event {
        NEW_YEAR("New Year Celebration", Material.CAKE, "Late Winter, year start"),
        SEASON_OF_JERRY("Season of Jerry", Material.SNOW_BLOCK, "Early Winter"),
        SPOOKY_FESTIVAL("Spooky Festival", Material.JACK_O_LANTERN, "Autumn, every 3rd year-day"),
        TRAVELING_ZOO("Traveling Zoo", Material.HAY_BLOCK, "Spring & Autumn"),
        DARK_AUCTION("Dark Auction", Material.ENDER_EYE, "Every hour"),
        JACOBS_FARMING("Jacob's Farming Contest", Material.GOLDEN_HOE, "Every 3rd year-day"),
        MINING_FIESTA("Mining Fiesta", Material.IRON_PICKAXE, "Occasional"),
        FISHING_FESTIVAL("Fishing Festival", Material.FISHING_ROD, "Full moon"),
        MAYOR_ELECTION("Mayor Election", Material.PAPER, "End of each year");

        private final String displayName;
        private final Material icon;
        private final String when;

        Event(String displayName, Material icon, String when) {
            this.displayName = displayName;
            this.icon = icon;
            this.when = when;
        }
    }

    /** Centred content slots across the middle rows, one per event. */
    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    public CalendarMenu() {
        super("§bCalendar & Events", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        Event[] events = Event.values();
        int count = Math.min(events.length, SLOTS.length);
        for (int i = 0; i < count; i++) {
            Event event = events[i];
            setItem(SLOTS[i], new ItemBuilder(event.icon)
                    .displayName("§a" + event.displayName)
                    .lore("§7" + event.when)
                    .build());
        }
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
