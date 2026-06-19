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
 * GUI menu opened by {@code /garden}. Renders three rows of Garden state:
 *
 * <ul>
 *   <li>a <b>visitor-queue row</b> of paper offers, each showing the visitor's
 *       name, the crops they want and the copper reward they pay;</li>
 *   <li>the nine <b>crop-plot</b> slots from {@link GardenManager}, showing for
 *       each crop whether its plot is unlocked, its current {@link PlotTier} and
 *       the farming-fortune bonus that tier grants;</li>
 *   <li>a <b>crop-progress row</b> with one item per harvestable crop, showing
 *       the total harvested and the player's milestone progress.</li>
 * </ul>
 *
 * <p>A crop plot is treated as unlocked when its unlock cost is free or the
 * player holds enough copper to unlock it (see
 * {@link GardenManager#getCropPlotUnlockCost(GardenCrop)} and
 * {@link GardenManager#getCopper(UUID)}).</p>
 */
public final class GardenMenu extends Menu {

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

    private final UUID playerId;

    public GardenMenu(Player player) {
        this(player.getUniqueId());
    }

    public GardenMenu(UUID playerId) {
        super("§aGarden", 5);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 9; slot < 18; slot++) setItem(slot, pane);
        for (int slot = 27; slot < 45; slot++) setItem(slot, pane);

        GardenManager manager = GardenManager.getInstance();

        setItem(SUMMARY_SLOT, new ItemBuilder(Material.JUKEBOX)
                .displayName("§aGarden")
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
        buildCropPlots(manager);
        buildCropProgress(manager);
    }

    /** Renders the visitor-queue row: one paper offer per queued visitor. */
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

    /** Renders the nine crop-plot slots with their unlock state and tier. */
    private void buildCropPlots(GardenManager manager) {
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

    /** Renders the crop-progress row: one item per harvestable crop. */
    private void buildCropProgress(GardenManager manager) {
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
