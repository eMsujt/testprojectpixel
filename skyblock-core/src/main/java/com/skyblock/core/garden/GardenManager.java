package com.skyblock.core.garden;

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

    /** Individual purchasable plots in the Garden (24 total). */
    public enum GardenPlot {
        CENTER("Center"),
        NORTH_1("North 1"),
        NORTH_2("North 2"),
        NORTH_3("North 3"),
        NORTH_4("North 4"),
        SOUTH_1("South 1"),
        SOUTH_2("South 2"),
        SOUTH_3("South 3"),
        SOUTH_4("South 4"),
        EAST_1("East 1"),
        EAST_2("East 2"),
        EAST_3("East 3"),
        EAST_4("East 4"),
        WEST_1("West 1"),
        WEST_2("West 2"),
        WEST_3("West 3"),
        WEST_4("West 4"),
        NORTH_EAST_1("North East 1"),
        NORTH_EAST_2("North East 2"),
        NORTH_EAST_3("North East 3"),
        NORTH_WEST_1("North West 1"),
        NORTH_WEST_2("North West 2"),
        SOUTH_EAST_1("South East 1"),
        SOUTH_WEST_1("South West 1");

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

    /** Upgrade tiers for a crop plot in the Garden. */
    public enum PlotTier {
        TIER_1("Tier I",   1),
        TIER_2("Tier II",  2),
        TIER_3("Tier III", 3),
        TIER_4("Tier IV",  4),
        TIER_5("Tier V",   5);

        private final String displayName;
        private final int tier;

        PlotTier(String displayName, int tier) {
            this.displayName = displayName;
            this.tier = tier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getTier() {
            return tier;
        }

        /** Returns the next tier, or {@code null} if already at max. */
        public PlotTier next() {
            int next = ordinal() + 1;
            PlotTier[] values = values();
            return next < values.length ? values[next] : null;
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

    private GardenManager() {
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
        plotLevels.put(playerId, Math.max(1, Math.min(24, level)));
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
        int updated = Math.max(1, Math.min(24, current + amount));
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

    // -------------------------------------------------------------------------
    // Crop harvesting
    // -------------------------------------------------------------------------

    /**
     * Harvests the given crop for the player.
     *
     * <p>Yield = {@code baseYield * (1 + cropUpgradeLevel)}.  The result is
     * accumulated in the player's harvest totals.</p>
     *
     * @param playerId the player harvesting
     * @param crop     the crop being harvested
     * @return the number of items yielded this harvest
     */
    public int harvest(UUID playerId, CropType crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        int upgradeLevel = getCropUpgrade(playerId, crop.getGardenCrop());
        int yield = crop.getBaseYield() * (1 + upgradeLevel);
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
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isSet(key + ".plotLevel")) {
                    plotLevels.put(uuid, cfg.getInt(key + ".plotLevel", 1));
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
        for (UUID uuid : allUuids) {
            String key = uuid.toString();
            if (plotLevels.containsKey(uuid)) {
                cfg.set(key + ".plotLevel", plotLevels.get(uuid));
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
        return had;
    }
}
