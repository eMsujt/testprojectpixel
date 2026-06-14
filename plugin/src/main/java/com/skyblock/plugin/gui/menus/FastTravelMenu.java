package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The Fast Travel menu.
 *
 * <p>A 54-slot (6-row) menu presenting one icon per warp destination. Clicking a
 * destination icon tells the player where they are warping; the actual teleport
 * is performed by the warp command handlers.</p>
 */
public class FastTravelMenu extends Menu {

    /** A fast-travel destination: its display name, representative icon, and slot. */
    private enum Destination {
        HUB("Hub Island", Material.OAK_LEAVES, 10),
        THE_FARMING_ISLANDS("The Farming Islands", Material.HAY_BLOCK, 11),
        THE_PARK("The Park", Material.JUNGLE_SAPLING, 12),
        SPIDERS_DEN("Spider's Den", Material.STRING, 13),
        THE_END("The End", Material.ENDER_PEARL, 14),
        CRIMSON_ISLE("Crimson Isle", Material.NETHERRACK, 15),
        DEEP_CAVERNS("Deep Caverns", Material.COBBLESTONE, 16),
        DWARVEN_MINES("Dwarven Mines", Material.IRON_ORE, 19),
        CRYSTAL_HOLLOWS("Crystal Hollows", Material.PRISMARINE_CRYSTALS, 20),
        DUNGEON_HUB("Dungeon Hub", Material.IRON_SWORD, 21);

        private final String displayName;
        private final Material icon;
        private final int slot;

        Destination(String displayName, Material icon, int slot) {
            this.displayName = displayName;
            this.icon = icon;
            this.slot = slot;
        }
    }

    public FastTravelMenu() {
        super("§aFast Travel", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (Destination destination : Destination.values()) {
            setItem(destination.slot, new ItemBuilder(destination.icon)
                            .displayName("§a" + destination.displayName)
                            .lore("§7Click to warp to " + destination.displayName + ".")
                            .build(),
                    event -> event.getWhoClicked().sendMessage(
                            "§aWarping to " + destination.displayName + "..."));
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
