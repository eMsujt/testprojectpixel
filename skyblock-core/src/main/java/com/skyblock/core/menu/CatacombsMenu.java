package com.skyblock.core.menu;

import com.skyblock.core.manager.CatacombsManager.Floor;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The "Catacombs Gate" floor-selection menu (talk to Mort). Laid out 1:1 with
 * Hypixel: the normal floors (Entrance, F1–F7) on slots 11–15 / 21–23, a Master
 * Mode toggle at slot 40 that swaps the grid to M1–M7, and the bottom-row nav
 * (Dungeon Classes 45, Find a Party 48, Auto Ready Up 50, Catacombs Profile 52,
 * Rules and Tips 53).
 */
public final class CatacombsMenu extends Menu {

    private static final String TITLE = "Catacombs Gate";

    /** Normal floors: Entrance, F1–F7 (8 tiles). */
    private static final int[] NORMAL_SLOTS = {11, 12, 13, 14, 15, 21, 22, 23};
    /** Master floors: M1–M7 (7 tiles; no master Entrance). */
    private static final int[] MASTER_SLOTS = {12, 13, 14, 15, 21, 22, 23};

    private final UUID owner;
    private final boolean master;

    public CatacombsMenu(UUID owner) {
        this(owner, false);
    }

    public CatacombsMenu(UUID owner, boolean master) {
        super(TITLE, 6);
        this.owner = owner;
        this.master = master;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) setItem(slot, pane);

        int[] slots = master ? MASTER_SLOTS : NORMAL_SLOTS;
        Floor[] floors = master ? masterFloors() : normalFloors();
        for (int i = 0; i < floors.length && i < slots.length; i++) {
            Floor floor = floors[i];
            setItem(slots[i], new ItemBuilder(
                    master ? Material.NETHER_STAR : Material.WITHER_SKELETON_SKULL)
                    .displayName((master ? "§c" : "§a") + floor.getDisplayName())
                    .lore("§7Click to view floor details.")
                    .build(),
                    e -> e.setCancelled(true));
        }

        // Master Mode toggle.
        setItem(40, new ItemBuilder(master ? Material.NETHER_STAR : Material.TRIPWIRE_HOOK)
                .displayName("§cMaster Mode " + (master ? "§a(ON)" : "§7(OFF)"))
                .lore("§7Toggle Master Mode floors.", "", "§eClick to toggle!")
                .build(),
                e -> { e.setCancelled(true); new CatacombsMenu(owner, !master).open((Player) e.getWhoClicked()); });

        // Bottom navigation.
        setItem(45, new ItemBuilder(Material.NETHER_STAR)
                .displayName("§aDungeon Classes")
                .lore("§7Select your dungeon class.")
                .build(),
                e -> { e.setCancelled(true); new DungeonClassMenu((Player) e.getWhoClicked()).open((Player) e.getWhoClicked()); });
        setItem(48, new ItemBuilder(Material.REDSTONE_BLOCK)
                .displayName("§aFind a Party")
                .lore("§7Find players to dungeon with.")
                .build(), e -> e.setCancelled(true));
        setItem(50, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .displayName("§aSearch Settings")
                .lore("§7Configure your party-finder search.")
                .build(), e -> e.setCancelled(true));
        setItem(52, new ItemBuilder(Material.OAK_SIGN)
                .displayName("§aCatacombs Profile")
                .lore("§7View your Catacombs profile.")
                .build(), e -> e.setCancelled(true));
        setItem(53, new ItemBuilder(Material.REDSTONE_TORCH)
                .displayName("§aRules and Tips")
                .lore("§7Dungeon rules and tips.")
                .build(), e -> e.setCancelled(true));
    }

    private static Floor[] normalFloors() {
        List<Floor> list = new ArrayList<>();
        for (Floor f : Floor.values()) if (!f.name().startsWith("M")) list.add(f);
        return list.toArray(new Floor[0]);
    }

    private static Floor[] masterFloors() {
        List<Floor> list = new ArrayList<>();
        for (Floor f : Floor.values()) if (f.name().startsWith("M")) list.add(f);
        return list.toArray(new Floor[0]);
    }
}
