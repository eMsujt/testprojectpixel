package com.skyblock.core.menu;

import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.manager.GardenManager.GardenCrop;
import com.skyblock.core.manager.GardenManager.PlotTier;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class GardenMenu extends Menu {

    /** The 9 crop-plot slots, in display order, mapped to their representative material. */
    static final Map<GardenCrop, Material> CROP_MATERIALS = new LinkedHashMap<>();

    static {
        CROP_MATERIALS.put(GardenCrop.WHEAT,       Material.WHEAT);
        CROP_MATERIALS.put(GardenCrop.CARROT,      Material.CARROT);
        CROP_MATERIALS.put(GardenCrop.POTATO,      Material.POTATO);
        CROP_MATERIALS.put(GardenCrop.PUMPKIN,     Material.PUMPKIN);
        CROP_MATERIALS.put(GardenCrop.SUGAR_CANE,  Material.SUGAR_CANE);
        CROP_MATERIALS.put(GardenCrop.MELON,       Material.MELON_SLICE);
        CROP_MATERIALS.put(GardenCrop.CACTUS,      Material.CACTUS);
        CROP_MATERIALS.put(GardenCrop.COCOA_BEANS, Material.COCOA_BEANS);
        CROP_MATERIALS.put(GardenCrop.MUSHROOM,    Material.RED_MUSHROOM);
    }

    /** Middle-row slots (row 1 of 3) where the 9 crop items are placed. */
    static final int FIRST_CROP_SLOT = 9;

    private final UUID playerId;

    public GardenMenu(Player player) {
        this(player.getUniqueId());
    }

    public GardenMenu(UUID playerId) {
        super("§2Garden", 3);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 18; slot < 27; slot++) setItem(slot, pane);

        GardenManager manager = GardenManager.getInstance();
        int index = 0;
        for (Map.Entry<GardenCrop, Material> entry : CROP_MATERIALS.entrySet()) {
            GardenCrop crop = entry.getKey();
            Material mat = entry.getValue();
            long unlockCost = GardenManager.CROP_PLOT_UNLOCK_COSTS.getOrDefault(crop, 0L);
            PlotTier tier = manager.getCropPlotTier(playerId, crop);
            int fortune = manager.getCropFarmingFortune(playerId, crop);

            List<String> lore = new ArrayList<>();
            lore.add("§7Tier: §e" + tier.getDisplayName());
            lore.add("§7Farming Fortune: §a+" + fortune);
            if (unlockCost > 0) {
                lore.add("§7Unlock Cost: §6" + unlockCost + " Copper");
            }

            setItem(FIRST_CROP_SLOT + index, new ItemBuilder(mat)
                    .displayName("§a" + crop.getDisplayName())
                    .lore(lore)
                    .build());
            index++;
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
