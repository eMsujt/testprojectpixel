package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FastTravelMenu extends Menu {

    private enum Destination {
        THE_HUB("The Hub", Material.COMPASS, 13, "/warp hub"),
        THE_BARN("The Barn", Material.HAY_BLOCK, 20, "/warp barn"),
        MUSHROOM_DESERT("Mushroom Desert", Material.RED_MUSHROOM, 21, "/warp desert"),
        SPIDERS_DEN("Spider's Den", Material.COBWEB, 22, "/warp spider"),
        BLAZING_FORTRESS("Blazing Fortress", Material.NETHERRACK, 23, "/warp nether"),
        THE_END("The End", Material.END_STONE, 24, "/warp end"),
        THE_PARK("The Park", Material.OAK_LEAVES, 29, "/warp park"),
        GOLD_MINE("Gold Mine", Material.GOLD_ORE, 30, "/warp mine"),
        DEEP_CAVERNS("Deep Caverns", Material.STONE, 31, "/warp deepcaverns"),
        DWARVEN_MINES("Dwarven Mines", Material.IRON_ORE, 32, "/warp dwarven"),
        CRYSTAL_HOLLOWS("Crystal Hollows", Material.AMETHYST_BLOCK, 33, "/warp crystals");

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
        super("§dFast Travel", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (Destination destination : Destination.values()) {
            setItem(destination.slot, new ItemBuilder(destination.icon)
                            .displayName("§d" + destination.displayName)
                            .lore("§7Click to travel to " + destination.displayName + ".")
                            .build(),
                    event -> {
                        event.getWhoClicked().closeInventory();
                        event.getWhoClicked().performCommand(destination.command.substring(1));
                    });
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
