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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * GUI menu opened by {@code /garden}. Renders the nine crop-plot slots from
 * {@link GardenManager}, showing for each crop whether its plot is unlocked,
 * its current {@link PlotTier} and the farming-fortune bonus that tier grants.
 *
 * <p>A crop plot is treated as unlocked when its unlock cost is free or the
 * player holds enough copper to unlock it (see
 * {@link GardenManager#getCropPlotUnlockCost(GardenCrop)} and
 * {@link GardenManager#getCopper(UUID)}).</p>
 */
public final class GardenMenu extends Menu {

    static final int SUMMARY_SLOT = 4;
    /** First slot of the crop-plot row; the nine crop plots occupy {@code FIRST_PLOT_SLOT .. +8}. */
    static final int FIRST_PLOT_SLOT = 18;

    private static final Map<GardenCrop, Material> ICONS = new EnumMap<>(GardenCrop.class);

    static {
        ICONS.put(GardenCrop.WHEAT,       Material.WHEAT);
        ICONS.put(GardenCrop.CARROT,      Material.CARROT);
        ICONS.put(GardenCrop.POTATO,      Material.POTATO);
        ICONS.put(GardenCrop.PUMPKIN,     Material.PUMPKIN);
        ICONS.put(GardenCrop.SUGAR_CANE,  Material.SUGAR_CANE);
        ICONS.put(GardenCrop.MELON,       Material.MELON_SLICE);
        ICONS.put(GardenCrop.CACTUS,      Material.CACTUS);
        ICONS.put(GardenCrop.COCOA_BEANS, Material.COCOA_BEANS);
        ICONS.put(GardenCrop.MUSHROOM,    Material.RED_MUSHROOM);
    }

    private final UUID playerId;

    public GardenMenu(Player player) {
        this(player.getUniqueId());
    }

    public GardenMenu(UUID playerId) {
        super("§aGarden", 4);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 27; slot < 36; slot++) setItem(slot, pane);

        GardenManager manager = GardenManager.getInstance();

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.JUKEBOX)
                .displayName("§aGarden")
                .lore(
                        "§7Garden Level: §e" + manager.getGardenLevel(playerId)
                                + "§7/§e" + manager.getMaxGardenLevel(),
                        "§7Copper: §c" + manager.getCopper(playerId),
                        "",
                        "§7Unlock and upgrade crop plots to",
                        "§7gain farming fortune.")
                .build());

        int index = 0;
        for (GardenCrop crop : GardenCrop.values()) {
            Long unlockCost = GardenManager.CROP_PLOT_UNLOCK_COSTS.get(crop);
            if (unlockCost == null) {
                continue;
            }
            boolean unlocked = unlockCost <= 0L || manager.getCopper(playerId) >= unlockCost;
            PlotTier tier = manager.getCropPlotTier(playerId, crop);

            List<String> lore = new ArrayList<>();
            if (unlocked) {
                lore.add("§aUnlocked");
                lore.add("§7Tier: §e" + tier.getDisplayName());
                lore.add("§7Farming Fortune: §6+" + manager.getCropFarmingFortune(playerId, crop));
            } else {
                lore.add("§cLocked");
                lore.add("§7Unlock cost: §c" + unlockCost + " Copper");
            }

            Material icon = unlocked ? ICONS.getOrDefault(crop, Material.WHEAT) : Material.GRAY_DYE;
            setItem(FIRST_PLOT_SLOT + index, new ItemBuilder(icon)
                    .displayName((unlocked ? "§a" : "§7") + crop.getDisplayName())
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
