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
 * The Fast Travel menu, matching the wiki {@code {{UI|Fast Travel}}} layout: the
 * real destinations at their documented slots and colours (Private Island /
 * SkyBlock Hub in aqua, the rest green, Jerry's Workshop red), with an Island
 * Browser / Go Back / Advanced Mode footer.
 *
 * <p>Each destination teleports if a warp is registered in {@link WarpManager};
 * destinations whose zones aren't generated in-world yet show "Not available
 * yet". (Real teleport targets land with worldgen — Phase 6.)</p>
 */
public final class WarpMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "Fast Travel";

    /** A Fast-Travel destination: slot, name, colour code, icon, and the warp it maps to (nullable). */
    private record Dest(int slot, String name, String color, Material icon, WarpLocation warp) {
    }

    private static final Dest[] DESTS = {
            new Dest(10, "Private Island",   "§b", Material.GRASS_BLOCK,    null),
            new Dest(11, "SkyBlock Hub",     "§b", Material.BEACON,         WarpLocation.HUB),
            new Dest(12, "Dungeon Hub",      "§a", Material.OAK_DOOR,       WarpLocation.DUNGEON_HUB),
            new Dest(13, "The Barn",         "§a", Material.HAY_BLOCK,      WarpLocation.FARMING_1),
            new Dest(14, "The Park",         "§a", Material.OAK_SAPLING,    WarpLocation.FORAGING_1),
            new Dest(15, "Galatea",          "§a", Material.AZALEA,         null),
            new Dest(16, "Gold Mine",        "§a", Material.GOLD_ORE,       WarpLocation.MINING_1),
            new Dest(19, "Deep Caverns",     "§a", Material.DEEPSLATE,      WarpLocation.MINING_2),
            new Dest(20, "Dwarven Mines",    "§a", Material.IRON_PICKAXE,   WarpLocation.MINING_3),
            new Dest(21, "Crystal Hollows",  "§a", Material.AMETHYST_SHARD, WarpLocation.CRYSTAL_HOLLOWS),
            new Dest(22, "Spider's Den",     "§a", Material.COBWEB,         WarpLocation.COMBAT_1),
            new Dest(23, "The End",          "§a", Material.END_STONE,      WarpLocation.COMBAT_3),
            new Dest(24, "Crimson Isle",     "§a", Material.NETHERRACK,     WarpLocation.CRIMSON_ISLE),
            new Dest(25, "The Garden",       "§a", Material.JUNGLE_SAPLING, null),
            new Dest(29, "The Rift",         "§a", Material.CHORUS_FLOWER,  WarpLocation.THE_RIFT),
            new Dest(30, "Backwater Bayou",  "§a", Material.LILY_PAD,       null),
            new Dest(32, "Lotus Atoll",      "§a", Material.SEA_PICKLE,     null),
            new Dest(33, "Jerry's Workshop", "§c", Material.SNOW_BLOCK,     null),
    };

    public WarpMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        WarpManager manager = WarpManager.getInstance();
        for (Dest dest : DESTS) {
            Optional<Warp> warp = dest.warp() == null ? Optional.empty() : manager.getWarp(dest.warp());
            boolean available = warp.isPresent();
            setItem(dest.slot(), new ItemBuilder(dest.icon())
                    .displayName(dest.color() + dest.name())
                    .lore("§7Warp to " + dest.color() + dest.name() + "§7.",
                            "",
                            available ? "§eClick to warp!" : "§cNot available yet")
                    .build(),
                    e -> {
                        e.setCancelled(true);
                        if (available) {
                            player.closeInventory();
                            player.teleport(warp.get().toLocation());
                            player.sendMessage("§aWarped to " + dest.color() + dest.name() + "§a.");
                        } else {
                            player.sendMessage("§c" + dest.name() + " isn't available yet.");
                        }
                    });
        }

        // Footer (wiki): Island Browser (45), Go Back (48), Close (49), Advanced Mode (50).
        setItem(45, new ItemBuilder(Material.BLAZE_POWDER)
                .displayName("§aIsland Browser")
                .lore("§7Visit other players' islands.")
                .build(), e -> e.setCancelled(true));
        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(), e -> {
                    e.setCancelled(true);
                    new SkyBlockMenu(player).open(player);
                });
        setItem(49, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(), e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                });
        setItem(50, new ItemBuilder(Material.LIME_DYE)
                .displayName("§aAdvanced Mode")
                .lore("§7Toggle the advanced warp menu.")
                .build(), e -> e.setCancelled(true));
    }
}
