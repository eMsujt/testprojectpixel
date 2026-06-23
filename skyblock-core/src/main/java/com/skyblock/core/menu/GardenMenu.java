package com.skyblock.core.menu;

import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.manager.GardenManager.CropType;
import com.skyblock.core.manager.GardenManager.GardenCrop;
import com.skyblock.core.manager.GardenManager.PlotTier;
import com.skyblock.core.manager.GardenManager.VisitorOffer;
import com.skyblock.core.manager.GardenManager.VisitorType;
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
 * 6-row chest GUI titled '§2Garden' extending AbstractSkyBlockMenu.
 *
 * <p>Layout (rows 0-5, 9 columns):
 * <ul>
 *   <li>Row 0: panes with summary item at slot 4.</li>
 *   <li>Row 1: visitor-queue row — one paper offer per queued visitor.</li>
 *   <li>Row 2: crop-plot row — unlock state and {@link PlotTier} per crop.</li>
 *   <li>Row 3: crop-progress row — harvest totals and milestone progress.</li>
 *   <li>Rows 4-5: panes.</li>
 * </ul>
 */
public final class GardenMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§aGarden";
    private static final int ROWS = 6;

    static final int SUMMARY_SLOT = 4;
    /** First slot of the visitor-queue row; offers occupy {@code VISITOR_SLOT .. +8}. */
    static final int VISITOR_SLOT = 9;
    /** First slot of the crop-plot row; the nine crop plots occupy {@code FIRST_PLOT_SLOT .. +8}. */
    static final int FIRST_PLOT_SLOT = 18;
    /** First slot of the crop-progress row; one item per harvestable crop. */
    static final int PROGRESS_SLOT = 27;

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

    /**
     * Representative visitor offers shown in the queue row. The manager tracks
     * only aggregate visitor stats, so this fixed queue illustrates the kind of
     * offers a player receives (visitor, requested crops and copper reward).
     */
    private static final List<VisitorOffer> VISITOR_QUEUE = List.of(
            new VisitorOffer(VisitorType.JACOB, Map.of(GardenCrop.WHEAT, 256), 1_000L),
            new VisitorOffer(VisitorType.BAKER, Map.of(GardenCrop.PUMPKIN, 160), 2_500L),
            new VisitorOffer(VisitorType.FARMING_MERCHANT, Map.of(GardenCrop.CARROT, 320), 4_000L),
            new VisitorOffer(VisitorType.ANITA, Map.of(GardenCrop.MELON, 480), 6_500L),
            new VisitorOffer(VisitorType.GRANDMA_WOLF, Map.of(GardenCrop.SUGAR_CANE, 384), 9_000L));

    public GardenMenu(Player player) {
        super(player, TITLE, ROWS);
    }

    @Override
    protected void populate() {
        UUID playerId = player.getUniqueId();
        GardenManager manager = GardenManager.getInstance();

        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < ROWS * 9; slot++) {
            setItem(slot, pane);
        }

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.JUKEBOX)
                .displayName("§2Garden")
                .lore(
                        "§7Garden Level: §e" + manager.getGardenLevel(playerId)
                                + "§7/§e" + manager.getMaxGardenLevel(),
                        "§7Copper: §c" + manager.getCopper(playerId),
                        "§7Visitors Served: §e" + manager.getCompletedOffers(playerId),
                        "",
                        "§7Unlock and upgrade crop plots to",
                        "§7gain farming fortune.")
                .build());

        buildVisitorQueue();
        buildCropPlots(manager, playerId);
        buildCropProgress(manager, playerId);
    }

    private void buildVisitorQueue() {
        int index = 0;
        for (VisitorOffer offer : VISITOR_QUEUE) {
            if (index >= 9) {
                break;
            }
            List<String> lore = new ArrayList<>();
            lore.add("§7Wants:");
            for (Map.Entry<GardenCrop, Integer> need : offer.getRequiredCrops().entrySet()) {
                lore.add("§8 - §e" + need.getValue() + "x §a" + need.getKey().getDisplayName());
            }
            lore.add("");
            lore.add("§7Reward: §c" + offer.getCopperReward() + " Copper");
            setItem(VISITOR_SLOT + index, new ItemBuilder(Material.PAPER)
                    .displayName("§e" + offer.getVisitor().getDisplayName())
                    .lore(lore)
                    .build());
            index++;
        }
    }

    private void buildCropPlots(GardenManager manager, UUID playerId) {
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

    private void buildCropProgress(GardenManager manager, UUID playerId) {
        int index = 0;
        for (CropType crop : CropType.values()) {
            if (index >= 9) {
                break;
            }
            long harvested = manager.getHarvestCount(playerId, crop);
            int milestone = manager.getCropMilestone(playerId, crop);
            int maxMilestone = manager.getMaxMilestone(crop);
            long untilNext = manager.getCropsUntilNextMilestone(playerId, crop);

            List<String> lore = new ArrayList<>();
            lore.add("§7Harvested: §e" + harvested);
            lore.add("§7Milestone: §e" + milestone + "§7/§e" + maxMilestone);
            if (untilNext > 0L) {
                lore.add("§7Next milestone in §e" + untilNext + " §7crops");
            } else {
                lore.add("§aMax milestone reached");
            }

            Material icon = ICONS.getOrDefault(crop.getGardenCrop(), Material.WHEAT);
            setItem(PROGRESS_SLOT + index, new ItemBuilder(icon)
                    .displayName("§a" + crop.getGardenCrop().getDisplayName())
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
