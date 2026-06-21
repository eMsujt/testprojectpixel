package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking each player's Garden plot level, visitor count, and crop upgrade levels.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class GardenManager {

    /** Individual purchasable plots in the Garden. */
    public enum GardenPlot {
        PLOT_1("Plot 1"),
        PLOT_2("Plot 2"),
        PLOT_3("Plot 3"),
        PLOT_4("Plot 4"),
        PLOT_5("Plot 5"),
        PLOT_6("Plot 6"),
        PLOT_7("Plot 7"),
        PLOT_8("Plot 8"),
        PLOT_9("Plot 9");

        private final String displayName;

        GardenPlot(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** NPC visitors that can arrive at the Garden. */
    public enum VisitorType {
        JACOB("Jacob"),
        GUNTHER("Gunther"),
        ANITA("Anita"),
        BAKER("Baker"),
        BANKER("Banker"),
        CARPENTER("Carpenter"),
        DWARVEN_ARTIFICER("Dwarven Artificer"),
        ELIZABETH("Elizabeth"),
        FARMING_MERCHANT("Farming Merchant"),
        GRANDMA_WOLF("Grandma Wolf"),
        JOYFUL_VILLAGER("Joyful Villager"),
        LAZY_MINER("Lazy Miner"),
        PHILLIP("Phillip"),
        SHADY_VILLAGER("Shady Villager"),
        TIA_THE_FAIRY("Tia the Fairy"),
        TOOLSMITH("Toolsmith"),
        WANDERING_TRADER("Wandering Trader");

        private final String displayName;

        VisitorType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Crops that can be upgraded in the Garden. */
    public enum GardenCrop {
        WHEAT("Wheat"),
        CARROT("Carrot"),
        POTATO("Potato"),
        MELON("Melon"),
        PUMPKIN("Pumpkin"),
        SUGAR_CANE("Sugar Cane"),
        COCOA_BEANS("Cocoa Beans"),
        CACTUS("Cactus"),
        MUSHROOM("Mushroom"),
        NETHER_WART("Nether Wart"),
        CABBAGE("Cabbage"),
        COARSE_POTATO("Coarse Potato");

        private final String displayName;

        GardenCrop(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Harvestable crop types with their base yield per harvest action.
     * Yield is multiplied by {@code (1 + cropUpgradeLevel)} at harvest time.
     */
    public enum CropType {
        WHEAT(1,    GardenCrop.WHEAT),
        CARROT(3,   GardenCrop.CARROT),
        POTATO(3,   GardenCrop.POTATO),
        PUMPKIN(1,  GardenCrop.PUMPKIN),
        MELON(4,    GardenCrop.MELON),
        SUGARCANE(2, GardenCrop.SUGAR_CANE),
        COCOA(3,    GardenCrop.COCOA_BEANS),
        MUSHROOM(1, GardenCrop.MUSHROOM);

        private final int baseYield;
        private final GardenCrop gardenCrop;

        CropType(int baseYield, GardenCrop gardenCrop) {
            this.baseYield = baseYield;
            this.gardenCrop = gardenCrop;
        }

        public int getBaseYield() {
            return baseYield;
        }

        public GardenCrop getGardenCrop() {
            return gardenCrop;
        }
    }

    /** Upgrade tiers for a crop plot in the Garden, each granting a farming-fortune bonus. */
    public enum PlotTier {
        TIER_1("Tier I",   1, 0),
        TIER_2("Tier II",  2, 25),
        TIER_3("Tier III", 3, 50),
        TIER_4("Tier IV",  4, 75),
        TIER_5("Tier V",   5, 100);

        private final String displayName;
        private final int tier;
        private final int farmingFortune;

        PlotTier(String displayName, int tier, int farmingFortune) {
            this.displayName = displayName;
            this.tier = tier;
            this.farmingFortune = farmingFortune;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getTier() {
            return tier;
        }

        /** Farming-fortune bonus granted by this plot tier. */
        public int getFarmingFortune() {
            return farmingFortune;
        }

        /** Returns the next tier, or {@code null} if already at max. */
        public PlotTier next() {
            int next = ordinal() + 1;
            PlotTier[] values = values();
            return next < values.length ? values[next] : null;
        }
    }

    /** Medal tiers awarded for placement in a Jacob's Farming Contest. */
    public enum ContestMedal {
        NONE("None"),
        BRONZE("Bronze"),
        SILVER("Silver"),
        GOLD("Gold"),
        PLATINUM("Platinum"),
        DIAMOND("Diamond");

        private final String displayName;

        ContestMedal(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final GardenManager INSTANCE = new GardenManager();

    /** Static metadata for each SkyBlock garden crop: {baseYield, maxUpgradeLevel, milestoneCount}. */
    public static final Map<String, int[]> CROP_DATA;
    static {
        Map<String, int[]> m = new HashMap<>();
        m.put("WHEAT",       new int[]{1, 12, 25});
        m.put("CARROT",      new int[]{3, 12, 25});
        m.put("POTATO",      new int[]{3, 12, 25});
        m.put("MELON",       new int[]{4, 12, 25});
        m.put("PUMPKIN",     new int[]{1, 12, 25});
        m.put("SUGAR_CANE",  new int[]{2, 12, 25});
        m.put("COCOA_BEANS", new int[]{3, 12, 25});
        m.put("CACTUS",      new int[]{2, 12, 25});
        m.put("MUSHROOM",    new int[]{1, 12, 25});
        CROP_DATA = Collections.unmodifiableMap(m);
    }

    /**
     * Copper cost to unlock each of the 9 crop-plot slots.  The first crop is
     * free; subsequent slots scale in price.
     */
    public static final Map<GardenCrop, Long> CROP_PLOT_UNLOCK_COSTS;
    static {
        Map<GardenCrop, Long> m = new EnumMap<>(GardenCrop.class);
        m.put(GardenCrop.WHEAT,       0L);
        m.put(GardenCrop.CARROT,      1_000L);
        m.put(GardenCrop.POTATO,      2_000L);
        m.put(GardenCrop.PUMPKIN,     4_000L);
        m.put(GardenCrop.SUGAR_CANE,  8_000L);
        m.put(GardenCrop.MELON,       16_000L);
        m.put(GardenCrop.CACTUS,      32_000L);
        m.put(GardenCrop.COCOA_BEANS, 64_000L);
        m.put(GardenCrop.MUSHROOM,    128_000L);
        CROP_PLOT_UNLOCK_COSTS = Collections.unmodifiableMap(m);
    }

    /** Per-player garden plot level (1–24). */
    private final Map<UUID, Integer> plotLevels = new HashMap<>();

    /** Per-player total visitor count. */
    private final Map<UUID, Integer> visitorCounts = new HashMap<>();

    /** Per-player crop upgrade levels indexed by GardenCrop ordinal. */
    private final Map<UUID, int[]> cropUpgrades = new HashMap<>();

    /** Per-player set of unlocked garden plots. */
    private final Map<UUID, Set<GardenPlot>> unlockedPlots = new HashMap<>();

    /** Per-player crop plot tiers. */
    private final Map<UUID, Map<GardenCrop, PlotTier>> cropPlotTiers = new HashMap<>();

    /** Per-player total crops harvested per CropType. */
    private final Map<UUID, Map<CropType, Long>> harvestCounts = new HashMap<>();

    /** Per-player farming-fortune stat (percent bonus crop yield). */
    private final Map<UUID, Integer> farmingFortune = new HashMap<>();

    /** Per-player count of Jacob's contest medals earned, indexed by ContestMedal ordinal. */
    private final Map<UUID, int[]> contestMedals = new HashMap<>();

    /** Per-player number of Jacob's Farming Contests participated in. */
    private final Map<UUID, Integer> contestsParticipated = new HashMap<>();

    /** Per-player best contest collection achieved per crop. */
    private final Map<UUID, Map<GardenCrop, Long>> bestContestCollection = new HashMap<>();

    /** Per-player active Jacob's Farming Contest registration (the crop signed up for). */
    private final Map<UUID, ContestRegistration> contestRegistrations = new HashMap<>();

    /** Per-player accumulated Garden XP (drives garden level). */
    private final Map<UUID, Long> gardenExperience = new HashMap<>();

    /** Per-player copper balance (earned from visitor offers). */
    private final Map<UUID, Long> copper = new HashMap<>();

    /** Per-player count of visitor offers fulfilled. */
    private final Map<UUID, Integer> completedOffers = new HashMap<>();

    /** Per-player composter organic-matter reserve. */
    private final Map<UUID, Long> composterOrganicMatter = new HashMap<>();

    /** Per-player composter fuel reserve. */
    private final Map<UUID, Long> composterFuel = new HashMap<>();

    /** Per-player composter compost output awaiting collection. */
    private final Map<UUID, Long> composterCompost = new HashMap<>();

    private GardenManager() {
    }

    /**
     * An offer presented by a Garden {@link VisitorType}: a set of crops the
     * visitor wants in exchange for a copper reward.
     */
    public static final class VisitorOffer {
        private final VisitorType visitor;
        private final Map<GardenCrop, Integer> requiredCrops;
        private final long copperReward;

        public VisitorOffer(VisitorType visitor, Map<GardenCrop, Integer> requiredCrops, long copperReward) {
            this.visitor = Objects.requireNonNull(visitor, "visitor");
            Objects.requireNonNull(requiredCrops, "requiredCrops");
            EnumMap<GardenCrop, Integer> copy = new EnumMap<>(GardenCrop.class);
            copy.putAll(requiredCrops);
            this.requiredCrops = Collections.unmodifiableMap(copy);
            this.copperReward = Math.max(0L, copperReward);
        }

        public VisitorType getVisitor() {
            return visitor;
        }

        public Map<GardenCrop, Integer> getRequiredCrops() {
            return requiredCrops;
        }

        public long getCopperReward() {
            return copperReward;
        }
    }

    /**
     * A player's active registration for a Jacob's Farming Contest: the year-day
     * the contest is held on and the crop the player signed up to farm.
     */
    public static final class ContestRegistration {
        private final int contestDay;
        private final GardenCrop crop;

        public ContestRegistration(int contestDay, GardenCrop crop) {
            this.contestDay = contestDay;
            this.crop = Objects.requireNonNull(crop, "crop");
        }

        public int getContestDay() {
            return contestDay;
        }

        public GardenCrop getCrop() {
            return crop;
        }
    }

    /**
     * Returns the single shared {@code GardenManager} instance.
     *
     * @return the singleton instance
     */
    public static GardenManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Plot level
    // -------------------------------------------------------------------------

    /**
     * Returns the garden plot level for the given player.
     *
     * @param playerId the player to look up
     * @return the plot level, {@code 1} if not set
     */
    public int getPlotLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return plotLevels.getOrDefault(playerId, 1);
    }

    /**
     * Sets the garden plot level for the given player (clamped to [1, 24]).
     *
     * @param playerId the player to update
     * @param level    the new plot level
     */
    public void setPlotLevel(UUID playerId, int level) {
        Objects.requireNonNull(playerId, "playerId");
        plotLevels.put(playerId, Math.max(1, Math.min(GardenPlot.values().length, level)));
    }

    /**
     * Adds to the garden plot level (clamped to [1, 24]).
     *
     * @param playerId the player to update
     * @param amount   the amount to add (may be negative)
     * @return the new plot level
     */
    public int addPlotLevel(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        int current = getPlotLevel(playerId);
        int updated = Math.max(1, Math.min(GardenPlot.values().length, current + amount));
        plotLevels.put(playerId, updated);
        return updated;
    }

    // -------------------------------------------------------------------------
    // Visitor count
    // -------------------------------------------------------------------------

    /**
     * Returns the total number of visitors the player has received.
     *
     * @param playerId the player to look up
     * @return the visitor count, {@code 0} if not set
     */
    public int getVisitorCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return visitorCounts.getOrDefault(playerId, 0);
    }

    /**
     * Adds to the visitor count for the given player (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param amount   the amount to add (may be negative)
     * @return the new visitor count
     */
    public int addVisitorCount(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        int updated = Math.max(0, getVisitorCount(playerId) + amount);
        visitorCounts.put(playerId, updated);
        return updated;
    }

    /**
     * Sets the visitor count for the given player.
     *
     * @param playerId the player to update
     * @param count    the new visitor count (clamped to {@code >= 0})
     */
    public void setVisitorCount(UUID playerId, int count) {
        Objects.requireNonNull(playerId, "playerId");
        visitorCounts.put(playerId, Math.max(0, count));
    }

    // -------------------------------------------------------------------------
    // Crop upgrades
    // -------------------------------------------------------------------------

    /**
     * Returns the upgrade level for the given crop.
     *
     * @param playerId the player to look up
     * @param crop     the crop type
     * @return the upgrade level, {@code 0} if not set
     */
    public int getCropUpgrade(UUID playerId, GardenCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        int[] upgrades = cropUpgrades.get(playerId);
        return upgrades == null ? 0 : upgrades[crop.ordinal()];
    }

    /**
     * Sets the upgrade level for the given crop (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param crop     the crop type
     * @param level    the new upgrade level
     */
    public void setCropUpgrade(UUID playerId, GardenCrop crop, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        int[] upgrades = cropUpgrades.computeIfAbsent(playerId, id -> new int[GardenCrop.values().length]);
        upgrades[crop.ordinal()] = Math.max(0, level);
    }

    /**
     * Adds to the upgrade level for the given crop (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param crop     the crop type
     * @param amount   the amount to add (may be negative)
     * @return the new upgrade level
     */
    public int addCropUpgrade(UUID playerId, GardenCrop crop, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        int[] upgrades = cropUpgrades.computeIfAbsent(playerId, id -> new int[GardenCrop.values().length]);
        upgrades[crop.ordinal()] = Math.max(0, upgrades[crop.ordinal()] + amount);
        return upgrades[crop.ordinal()];
    }

    // -------------------------------------------------------------------------
    // Garden plots
    // -------------------------------------------------------------------------

    /**
     * Returns whether the given plot is unlocked for the player.
     *
     * @param playerId the player to look up
     * @param plot     the plot to check
     * @return {@code true} if unlocked
     */
    public boolean isPlotUnlocked(UUID playerId, GardenPlot plot) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(plot, "plot");
        Set<GardenPlot> plots = unlockedPlots.get(playerId);
        return plots != null && plots.contains(plot);
    }

    /**
     * Unlocks a plot for the given player.
     *
     * @param playerId the player to update
     * @param plot     the plot to unlock
     */
    public void unlockPlot(UUID playerId, GardenPlot plot) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(plot, "plot");
        unlockedPlots.computeIfAbsent(playerId, id -> EnumSet.noneOf(GardenPlot.class)).add(plot);
    }

    /**
     * Returns an immutable view of the plots unlocked for the given player.
     *
     * @param playerId the player to look up
     * @return the set of unlocked plots (may be empty, never {@code null})
     */
    public Set<GardenPlot> getUnlockedPlots(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<GardenPlot> plots = unlockedPlots.get(playerId);
        return plots == null ? java.util.Collections.emptySet() : java.util.Collections.unmodifiableSet(plots);
    }

    // -------------------------------------------------------------------------
    // Crop plot tiers
    // -------------------------------------------------------------------------

    /**
     * Returns the {@link PlotTier} for the given player and crop.
     *
     * @param playerId the player to look up
     * @param crop     the crop to check
     * @return the current tier, {@link PlotTier#TIER_1} if not yet upgraded
     */
    public PlotTier getCropPlotTier(UUID playerId, GardenCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        Map<GardenCrop, PlotTier> tiers = cropPlotTiers.get(playerId);
        return tiers == null ? PlotTier.TIER_1 : tiers.getOrDefault(crop, PlotTier.TIER_1);
    }

    /**
     * Sets the {@link PlotTier} for the given player and crop.
     *
     * @param playerId the player to update
     * @param crop     the crop to update
     * @param tier     the new tier
     */
    public void setCropPlotTier(UUID playerId, GardenCrop crop, PlotTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        Objects.requireNonNull(tier, "tier");
        cropPlotTiers.computeIfAbsent(playerId, k -> new EnumMap<>(GardenCrop.class)).put(crop, tier);
    }

    /**
     * Upgrades the crop plot tier by one step for the given player and crop.
     *
     * @param playerId the player to update
     * @param crop     the crop to upgrade
     * @return the new {@link PlotTier}, unchanged if already at max
     */
    public PlotTier upgradeCropPlotTier(UUID playerId, GardenCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        PlotTier current = getCropPlotTier(playerId, crop);
        PlotTier next = current.next();
        if (next == null) {
            return current;
        }
        cropPlotTiers.computeIfAbsent(playerId, k -> new EnumMap<>(GardenCrop.class)).put(crop, next);
        return next;
    }

    /**
     * Returns the copper cost to unlock the given crop's plot slot.
     *
     * @param crop the crop whose plot slot to price
     * @return the unlock cost in copper, {@code 0} if the crop has no plot slot
     */
    public long getCropPlotUnlockCost(GardenCrop crop) {
        Objects.requireNonNull(crop, "crop");
        return CROP_PLOT_UNLOCK_COSTS.getOrDefault(crop, 0L);
    }

    /**
     * Returns the farming-fortune bonus the player currently enjoys on the given
     * crop, derived from its crop-plot tier.
     *
     * @param playerId the player to look up
     * @param crop     the crop to check
     * @return the farming-fortune bonus from the crop's current {@link PlotTier}
     */
    public int getCropFarmingFortune(UUID playerId, GardenCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        return getCropPlotTier(playerId, crop).getFarmingFortune();
    }

    // -------------------------------------------------------------------------
    // Crop harvesting
    // -------------------------------------------------------------------------

    /**
     * Harvests the given crop for the player.
     *
     * <p>Yield = {@code baseYield * (1 + cropUpgradeLevel)}, increased by the
     * player's farming-fortune percentage.  The result is accumulated in the
     * player's harvest totals.</p>
     *
     * @param playerId the player harvesting
     * @param crop     the crop being harvested
     * @return the number of items yielded this harvest
     */
    public int harvest(UUID playerId, CropType crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        int upgradeLevel = getCropUpgrade(playerId, crop.getGardenCrop());
        int base = crop.getBaseYield() * (1 + upgradeLevel);
        int yield = base + (int) ((long) base * getFarmingFortune(playerId) / 100);
        harvestCounts.computeIfAbsent(playerId, id -> new EnumMap<>(CropType.class))
                .merge(crop, (long) yield, Long::sum);
        return yield;
    }

    /**
     * Returns the total number of the given crop this player has harvested.
     *
     * @param playerId the player to look up
     * @param crop     the crop type
     * @return total harvested, {@code 0} if none
     */
    public long getHarvestCount(UUID playerId, CropType crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        Map<CropType, Long> counts = harvestCounts.get(playerId);
        return counts == null ? 0L : counts.getOrDefault(crop, 0L);
    }

    /**
     * Returns an immutable view of all harvest counts for the given player.
     *
     * @param playerId the player to look up
     * @return map of CropType to total harvested (may be empty, never {@code null})
     */
    public Map<CropType, Long> getHarvestCounts(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<CropType, Long> counts = harvestCounts.get(playerId);
        return counts == null ? Collections.emptyMap() : Collections.unmodifiableMap(counts);
    }

    /**
     * Returns a human-readable history of the player's garden progress, derived
     * from their harvest totals and milestones.
     *
     * @param playerId the player to look up
     * @return one summary line per harvested crop (may be empty, never {@code null})
     */
    public List<String> getGardenHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<String> history = new ArrayList<>();
        for (Map.Entry<CropType, Long> entry : getHarvestCounts(playerId).entrySet()) {
            if (entry.getValue() > 0L) {
                history.add(entry.getKey().name() + ": " + entry.getValue()
                        + " harvested (milestone " + getCropMilestone(playerId, entry.getKey()) + ")");
            }
        }
        return history;
    }

    // -------------------------------------------------------------------------
    // Farming fortune
    // -------------------------------------------------------------------------

    /**
     * Returns the player's farming-fortune stat (percent bonus crop yield).
     *
     * @param playerId the player to look up
     * @return the farming fortune, {@code 0} if not set
     */
    public int getFarmingFortune(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return farmingFortune.getOrDefault(playerId, 0);
    }

    /**
     * Sets the player's farming-fortune stat (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param fortune  the new farming fortune
     */
    public void setFarmingFortune(UUID playerId, int fortune) {
        Objects.requireNonNull(playerId, "playerId");
        farmingFortune.put(playerId, Math.max(0, fortune));
    }

    /**
     * Adds to the player's farming-fortune stat (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param amount   the amount to add (may be negative)
     * @return the new farming fortune
     */
    public int addFarmingFortune(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        int updated = Math.max(0, getFarmingFortune(playerId) + amount);
        farmingFortune.put(playerId, updated);
        return updated;
    }

    // -------------------------------------------------------------------------
    // Crop milestones
    // -------------------------------------------------------------------------

    /** Base factor for the per-milestone cumulative crop requirement. */
    private static final long MILESTONE_BASE = 100L;

    /**
     * Returns the cumulative number of crops harvested required to reach the
     * given milestone level.  Requirements scale quadratically.
     *
     * @param milestone the milestone level (1-based)
     * @return the cumulative crops required, {@code 0} for {@code milestone <= 0}
     */
    public long getMilestoneThreshold(int milestone) {
        if (milestone <= 0) {
            return 0L;
        }
        return MILESTONE_BASE * milestone * milestone;
    }

    /**
     * Returns the maximum milestone level attainable for the given crop.
     *
     * @param crop the crop type
     * @return the maximum milestone level, {@code 0} if the crop has no milestone data
     */
    public int getMaxMilestone(CropType crop) {
        Objects.requireNonNull(crop, "crop");
        int[] data = CROP_DATA.get(crop.getGardenCrop().name());
        return data == null ? 0 : data[2];
    }

    /**
     * Returns the player's current milestone level for the given crop, derived
     * from their total harvested count and capped at the crop's maximum.
     *
     * @param playerId the player to look up
     * @param crop     the crop type
     * @return the current milestone level, {@code 0} if no milestone reached
     */
    public int getCropMilestone(UUID playerId, CropType crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        long harvested = getHarvestCount(playerId, crop);
        int max = getMaxMilestone(crop);
        int level = 0;
        while (level < max && harvested >= getMilestoneThreshold(level + 1)) {
            level++;
        }
        return level;
    }

    /**
     * Returns how many more of the given crop the player must harvest to reach
     * their next milestone.
     *
     * @param playerId the player to look up
     * @param crop     the crop type
     * @return crops remaining until the next milestone, {@code 0} if already maxed
     */
    public long getCropsUntilNextMilestone(UUID playerId, CropType crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        int next = getCropMilestone(playerId, crop) + 1;
        if (next > getMaxMilestone(crop)) {
            return 0L;
        }
        return Math.max(0L, getMilestoneThreshold(next) - getHarvestCount(playerId, crop));
    }

    // -------------------------------------------------------------------------
    // Jacob's Farming Contests
    // -------------------------------------------------------------------------

    /** Minimum crops collected during a contest to earn each medal tier. */
    private static final long CONTEST_BRONZE   = 1_000L;
    private static final long CONTEST_SILVER   = 5_000L;
    private static final long CONTEST_GOLD     = 10_000L;
    private static final long CONTEST_PLATINUM = 25_000L;
    private static final long CONTEST_DIAMOND  = 50_000L;

    /**
     * Returns the {@link ContestMedal} earned for the given contest collection.
     *
     * @param collected the number of crops collected during the contest
     * @return the medal tier earned, {@link ContestMedal#NONE} if below bronze
     */
    public ContestMedal medalFor(long collected) {
        if (collected >= CONTEST_DIAMOND) {
            return ContestMedal.DIAMOND;
        }
        if (collected >= CONTEST_PLATINUM) {
            return ContestMedal.PLATINUM;
        }
        if (collected >= CONTEST_GOLD) {
            return ContestMedal.GOLD;
        }
        if (collected >= CONTEST_SILVER) {
            return ContestMedal.SILVER;
        }
        if (collected >= CONTEST_BRONZE) {
            return ContestMedal.BRONZE;
        }
        return ContestMedal.NONE;
    }

    /**
     * Records the player's participation in a Jacob's Farming Contest for the
     * given crop, updating their best collection, contest count, and medal tally.
     *
     * @param playerId  the player participating
     * @param crop      the contest crop
     * @param collected the number of crops collected during the contest
     * @return the {@link ContestMedal} earned this contest
     */
    public ContestMedal recordContest(UUID playerId, GardenCrop crop, long collected) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        long amount = Math.max(0L, collected);
        contestsParticipated.merge(playerId, 1, Integer::sum);
        bestContestCollection.computeIfAbsent(playerId, id -> new EnumMap<>(GardenCrop.class))
                .merge(crop, amount, Math::max);
        ContestMedal medal = medalFor(amount);
        if (medal != ContestMedal.NONE) {
            int[] medals = contestMedals.computeIfAbsent(playerId, id -> new int[ContestMedal.values().length]);
            medals[medal.ordinal()]++;
        }
        return medal;
    }

    /**
     * Returns how many of the given medal the player has earned across all contests.
     *
     * @param playerId the player to look up
     * @param medal    the medal tier
     * @return the medal count, {@code 0} if none
     */
    public int getContestMedalCount(UUID playerId, ContestMedal medal) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(medal, "medal");
        int[] medals = contestMedals.get(playerId);
        return medals == null ? 0 : medals[medal.ordinal()];
    }

    /**
     * Returns the number of Jacob's Farming Contests the player has participated in.
     *
     * @param playerId the player to look up
     * @return the contest count, {@code 0} if none
     */
    public int getContestsParticipated(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return contestsParticipated.getOrDefault(playerId, 0);
    }

    /**
     * Returns the player's best contest collection for the given crop.
     *
     * @param playerId the player to look up
     * @param crop     the contest crop
     * @return the highest collection recorded, {@code 0} if never contested
     */
    public long getBestContestCollection(UUID playerId, GardenCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        Map<GardenCrop, Long> best = bestContestCollection.get(playerId);
        return best == null ? 0L : best.getOrDefault(crop, 0L);
    }

    /**
     * Registers the player for the Jacob's Farming Contest held on the given
     * year-day, signing them up to farm the chosen crop.
     *
     * <p>The contest schedule is owned by {@link CalendarManager}: the day must
     * host a contest ({@link CalendarManager#isContestDay(int)}) and the crop
     * must be one of the three crops featured that day
     * ({@link CalendarManager#getGardenCrops(int)}).</p>
     *
     * @param playerId   the player registering
     * @param contestDay the contest's year-day (1–{@value CalendarManager#DAYS_PER_YEAR})
     * @param crop       the crop the player wishes to farm
     * @return the resulting {@link ContestRegistration}
     * @throws IllegalArgumentException if no contest is held that day or the crop is not featured
     */
    public ContestRegistration registerForContest(UUID playerId, int contestDay, GardenCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        if (!CalendarManager.isContestDay(contestDay)) {
            throw new IllegalArgumentException(
                    "day " + contestDay + " does not host a Jacob's Farming Contest");
        }
        if (!CalendarManager.getGardenCrops(contestDay).contains(crop)) {
            throw new IllegalArgumentException(
                    crop.getDisplayName() + " is not featured in the contest on day " + contestDay);
        }
        ContestRegistration registration = new ContestRegistration(contestDay, crop);
        contestRegistrations.put(playerId, registration);
        return registration;
    }

    /**
     * Returns the player's active contest registration.
     *
     * @param playerId the player to look up
     * @return the registration, or {@code null} if the player is not registered
     */
    public ContestRegistration getContestRegistration(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return contestRegistrations.get(playerId);
    }

    /**
     * Returns whether the player is currently registered for a contest.
     *
     * @param playerId the player to look up
     * @return {@code true} if registered
     */
    public boolean isRegisteredForContest(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return contestRegistrations.containsKey(playerId);
    }

    /**
     * Finalizes the player's registered contest: scores the collection against
     * their registered crop via {@link #recordContest(UUID, GardenCrop, long)}
     * and clears the registration.
     *
     * @param playerId  the player submitting their contest result
     * @param collected the number of crops collected during the contest
     * @return the {@link ContestMedal} earned this contest
     * @throws IllegalStateException if the player is not registered for a contest
     */
    public ContestMedal submitContest(UUID playerId, long collected) {
        Objects.requireNonNull(playerId, "playerId");
        ContestRegistration registration = contestRegistrations.remove(playerId);
        if (registration == null) {
            throw new IllegalStateException("player is not registered for a contest");
        }
        return recordContest(playerId, registration.getCrop(), collected);
    }

    // -------------------------------------------------------------------------
    // Garden level XP
    // -------------------------------------------------------------------------

    /** Cumulative Garden XP required to reach each garden level (index = level). */
    private static final long[] GARDEN_LEVEL_XP = {
        0L,       // level 0
        70L,      // 1
        140L,     // 2
        280L,     // 3
        520L,     // 4
        1_120L,   // 5
        2_620L,   // 6
        4_620L,   // 7
        7_120L,   // 8
        10_120L,  // 9
        14_120L,  // 10
        20_120L,  // 11
        28_120L,  // 12
        40_120L,  // 13
        56_120L,  // 14
        81_120L,  // 15
    };

    /**
     * Returns the maximum attainable garden level.
     *
     * @return the maximum garden level
     */
    public int getMaxGardenLevel() {
        return GARDEN_LEVEL_XP.length - 1;
    }

    /**
     * Returns the player's accumulated Garden XP.
     *
     * @param playerId the player to look up
     * @return the garden XP, {@code 0} if not set
     */
    public long getGardenExperience(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return gardenExperience.getOrDefault(playerId, 0L);
    }

    /**
     * Sets the player's Garden XP (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param xp       the new garden XP
     */
    public void setGardenExperience(UUID playerId, long xp) {
        Objects.requireNonNull(playerId, "playerId");
        gardenExperience.put(playerId, Math.max(0L, xp));
    }

    /**
     * Adds to the player's Garden XP (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param amount   the amount to add (may be negative)
     * @return the new garden XP total
     */
    public long addGardenExperience(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        long updated = Math.max(0L, getGardenExperience(playerId) + amount);
        gardenExperience.put(playerId, updated);
        return updated;
    }

    /**
     * Returns the player's garden level, derived from accumulated Garden XP and
     * capped at {@link #getMaxGardenLevel()}.
     *
     * @param playerId the player to look up
     * @return the current garden level, {@code 0} if no XP earned
     */
    public int getGardenLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        long xp = getGardenExperience(playerId);
        int level = 0;
        while (level < getMaxGardenLevel() && xp >= GARDEN_LEVEL_XP[level + 1]) {
            level++;
        }
        return level;
    }

    /**
     * Returns how much more Garden XP the player needs to reach the next garden
     * level.
     *
     * @param playerId the player to look up
     * @return XP remaining until the next level, {@code 0} if already at max
     */
    public long getGardenXpToNextLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int next = getGardenLevel(playerId) + 1;
        if (next > getMaxGardenLevel()) {
            return 0L;
        }
        return Math.max(0L, GARDEN_LEVEL_XP[next] - getGardenExperience(playerId));
    }

    // -------------------------------------------------------------------------
    // Visitor offers
    // -------------------------------------------------------------------------

    /**
     * Returns the player's copper balance.
     *
     * @param playerId the player to look up
     * @return the copper balance, {@code 0} if not set
     */
    public long getCopper(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return copper.getOrDefault(playerId, 0L);
    }

    /**
     * Adds to the player's copper balance (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param amount   the amount to add (may be negative)
     * @return the new copper balance
     */
    public long addCopper(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        long updated = Math.max(0L, getCopper(playerId) + amount);
        copper.put(playerId, updated);
        return updated;
    }

    /**
     * Returns the number of visitor offers the player has fulfilled.
     *
     * @param playerId the player to look up
     * @return the completed offer count, {@code 0} if none
     */
    public int getCompletedOffers(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return completedOffers.getOrDefault(playerId, 0);
    }

    /**
     * Fulfills a visitor offer for the player: awards the offer's copper reward,
     * counts the visitor, and records the completed offer.
     *
     * @param playerId the player fulfilling the offer
     * @param offer    the offer being fulfilled
     * @return the player's new copper balance
     */
    public long completeVisitorOffer(UUID playerId, VisitorOffer offer) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(offer, "offer");
        addVisitorCount(playerId, 1);
        completedOffers.merge(playerId, 1, Integer::sum);
        return addCopper(playerId, offer.getCopperReward());
    }

    // -------------------------------------------------------------------------
    // Composter
    // -------------------------------------------------------------------------

    /** Organic matter consumed to produce a single compost. */
    public static final long ORGANIC_MATTER_PER_COMPOST = 4_000L;

    /** Fuel consumed to produce a single compost. */
    public static final long FUEL_PER_COMPOST = 2_000L;

    /**
     * Returns the player's stored composter organic matter.
     *
     * @param playerId the player to look up
     * @return the organic-matter reserve, {@code 0} if not set
     */
    public long getComposterOrganicMatter(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return composterOrganicMatter.getOrDefault(playerId, 0L);
    }

    /**
     * Adds to the player's composter organic matter (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param amount   the amount to add (may be negative)
     * @return the new organic-matter reserve
     */
    public long addComposterOrganicMatter(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        long updated = Math.max(0L, getComposterOrganicMatter(playerId) + amount);
        composterOrganicMatter.put(playerId, updated);
        return updated;
    }

    /**
     * Returns the player's stored composter fuel.
     *
     * @param playerId the player to look up
     * @return the fuel reserve, {@code 0} if not set
     */
    public long getComposterFuel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return composterFuel.getOrDefault(playerId, 0L);
    }

    /**
     * Adds to the player's composter fuel (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param amount   the amount to add (may be negative)
     * @return the new fuel reserve
     */
    public long addComposterFuel(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        long updated = Math.max(0L, getComposterFuel(playerId) + amount);
        composterFuel.put(playerId, updated);
        return updated;
    }

    /**
     * Returns the player's uncollected composter compost.
     *
     * @param playerId the player to look up
     * @return the compost awaiting collection, {@code 0} if none
     */
    public long getComposterCompost(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return composterCompost.getOrDefault(playerId, 0L);
    }

    /**
     * Processes the player's composter, converting as much organic matter and
     * fuel as available into compost.  Each compost consumes
     * {@link #ORGANIC_MATTER_PER_COMPOST} organic matter and
     * {@link #FUEL_PER_COMPOST} fuel; processing stops when either runs short.
     *
     * @param playerId the player whose composter to process
     * @return the amount of compost produced this call ({@code 0} if resources were insufficient)
     */
    public long processComposter(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        long matter = getComposterOrganicMatter(playerId);
        long fuel = getComposterFuel(playerId);
        long produced = Math.min(matter / ORGANIC_MATTER_PER_COMPOST, fuel / FUEL_PER_COMPOST);
        if (produced <= 0L) {
            return 0L;
        }
        composterOrganicMatter.put(playerId, matter - produced * ORGANIC_MATTER_PER_COMPOST);
        composterFuel.put(playerId, fuel - produced * FUEL_PER_COMPOST);
        composterCompost.merge(playerId, produced, Long::sum);
        return produced;
    }

    /**
     * Collects all compost the player's composter has produced, clearing the
     * stored amount.
     *
     * @param playerId the player collecting
     * @return the amount of compost collected
     */
    public long collectComposterCompost(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Long collected = composterCompost.remove(playerId);
        return collected == null ? 0L : collected;
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "garden.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        plotLevels.clear();
        visitorCounts.clear();
        cropUpgrades.clear();
        unlockedPlots.clear();
        cropPlotTiers.clear();
        harvestCounts.clear();
        farmingFortune.clear();
        contestMedals.clear();
        contestsParticipated.clear();
        bestContestCollection.clear();
        contestRegistrations.clear();
        gardenExperience.clear();
        copper.clear();
        completedOffers.clear();
        composterOrganicMatter.clear();
        composterFuel.clear();
        composterCompost.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isSet(key + ".plotLevel")) {
                    plotLevels.put(uuid, cfg.getInt(key + ".plotLevel", 1));
                }
                if (cfg.isSet(key + ".farmingFortune")) {
                    farmingFortune.put(uuid, cfg.getInt(key + ".farmingFortune", 0));
                }
                if (cfg.isSet(key + ".visitorCount")) {
                    visitorCounts.put(uuid, cfg.getInt(key + ".visitorCount", 0));
                }
                if (cfg.isConfigurationSection(key + ".cropUpgrades")) {
                    int[] upgrades = new int[GardenCrop.values().length];
                    for (String cropName : cfg.getConfigurationSection(key + ".cropUpgrades").getKeys(false)) {
                        try {
                            GardenCrop crop = GardenCrop.valueOf(cropName);
                            upgrades[crop.ordinal()] = cfg.getInt(key + ".cropUpgrades." + cropName, 0);
                        } catch (IllegalArgumentException ignored) {}
                    }
                    cropUpgrades.put(uuid, upgrades);
                }
                if (cfg.isList(key + ".unlockedPlots")) {
                    Set<GardenPlot> plots = EnumSet.noneOf(GardenPlot.class);
                    for (String plotName : cfg.getStringList(key + ".unlockedPlots")) {
                        try {
                            plots.add(GardenPlot.valueOf(plotName));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (!plots.isEmpty()) {
                        unlockedPlots.put(uuid, plots);
                    }
                }
                if (cfg.isConfigurationSection(key + ".cropTiers")) {
                    Map<GardenCrop, PlotTier> tiers = new EnumMap<>(GardenCrop.class);
                    for (String cropName : cfg.getConfigurationSection(key + ".cropTiers").getKeys(false)) {
                        try {
                            GardenCrop crop = GardenCrop.valueOf(cropName);
                            PlotTier tier = PlotTier.valueOf(cfg.getString(key + ".cropTiers." + cropName, "TIER_1"));
                            tiers.put(crop, tier);
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (!tiers.isEmpty()) {
                        cropPlotTiers.put(uuid, tiers);
                    }
                }
                if (cfg.isConfigurationSection(key + ".harvests")) {
                    Map<CropType, Long> counts = new EnumMap<>(CropType.class);
                    for (String typeName : cfg.getConfigurationSection(key + ".harvests").getKeys(false)) {
                        try {
                            CropType type = CropType.valueOf(typeName);
                            counts.put(type, cfg.getLong(key + ".harvests." + typeName, 0L));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (!counts.isEmpty()) {
                        harvestCounts.put(uuid, counts);
                    }
                }
                if (cfg.isSet(key + ".contestsParticipated")) {
                    contestsParticipated.put(uuid, cfg.getInt(key + ".contestsParticipated", 0));
                }
                if (cfg.isSet(key + ".gardenXp")) {
                    gardenExperience.put(uuid, cfg.getLong(key + ".gardenXp", 0L));
                }
                if (cfg.isSet(key + ".copper")) {
                    copper.put(uuid, cfg.getLong(key + ".copper", 0L));
                }
                if (cfg.isSet(key + ".completedOffers")) {
                    completedOffers.put(uuid, cfg.getInt(key + ".completedOffers", 0));
                }
                if (cfg.isSet(key + ".composterOrganicMatter")) {
                    composterOrganicMatter.put(uuid, cfg.getLong(key + ".composterOrganicMatter", 0L));
                }
                if (cfg.isSet(key + ".composterFuel")) {
                    composterFuel.put(uuid, cfg.getLong(key + ".composterFuel", 0L));
                }
                if (cfg.isSet(key + ".composterCompost")) {
                    composterCompost.put(uuid, cfg.getLong(key + ".composterCompost", 0L));
                }
                if (cfg.isConfigurationSection(key + ".contestMedals")) {
                    int[] medals = new int[ContestMedal.values().length];
                    for (String medalName : cfg.getConfigurationSection(key + ".contestMedals").getKeys(false)) {
                        try {
                            ContestMedal medal = ContestMedal.valueOf(medalName);
                            medals[medal.ordinal()] = cfg.getInt(key + ".contestMedals." + medalName, 0);
                        } catch (IllegalArgumentException ignored) {}
                    }
                    contestMedals.put(uuid, medals);
                }
                if (cfg.isConfigurationSection(key + ".bestContest")) {
                    Map<GardenCrop, Long> best = new EnumMap<>(GardenCrop.class);
                    for (String cropName : cfg.getConfigurationSection(key + ".bestContest").getKeys(false)) {
                        try {
                            GardenCrop crop = GardenCrop.valueOf(cropName);
                            best.put(crop, cfg.getLong(key + ".bestContest." + cropName, 0L));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    if (!best.isEmpty()) {
                        bestContestCollection.put(uuid, best);
                    }
                }
                if (cfg.isSet(key + ".contestRegistration.crop")) {
                    try {
                        GardenCrop crop = GardenCrop.valueOf(cfg.getString(key + ".contestRegistration.crop"));
                        int day = cfg.getInt(key + ".contestRegistration.day", 1);
                        contestRegistrations.put(uuid, new ContestRegistration(day, crop));
                    } catch (IllegalArgumentException ignored) {}
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "garden.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        Set<UUID> allUuids = new HashSet<>();
        allUuids.addAll(plotLevels.keySet());
        allUuids.addAll(visitorCounts.keySet());
        allUuids.addAll(cropUpgrades.keySet());
        allUuids.addAll(unlockedPlots.keySet());
        allUuids.addAll(cropPlotTiers.keySet());
        allUuids.addAll(harvestCounts.keySet());
        allUuids.addAll(farmingFortune.keySet());
        allUuids.addAll(contestMedals.keySet());
        allUuids.addAll(contestsParticipated.keySet());
        allUuids.addAll(bestContestCollection.keySet());
        allUuids.addAll(contestRegistrations.keySet());
        allUuids.addAll(gardenExperience.keySet());
        allUuids.addAll(copper.keySet());
        allUuids.addAll(completedOffers.keySet());
        allUuids.addAll(composterOrganicMatter.keySet());
        allUuids.addAll(composterFuel.keySet());
        allUuids.addAll(composterCompost.keySet());
        for (UUID uuid : allUuids) {
            String key = uuid.toString();
            if (plotLevels.containsKey(uuid)) {
                cfg.set(key + ".plotLevel", plotLevels.get(uuid));
            }
            if (farmingFortune.containsKey(uuid)) {
                cfg.set(key + ".farmingFortune", farmingFortune.get(uuid));
            }
            if (visitorCounts.containsKey(uuid)) {
                cfg.set(key + ".visitorCount", visitorCounts.get(uuid));
            }
            int[] upgrades = cropUpgrades.get(uuid);
            if (upgrades != null) {
                GardenCrop[] crops = GardenCrop.values();
                for (int i = 0; i < crops.length; i++) {
                    if (upgrades[i] != 0) {
                        cfg.set(key + ".cropUpgrades." + crops[i].name(), upgrades[i]);
                    }
                }
            }
            Set<GardenPlot> plots = unlockedPlots.get(uuid);
            if (plots != null && !plots.isEmpty()) {
                List<String> plotNames = new ArrayList<>();
                for (GardenPlot plot : plots) {
                    plotNames.add(plot.name());
                }
                cfg.set(key + ".unlockedPlots", plotNames);
            }
            Map<GardenCrop, PlotTier> tiers = cropPlotTiers.get(uuid);
            if (tiers != null) {
                for (Map.Entry<GardenCrop, PlotTier> e : tiers.entrySet()) {
                    cfg.set(key + ".cropTiers." + e.getKey().name(), e.getValue().name());
                }
            }
            Map<CropType, Long> counts = harvestCounts.get(uuid);
            if (counts != null) {
                for (Map.Entry<CropType, Long> hc : counts.entrySet()) {
                    cfg.set(key + ".harvests." + hc.getKey().name(), hc.getValue());
                }
            }
            if (contestsParticipated.containsKey(uuid)) {
                cfg.set(key + ".contestsParticipated", contestsParticipated.get(uuid));
            }
            if (gardenExperience.containsKey(uuid)) {
                cfg.set(key + ".gardenXp", gardenExperience.get(uuid));
            }
            if (copper.containsKey(uuid)) {
                cfg.set(key + ".copper", copper.get(uuid));
            }
            if (completedOffers.containsKey(uuid)) {
                cfg.set(key + ".completedOffers", completedOffers.get(uuid));
            }
            if (composterOrganicMatter.containsKey(uuid)) {
                cfg.set(key + ".composterOrganicMatter", composterOrganicMatter.get(uuid));
            }
            if (composterFuel.containsKey(uuid)) {
                cfg.set(key + ".composterFuel", composterFuel.get(uuid));
            }
            if (composterCompost.containsKey(uuid)) {
                cfg.set(key + ".composterCompost", composterCompost.get(uuid));
            }
            int[] medals = contestMedals.get(uuid);
            if (medals != null) {
                ContestMedal[] medalTypes = ContestMedal.values();
                for (int i = 0; i < medalTypes.length; i++) {
                    if (medals[i] != 0) {
                        cfg.set(key + ".contestMedals." + medalTypes[i].name(), medals[i]);
                    }
                }
            }
            Map<GardenCrop, Long> best = bestContestCollection.get(uuid);
            if (best != null) {
                for (Map.Entry<GardenCrop, Long> bc : best.entrySet()) {
                    cfg.set(key + ".bestContest." + bc.getKey().name(), bc.getValue());
                }
            }
            ContestRegistration registration = contestRegistrations.get(uuid);
            if (registration != null) {
                cfg.set(key + ".contestRegistration.crop", registration.getCrop().name());
                cfg.set(key + ".contestRegistration.day", registration.getContestDay());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save garden.yml", e);
        }
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Resets all garden data for the given player.
     *
     * @param playerId the player to reset
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        plotLevels.remove(playerId);
        visitorCounts.remove(playerId);
        cropUpgrades.remove(playerId);
        unlockedPlots.remove(playerId);
        cropPlotTiers.remove(playerId);
        harvestCounts.remove(playerId);
        farmingFortune.remove(playerId);
        contestMedals.remove(playerId);
        contestsParticipated.remove(playerId);
        bestContestCollection.remove(playerId);
        contestRegistrations.remove(playerId);
        gardenExperience.remove(playerId);
        copper.remove(playerId);
        completedOffers.remove(playerId);
        composterOrganicMatter.remove(playerId);
        composterFuel.remove(playerId);
        composterCompost.remove(playerId);
    }

    /**
     * Removes all garden data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = plotLevels.remove(playerId) != null;
        had |= visitorCounts.remove(playerId) != null;
        had |= cropUpgrades.remove(playerId) != null;
        had |= unlockedPlots.remove(playerId) != null;
        had |= cropPlotTiers.remove(playerId) != null;
        had |= harvestCounts.remove(playerId) != null;
        had |= farmingFortune.remove(playerId) != null;
        had |= contestMedals.remove(playerId) != null;
        had |= contestsParticipated.remove(playerId) != null;
        had |= bestContestCollection.remove(playerId) != null;
        had |= contestRegistrations.remove(playerId) != null;
        had |= gardenExperience.remove(playerId) != null;
        had |= copper.remove(playerId) != null;
        had |= completedOffers.remove(playerId) != null;
        had |= composterOrganicMatter.remove(playerId) != null;
        had |= composterFuel.remove(playerId) != null;
        had |= composterCompost.remove(playerId) != null;
        return had;
    }
}
