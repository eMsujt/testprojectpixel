package com.skyblock.core.menu;

import com.skyblock.core.manager.CatacombsManager;
import com.skyblock.core.manager.CatacombsManager.Floor;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 6-row Catacombs overview menu backed by {@link CatacombsManager}.
 *
 * <p>The top rows list every dungeon {@link Floor} (ENTRANCE, F1–F7, M1–M7)
 * as a single tile: normal floors are wither-skeleton skulls, master-mode
 * floors are nether stars. The edges are purple-pane borders.</p>
 */
public final class CatacombsMenu extends Menu {

    private static final String TITLE = "§5The Catacombs";

    /** First inventory slot used for a floor tile; floors fill sequentially. */
    private static final int FIRST_FLOOR_SLOT = 9;

    private final UUID owner;

    public CatacombsMenu(UUID owner) {
        super(TITLE, 6);
        this.owner = owner;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        CatacombsManager.getInstance();

        Floor[] floors = Floor.values();
        for (int i = 0; i < floors.length; i++) {
            Floor floor = floors[i];
            boolean master = floor.name().startsWith("M");

            setItem(contentSlot(i), new ItemBuilder(
                    master ? Material.NETHER_STAR : Material.WITHER_SKELETON_SKULL)
                    .displayName((master ? "§5" : "§f") + floor.getDisplayName())
                    .lore("§7Click to view floor details.")
                    .build(),
                    e -> e.setCancelled(true));
        }
    }
}
