package com.skyblock.core.menu;

import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.foraging.ForagingManager.ForagingArea;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class ForagingMenu extends Menu {

    private static final String TITLE = "Foraging";

    private static final int[] AREA_SLOTS = {10, 12, 14, 16, 22};

    private final UUID owner;

    public ForagingMenu(UUID owner) {
        super(TITLE, 6);
        this.owner = owner;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            setItem(slot, pane);
        }

        ForagingManager mgr = ForagingManager.getInstance();
        ForagingArea[] areas = ForagingArea.values();

        for (int i = 0; i < areas.length && i < AREA_SLOTS.length; i++) {
            ForagingArea area = areas[i];
            Material mat = area.getPrimaryTree().getMaterial();
            int chops = mgr.getChops(owner, area.getPrimaryTree());

            setItem(AREA_SLOTS[i], new ItemBuilder(mat)
                    .displayName("§2" + area.getDisplayName())
                    .lore(
                            "§7Chops: §a" + chops,
                            "§7Click to view area details.")
                    .build(),
                    e -> e.setCancelled(true));
        }
    }
}
