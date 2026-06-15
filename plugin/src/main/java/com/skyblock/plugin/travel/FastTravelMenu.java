package com.skyblock.plugin.travel;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The Fast Travel menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §aFast Travel} that lists the
 * available warp destinations across the inner slots, framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border. Each destination is rendered as a
 * {@code COMPASS} icon; clicking one warps the player and closes the menu. A
 * close button sits on the bottom row.</p>
 */
public class FastTravelMenu extends Menu {

    /** A warp destination shown as a compass in the menu. */
    private enum Destination {
        HUB("§aHub", "/warp hub"),
        ISLAND("§aYour Island", "/warp island"),
        THE_BARN("§aThe Barn", "/warp barn"),
        MUSHROOM_DESERT("§aMushroom Desert", "/warp desert"),
        SPIDERS_DEN("§aSpider's Den", "/warp spider"),
        BLAZING_FORTRESS("§aBlazing Fortress", "/warp nether"),
        THE_END("§aThe End", "/warp end"),
        THE_PARK("§aThe Park", "/warp park"),
        GOLD_MINE("§aGold Mine", "/warp mine"),
        DEEP_CAVERNS("§aDeep Caverns", "/warp deepcaverns"),
        DWARVEN_MINES("§aDwarven Mines", "/warp dwarven"),
        CRYSTAL_HOLLOWS("§aCrystal Hollows", "/warp crystals");

        private final String displayName;
        private final String command;

        Destination(String displayName, String command) {
            this.displayName = displayName;
            this.command = command;
        }
    }

    /** Inner slots used to display destinations (rows 2–5, columns 1–7). */
    private static final int[] DESTINATION_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 53;

    public FastTravelMenu() {
        super("§aFast Travel", 6);
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

        Destination[] destinations = Destination.values();
        int count = Math.min(destinations.length, DESTINATION_SLOTS.length);
        for (int i = 0; i < count; i++) {
            Destination destination = destinations[i];
            setItem(DESTINATION_SLOTS[i], new ItemBuilder(Material.COMPASS)
                    .displayName(destination.displayName)
                    .lore("§7Click to warp")
                    .build(), e -> {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().performCommand(destination.command.substring(1));
            });
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> e.getWhoClicked().closeInventory());
    }
}
