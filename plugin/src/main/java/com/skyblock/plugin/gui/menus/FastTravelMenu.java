package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;

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
        HUB("Hub", Material.NETHER_STAR, 20),
        SPIDERS_DEN("Spider's Den", Material.STRING, 21),
        THE_END("The End", Material.ENDER_PEARL, 22),
        CRIMSON_ISLE("Crimson Isle", Material.NETHERRACK, 23),
        DWARVEN_MINES("Dwarven Mines", Material.IRON_ORE, 24);

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
        super("Fast Travel", 6);
    }

    @Override
    protected void build() {
        for (Destination destination : Destination.values()) {
            setItem(destination.slot, new ItemBuilder(destination.icon)
                            .displayName("§a" + destination.displayName)
                            .lore("§7Click to warp to " + destination.displayName + ".")
                            .build(),
                    event -> event.getWhoClicked().sendMessage(
                            "§aWarping to " + destination.displayName + "..."));
        }
    }
}
