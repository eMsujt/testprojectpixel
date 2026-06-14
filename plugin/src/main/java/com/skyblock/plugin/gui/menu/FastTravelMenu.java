package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The Fast Travel menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §9Fast Travel} listing the available
 * warp destinations across the inner slots, framed by a gray glass border. Each
 * destination is rendered as a {@code COMPASS}; clicking one closes the menu and
 * runs the destination's warp command, matching Hypixel's layout.</p>
 */
public class FastTravelMenu extends Menu {

    /** A warp destination shown as a compass in the menu. */
    private enum Destination {
        HUB("Hub", "/warp hub"),
        YOUR_ISLAND("Your Island", "/warp island"),
        THE_BARN("The Barn", "/warp barn"),
        MUSHROOM_DESERT("Mushroom Desert", "/warp desert"),
        SPIDERS_DEN("Spider's Den", "/warp spider"),
        BLAZING_FORTRESS("Blazing Fortress", "/warp nether"),
        THE_END("The End", "/warp end"),
        THE_PARK("The Park", "/warp park"),
        GOLD_MINE("Gold Mine", "/warp mine"),
        DEEP_CAVERNS("Deep Caverns", "/warp deepcaverns"),
        DWARVEN_MINES("Dwarven Mines", "/warp dwarven"),
        CRYSTAL_HOLLOWS("Crystal Hollows", "/warp crystals");

        private final String displayName;
        private final String command;

        Destination(String displayName, String command) {
            this.displayName = displayName;
            this.command = command;
        }
    }

    /** Centred content slots across the middle rows, one per destination. */
    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    public FastTravelMenu() {
        super("§9Fast Travel", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        Destination[] destinations = Destination.values();
        int count = Math.min(destinations.length, SLOTS.length);
        for (int i = 0; i < count; i++) {
            Destination destination = destinations[i];
            setItem(SLOTS[i], new ItemBuilder(Material.COMPASS)
                            .displayName("§a" + destination.displayName)
                            .lore("§7Click to warp to " + destination.displayName + ".")
                            .build(),
                    event -> {
                        event.getWhoClicked().closeInventory();
                        event.getWhoClicked().performCommand(destination.command.substring(1));
                    });
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
