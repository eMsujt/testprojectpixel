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
 * destination is rendered as a representative block at its own slot; clicking one
 * closes the menu and runs the destination's warp command, matching Hypixel's
 * layout.</p>
 */
public class FastTravelMenu extends Menu {

    /** A warp destination shown as a block in the menu. */
    private enum Destination {
        THE_HUB("The Hub", Material.GRASS_BLOCK, 13, "/warp hub"),
        THE_BARN("The Barn", Material.HAY_BLOCK, 20, "/warp barn"),
        MUSHROOM_DESERT("Mushroom Desert", Material.RED_MUSHROOM_BLOCK, 21, "/warp desert"),
        SPIDERS_DEN("Spider's Den", Material.COBWEB, 22, "/warp spider"),
        BLAZING_FORTRESS("Blazing Fortress", Material.NETHERRACK, 23, "/warp nether"),
        THE_END("The End", Material.END_STONE, 24, "/warp end"),
        THE_PARK("The Park", Material.OAK_SAPLING, 29, "/warp park"),
        GOLD_MINE("Gold Mine", Material.GOLD_ORE, 30, "/warp mine"),
        DEEP_CAVERNS("Deep Caverns", Material.STONE, 31, "/warp deepcaverns"),
        DWARVEN_MINES("Dwarven Mines", Material.IRON_ORE, 32, "/warp dwarven"),
        CRYSTAL_HOLLOWS("Crystal Hollows", Material.AMETHYST_CLUSTER, 33, "/warp crystals");

        private final String displayName;
        private final Material icon;
        private final int slot;
        private final String command;

        Destination(String displayName, Material icon, int slot, String command) {
            this.displayName = displayName;
            this.icon = icon;
            this.slot = slot;
            this.command = command;
        }
    }

    public FastTravelMenu() {
        super("§6Fast Travel", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (Destination destination : Destination.values()) {
            setItem(destination.slot, new ItemBuilder(destination.icon)
                            .displayName("§a" + destination.displayName)
                            .lore("§7Click to travel to " + destination.displayName + ".")
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
