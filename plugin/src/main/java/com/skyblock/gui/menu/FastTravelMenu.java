package com.skyblock.gui.menu;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FastTravelMenu extends Menu {

    private enum Destination {
        HUB("Hub", 11, Material.GRASS_BLOCK, "/warp hub"),
        PRIVATE_ISLAND("Private Island", 13, Material.OAK_PLANKS, "/warp island"),
        THE_BARN("The Barn", 15, Material.HAY_BLOCK, "/warp barn"),
        MUSHROOM_DESERT("Mushroom Desert", 20, Material.RED_MUSHROOM, "/warp desert"),
        SPIDERS_DEN("Spider's Den", 22, Material.STRING, "/warp spider"),
        BLAZING_FORTRESS("Blazing Fortress", 24, Material.NETHERRACK, "/warp nether"),
        THE_END("The End", 29, Material.ENDER_PEARL, "/warp end"),
        THE_PARK("The Park", 31, Material.JUNGLE_SAPLING, "/warp park"),
        GOLD_MINE("Gold Mine", 33, Material.GOLD_ORE, "/warp goldmine"),
        DEEP_CAVERNS("Deep Caverns", 38, Material.COBBLESTONE, "/warp deepcaverns"),
        DWARVEN_MINES("Dwarven Mines", 40, Material.IRON_PICKAXE, "/warp dwarven"),
        CRYSTAL_HOLLOWS("Crystal Hollows", 42, Material.AMETHYST_SHARD, "/warp crystals");

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
        super("§aFast Travel", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        setItem(4, new ItemBuilder(Material.COMPASS)
                .displayName("§aFast Travel")
                .lore("§7Click a destination to warp there.")
                .build());

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
            if (slot < 9 || slot >= 45) {
                setItem(slot, pane);
            }
        }
    }
}
