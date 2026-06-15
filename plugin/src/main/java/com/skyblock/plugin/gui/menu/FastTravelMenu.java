package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FastTravelMenu extends Menu {

    private enum Destination {
        HUB("Hub", 13, Material.COMPASS, "/warp hub"),
        PRIVATE_ISLAND("Private Island", 11, Material.COMPASS, "/warp island"),
        THE_BARN("The Barn", 15, Material.COMPASS, "/warp barn"),
        MUSHROOM_DESERT("Mushroom Desert", 20, Material.COMPASS, "/warp desert"),
        SPIDERS_DEN("Spider's Den", 22, Material.COMPASS, "/warp spider"),
        BLAZING_FORTRESS("Blazing Fortress", 24, Material.COMPASS, "/warp nether"),
        THE_END("The End", 29, Material.COMPASS, "/warp end"),
        THE_PARK("The Park", 31, Material.COMPASS, "/warp park"),
        GOLD_MINE("Gold Mine", 33, Material.COMPASS, "/warp goldmine"),
        DEEP_CAVERNS("Deep Caverns", 38, Material.COMPASS, "/warp deepcaverns"),
        DWARVEN_MINES("Dwarven Mines", 40, Material.COMPASS, "/warp dwarven"),
        CRYSTAL_HOLLOWS("Crystal Hollows", 42, Material.COMPASS, "/warp crystals");

        private final String displayName;
        private final int slot;
        private final Material material;
        private final String command;

        Destination(String displayName, int slot, Material material, String command) {
            this.displayName = displayName;
            this.slot = slot;
            this.material = material;
            this.command = command;
        }
    }

    public FastTravelMenu() {
        super("§bFast Travel", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (Destination destination : Destination.values()) {
            setItem(destination.slot, new ItemBuilder(destination.material)
                            .displayName("§a" + destination.displayName)
                            .lore("§7Click to warp to " + destination.displayName + ".")
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
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
