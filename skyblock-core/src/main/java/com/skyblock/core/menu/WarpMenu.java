package com.skyblock.core.menu;

import com.skyblock.core.manager.Warp;
import com.skyblock.core.manager.WarpManager;
import com.skyblock.core.manager.WarpManager.WarpLocation;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * 6-row chest GUI titled '§bFast Travel'. Places each {@link WarpLocation} as a
 * themed icon starting at slot 9 (below a cyan-pane border row). Clicking
 * teleports the player if the warp is registered, otherwise sends an error.
 */
public final class WarpMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§bFast Travel";
    private static final int CLOSE_SLOT = 53;

    /** Themed icon for each WarpLocation in declaration order. */
    private static final Material[] WARP_ICONS = {
        Material.BEACON,            // HUB
        Material.HAY_BLOCK,         // FARMING_1 (Barn)
        Material.BROWN_MUSHROOM,    // FARMING_2 (Mushroom Desert)
        Material.GOLD_ORE,          // MINING_1 (Gold Mine)
        Material.DEEPSLATE,         // MINING_2 (Deep Caverns)
        Material.IRON_PICKAXE,      // MINING_3 (Dwarven Mines)
        Material.OAK_SAPLING,       // FORAGING_1 (The Park)
        Material.COBWEB,            // COMBAT_1 (Spider's Den)
        Material.BLAZE_ROD,         // COMBAT_2 (Blazing Fortress)
        Material.END_STONE,         // COMBAT_3 (The End)
        Material.AMETHYST_SHARD,    // CRYSTAL_HOLLOWS
        Material.NETHERRACK,        // CRIMSON_ISLE
        Material.CHORUS_FLOWER,     // THE_RIFT
        Material.BRICK,             // DUNGEON_HUB
    };

    public WarpMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) {
            setItem(slot, pane);
        }
        for (int slot = 45; slot < 53; slot++) {
            setItem(slot, pane);
        }

        WarpManager manager = WarpManager.getInstance();
        WarpLocation[] locations = WarpLocation.values();

        for (int i = 0; i < locations.length; i++) {
            WarpLocation location = locations[i];
            Optional<Warp> warp = manager.getWarp(location);
            boolean registered = warp.isPresent();
            Material icon = i < WARP_ICONS.length ? WARP_ICONS[i] : Material.ENDER_PEARL;

            setItem(9 + i, new ItemBuilder(icon)
                    .displayName("§b" + location.getDisplayName())
                    .lore(
                            "§7Warp to §b" + location.getDisplayName() + "§7.",
                            "",
                            registered ? "§eClick to warp!" : "§cNot available")
                    .build(),
                    event -> {
                        event.setCancelled(true);
                        if (registered) {
                            player.closeInventory();
                            player.teleport(warp.get().toLocation());
                            player.sendMessage("§aWarped to §b" + location.getDisplayName() + "§a.");
                        } else {
                            player.sendMessage("§cThat warp is not available yet.");
                        }
                    });
        }

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Click to close.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                });
    }
}
