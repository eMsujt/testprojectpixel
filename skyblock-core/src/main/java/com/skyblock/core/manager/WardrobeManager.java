package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import com.skyblock.core.stat.StatManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton managing per-player wardrobe (named armor outfits).
 *
 * <p>Each player may save up to {@link #MAX_OUTFITS} named outfits. An outfit
 * is a snapshot of the four armor slots (helmet, chestplate, leggings, boots).
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class WardrobeManager {

    /**
     * 18 wardrobe slots arranged as 9 pages × 2 armor sets per page.
     * Page 1 = slots 1-2, page 2 = slots 3-4, …, page 9 = slots 17-18.
     */
    public enum WardrobeSlot {
        SLOT_1(1,  "Slot 1",  1, 1),
        SLOT_2(2,  "Slot 2",  1, 2),
        SLOT_3(3,  "Slot 3",  2, 1),
        SLOT_4(4,  "Slot 4",  2, 2),
        SLOT_5(5,  "Slot 5",  3, 1),
        SLOT_6(6,  "Slot 6",  3, 2),
        SLOT_7(7,  "Slot 7",  4, 1),
        SLOT_8(8,  "Slot 8",  4, 2),
        SLOT_9(9,  "Slot 9",  5, 1),
        SLOT_10(10, "Slot 10", 5, 2),
        SLOT_11(11, "Slot 11", 6, 1),
        SLOT_12(12, "Slot 12", 6, 2),
        SLOT_13(13, "Slot 13", 7, 1),
        SLOT_14(14, "Slot 14", 7, 2),
        SLOT_15(15, "Slot 15", 8, 1),
        SLOT_16(16, "Slot 16", 8, 2),
        SLOT_17(17, "Slot 17", 9, 1),
        SLOT_18(18, "Slot 18", 9, 2);

        private final int slotNumber;
        private final String displayName;
        private final int page;
        private final int set;

        WardrobeSlot(int slotNumber, String displayName, int page, int set) {
            this.slotNumber = slotNumber;
            this.displayName = displayName;
            this.page = page;
            this.set = set;
        }

        public int getSlotNumber() {
            return slotNumber;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** Returns the wardrobe page this slot belongs to (1–9). */
        public int getPage() {
            return page;
        }

        /** Returns the set number within its page (1 or 2). */
        public int getSet() {
            return set;
        }
    }

    /** Maximum named outfits a player may store. */
    public static final int MAX_OUTFITS = 18;

    private static final WardrobeManager INSTANCE = new WardrobeManager();

    /** playerId → (outfitName → armor[4]) */
    private final Map<UUID, Map<String, ItemStack[]>> wardrobes = new HashMap<>();

    /** playerId → name of the currently active (equipped) armor set */
    private final Map<UUID, String> activeArmorSet = new HashMap<>();

    /** playerId → (outfitName → aggregated armor stats for that outfit) */
    private final Map<UUID, Map<String, Map<Stat, Double>>> outfitStats = new HashMap<>();

    /** playerId → armor stat bonuses currently applied to {@link StatManager}, kept for clean swaps */
    private final Map<UUID, Map<Stat, Double>> appliedStats = new HashMap<>();

    private WardrobeManager() {}

    /**
     * Returns the single shared {@code WardrobeManager} instance.
     *
     * @return the singleton instance
     */
    public static WardrobeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Saves the given armor snapshot under {@code name} for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the outfit name, must not be null or blank
     * @param armor    the four armor slots to snapshot (index 0-3)
     * @return {@code true} if saved, {@code false} if the player already has
     *         {@link #MAX_OUTFITS} outfits and {@code name} is a new entry
     */
    public boolean saveOutfit(UUID playerId, String name, ItemStack[] armor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(armor, "armor");
        Map<String, ItemStack[]> outfits = wardrobes.computeIfAbsent(playerId, id -> new HashMap<>());
        if (!outfits.containsKey(name) && outfits.size() >= MAX_OUTFITS) {
            return false;
        }
        ItemStack[] snapshot = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            snapshot[i] = (i < armor.length && armor[i] != null) ? armor[i].clone() : null;
        }
        outfits.put(name, snapshot);
        return true;
    }

    /**
     * Returns a copy of the named outfit, or {@code null} if it does not exist.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the outfit name, must not be null
     * @return cloned armor array, or {@code null} if not found
     */
    public ItemStack[] getOutfit(UUID playerId, String name) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        Map<String, ItemStack[]> outfits = wardrobes.get(playerId);
        if (outfits == null) {
            return null;
        }
        ItemStack[] stored = outfits.get(name);
        if (stored == null) {
            return null;
        }
        ItemStack[] copy = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            copy[i] = stored[i] != null ? stored[i].clone() : null;
        }
        return copy;
    }

    /**
     * Deletes the named outfit for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the outfit name, must not be null
     * @return {@code true} if the outfit existed and was removed
     */
    public boolean deleteOutfit(UUID playerId, String name) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        Map<String, ItemStack[]> outfits = wardrobes.get(playerId);
        if (outfits == null) {
            return false;
        }
        return outfits.remove(name) != null;
    }

    /**
     * Returns an unmodifiable view of the outfit names saved by the player.
     *
     * @param playerId the player's UUID, must not be null
     * @return set of outfit names; empty if none saved
     */
    public Set<String> getOutfitNames(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, ItemStack[]> outfits = wardrobes.get(playerId);
        if (outfits == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(outfits.keySet());
    }

    /**
     * Saves the given armor snapshot into the specified wardrobe slot for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @param slot     the wardrobe slot, must not be null
     * @param armor    the four armor slots to snapshot (index 0-3)
     * @return {@code true} if saved; {@code false} if the player already has
     *         {@link #MAX_OUTFITS} outfits and the slot is a new entry
     */
    public boolean saveOutfit(UUID playerId, WardrobeSlot slot, ItemStack[] armor) {
        Objects.requireNonNull(slot, "slot");
        return saveOutfit(playerId, slot.name(), armor);
    }

    /**
     * Returns a copy of the outfit stored in the specified slot, or {@code null} if empty.
     *
     * @param playerId the player's UUID, must not be null
     * @param slot     the wardrobe slot, must not be null
     * @return cloned armor array, or {@code null} if the slot is empty
     */
    public ItemStack[] getOutfit(UUID playerId, WardrobeSlot slot) {
        Objects.requireNonNull(slot, "slot");
        return getOutfit(playerId, slot.name());
    }

    /**
     * Clears the outfit stored in the specified slot.
     *
     * @param playerId the player's UUID, must not be null
     * @param slot     the wardrobe slot, must not be null
     * @return {@code true} if the slot was occupied and is now cleared
     */
    public boolean deleteOutfit(UUID playerId, WardrobeSlot slot) {
        Objects.requireNonNull(slot, "slot");
        return deleteOutfit(playerId, slot.name());
    }

    /**
     * Saves the given armor snapshot together with the aggregated stats its
     * pieces grant, so the stats can be applied when the outfit is equipped.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the outfit name, must not be null or blank
     * @param armor    the four armor slots to snapshot (index 0-3)
     * @param stats    the combined stats granted by the outfit; may be null or empty
     * @return {@code true} if saved; {@code false} if the player already has
     *         {@link #MAX_OUTFITS} outfits and {@code name} is a new entry
     */
    public boolean saveOutfit(UUID playerId, String name, ItemStack[] armor, Map<Stat, Double> stats) {
        boolean saved = saveOutfit(playerId, name, armor);
        if (saved) {
            if (stats == null || stats.isEmpty()) {
                Map<String, Map<Stat, Double>> playerOutfits = outfitStats.get(playerId);
                if (playerOutfits != null) {
                    playerOutfits.remove(name);
                }
            } else {
                outfitStats.computeIfAbsent(playerId, id -> new HashMap<>())
                        .put(name, new EnumMap<>(stats));
            }
        }
        return saved;
    }

    /**
     * Slot-keyed variant of {@link #saveOutfit(UUID, String, ItemStack[], Map)}.
     *
     * @param playerId the player's UUID, must not be null
     * @param slot     the wardrobe slot, must not be null
     * @param armor    the four armor slots to snapshot (index 0-3)
     * @param stats    the combined stats granted by the outfit; may be null or empty
     * @return {@code true} if saved; {@code false} if the player is at the outfit cap
     */
    public boolean saveOutfit(UUID playerId, WardrobeSlot slot, ItemStack[] armor, Map<Stat, Double> stats) {
        Objects.requireNonNull(slot, "slot");
        return saveOutfit(playerId, slot.name(), armor, stats);
    }

    /**
     * Returns the aggregated stats granted by the named outfit.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the outfit name, must not be null
     * @return an unmodifiable map of stats; empty if the outfit has no stored stats
     */
    public Map<Stat, Double> getOutfitStats(UUID playerId, String name) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        Map<String, Map<Stat, Double>> playerOutfits = outfitStats.get(playerId);
        if (playerOutfits == null) {
            return Collections.emptyMap();
        }
        Map<Stat, Double> stats = playerOutfits.get(name);
        if (stats == null || stats.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new EnumMap<>(stats));
    }

    /**
     * Slot-keyed variant of {@link #getOutfitStats(UUID, String)}.
     *
     * @param playerId the player's UUID, must not be null
     * @param slot     the wardrobe slot, must not be null
     * @return an unmodifiable map of stats; empty if the slot has no stored stats
     */
    public Map<Stat, Double> getOutfitStats(UUID playerId, WardrobeSlot slot) {
        Objects.requireNonNull(slot, "slot");
        return getOutfitStats(playerId, slot.name());
    }

    /**
     * Equips the named outfit: marks it active and applies its armor stats as
     * {@link StatManager} bonuses, first removing the bonuses of the previously
     * equipped outfit so swaps don't accumulate.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the outfit name, must not be null
     * @return the cloned armor of the equipped outfit, or {@code null} if no such outfit exists
     */
    public ItemStack[] equip(UUID playerId, String name) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        ItemStack[] armor = getOutfit(playerId, name);
        if (armor == null) {
            return null;
        }
        applyStats(playerId, getOutfitStats(playerId, name));
        activeArmorSet.put(playerId, name);
        return armor;
    }

    /**
     * Slot-keyed variant of {@link #equip(UUID, String)}.
     *
     * @param playerId the player's UUID, must not be null
     * @param slot     the wardrobe slot, must not be null
     * @return the cloned armor of the equipped slot, or {@code null} if the slot is empty
     */
    public ItemStack[] equip(UUID playerId, WardrobeSlot slot) {
        Objects.requireNonNull(slot, "slot");
        return equip(playerId, slot.name());
    }

    /**
     * Unequips the player's active outfit, removing its applied armor stat
     * bonuses from {@link StatManager} and clearing the active set.
     *
     * @param playerId the player's UUID, must not be null
     * @return {@code true} if an active set was cleared
     */
    public boolean unequip(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        applyStats(playerId, Collections.emptyMap());
        return activeArmorSet.remove(playerId) != null;
    }

    /**
     * Swaps the player's applied armor stat bonuses on {@link StatManager}:
     * reverses the previously applied bonuses, then adds the new ones.
     */
    private void applyStats(UUID playerId, Map<Stat, Double> newStats) {
        StatManager statManager = StatManager.getInstance();
        Map<Stat, Double> previous = appliedStats.get(playerId);
        if (previous != null) {
            for (Map.Entry<Stat, Double> entry : previous.entrySet()) {
                statManager.addBonus(playerId, entry.getKey(), -entry.getValue());
            }
        }
        if (newStats == null || newStats.isEmpty()) {
            appliedStats.remove(playerId);
            return;
        }
        Map<Stat, Double> applied = new EnumMap<>(Stat.class);
        for (Map.Entry<Stat, Double> entry : newStats.entrySet()) {
            statManager.addBonus(playerId, entry.getKey(), entry.getValue());
            applied.put(entry.getKey(), entry.getValue());
        }
        appliedStats.put(playerId, applied);
    }

    /**
     * Returns the name of the armor set the player currently has equipped, or
     * {@code null} if none is active.
     *
     * @param playerId the player's UUID, must not be null
     * @return active armor set name, or {@code null}
     */
    public String getActiveArmorSet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeArmorSet.get(playerId);
    }

    /**
     * Records {@code name} as the player's active armor set.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the outfit name to mark as active, must not be null
     */
    public void setActiveArmorSet(UUID playerId, String name) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        activeArmorSet.put(playerId, name);
    }

    /**
     * Clears the active armor set for the player (e.g. when they unequip).
     *
     * @param playerId the player's UUID, must not be null
     * @return {@code true} if an active set was cleared
     */
    public boolean clearActiveArmorSet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        applyStats(playerId, Collections.emptyMap());
        return activeArmorSet.remove(playerId) != null;
    }

    /**
     * Resets all wardrobe data for the given player.
     *
     * @param playerId the player to reset
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        applyStats(playerId, Collections.emptyMap());
        wardrobes.remove(playerId);
        activeArmorSet.remove(playerId);
        outfitStats.remove(playerId);
    }

    /**
     * Removes all wardrobe data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        applyStats(playerId, Collections.emptyMap());
        activeArmorSet.remove(playerId);
        outfitStats.remove(playerId);
        return wardrobes.remove(playerId) != null;
    }

    /** Removes all stored wardrobe data. */
    public void clear() {
        wardrobes.clear();
        activeArmorSet.clear();
        outfitStats.clear();
        appliedStats.clear();
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "wardrobe.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        wardrobes.clear();
        activeArmorSet.clear();
        outfitStats.clear();
        appliedStats.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String active = cfg.getString(key + ".active");
                if (active != null) {
                    activeArmorSet.put(uuid, active);
                }
                if (cfg.isConfigurationSection(key + ".outfits")) {
                    Map<String, ItemStack[]> outfits = new HashMap<>();
                    for (String outfitName : cfg.getConfigurationSection(key + ".outfits").getKeys(false)) {
                        ItemStack[] armor = new ItemStack[4];
                        for (int i = 0; i < 4; i++) {
                            armor[i] = cfg.getItemStack(key + ".outfits." + outfitName + "." + i);
                        }
                        outfits.put(outfitName, armor);
                        String statsPath = key + ".outfits." + outfitName + ".stats";
                        if (cfg.isConfigurationSection(statsPath)) {
                            Map<Stat, Double> stats = new EnumMap<>(Stat.class);
                            for (String statName : cfg.getConfigurationSection(statsPath).getKeys(false)) {
                                try {
                                    stats.put(Stat.valueOf(statName), cfg.getDouble(statsPath + "." + statName));
                                } catch (IllegalArgumentException ignored) {
                                    // skip unknown stat names
                                }
                            }
                            if (!stats.isEmpty()) {
                                outfitStats.computeIfAbsent(uuid, id -> new HashMap<>()).put(outfitName, stats);
                            }
                        }
                    }
                    wardrobes.put(uuid, outfits);
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "wardrobe.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, ItemStack[]>> entry : wardrobes.entrySet()) {
            String key = entry.getKey().toString();
            String active = activeArmorSet.get(entry.getKey());
            if (active != null) {
                cfg.set(key + ".active", active);
            }
            Map<String, Map<Stat, Double>> playerStats = outfitStats.get(entry.getKey());
            for (Map.Entry<String, ItemStack[]> outfit : entry.getValue().entrySet()) {
                ItemStack[] armor = outfit.getValue();
                for (int i = 0; i < armor.length; i++) {
                    cfg.set(key + ".outfits." + outfit.getKey() + "." + i, armor[i]);
                }
                Map<Stat, Double> stats = playerStats == null ? null : playerStats.get(outfit.getKey());
                if (stats != null) {
                    for (Map.Entry<Stat, Double> stat : stats.entrySet()) {
                        cfg.set(key + ".outfits." + outfit.getKey() + ".stats." + stat.getKey().name(), stat.getValue());
                    }
                }
            }
        }
        // persist active sets for players who have no outfits saved
        for (Map.Entry<UUID, String> entry : activeArmorSet.entrySet()) {
            String key = entry.getKey().toString();
            if (!wardrobes.containsKey(entry.getKey())) {
                cfg.set(key + ".active", entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save wardrobe.yml", e);
        }
    }
}
