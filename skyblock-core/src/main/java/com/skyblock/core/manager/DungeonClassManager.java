package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical singleton DungeonClassManager for SkyBlock.
 *
 * <p>Tracks each player's selected dungeon class and per-class levels (driven by
 * accumulated class XP), and derives the passive stat bonus each class grants at
 * its current level. The five playable classes — Archer, Berserk, Mage, Healer
 * and Tank — each buff a single primary {@link Stat} that scales linearly with
 * the class level.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class DungeonClassManager {

    /** Maximum dungeon class level. */
    public static final int MAX_CLASS_LEVEL = 50;

    /**
     * Playable dungeon classes, each granting a passive bonus to one primary
     * {@link Stat} that scales with the class level.
     */
    public enum DungeonClass {
        ARCHER("Archer", Stat.CRIT_DAMAGE, 1.0),
        BERSERK("Berserk", Stat.STRENGTH, 1.0),
        MAGE("Mage", Stat.INTELLIGENCE, 2.0),
        HEALER("Healer", Stat.HEALTH, 2.0),
        TANK("Tank", Stat.DEFENSE, 1.0);

        private final String displayName;
        private final Stat passiveStat;
        private final double bonusPerLevel;

        DungeonClass(String displayName, Stat passiveStat, double bonusPerLevel) {
            this.displayName = displayName;
            this.passiveStat = passiveStat;
            this.bonusPerLevel = bonusPerLevel;
        }

        public String getDisplayName() { return displayName; }
        public Stat getPassiveStat() { return passiveStat; }
        public double getBonusPerLevel() { return bonusPerLevel; }
    }

    /** Cumulative XP required to reach each class level (index = level, 0..50). */
    private static final long[] CLASS_XP_TABLE = {
        0L, 50L, 125L, 235L, 395L, 625L, 955L, 1425L, 2095L, 3045L, 4385L,
        6275L, 8940L, 12700L, 17960L, 25340L, 35640L, 50040L, 70040L, 97640L,
        135640L, 188140L, 259640L, 356640L, 488640L, 668640L, 911640L, 1239640L,
        1684640L, 2284640L, 3084640L, 4149640L, 5559640L, 7459640L, 9959640L,
        13259640L, 17559640L, 23159640L, 30359640L, 39559640L, 51559640L,
        66559640L, 85559640L, 109559640L, 139559640L, 177559640L, 225559640L,
        285559640L, 360559640L, 453559640L, 569809640L
    };

    private static final DungeonClassManager INSTANCE = new DungeonClassManager();

    public static DungeonClassManager getInstance() {
        return INSTANCE;
    }

    private DungeonClassManager() {}

    /** Selected dungeon class per player. */
    private final Map<UUID, DungeonClass> selectedClass = new HashMap<>();
    /** Accumulated class XP per player per DungeonClass. */
    private final Map<UUID, Map<DungeonClass, Double>> classXp = new HashMap<>();

    // -------------------------------------------------------------------------
    // Class selection
    // -------------------------------------------------------------------------

    /** Selects the active dungeon class for the given player. */
    public void selectClass(UUID playerId, DungeonClass dungeonClass) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        selectedClass.put(playerId, dungeonClass);
    }

    /** Returns the player's selected dungeon class, or {@code null} if none chosen. */
    public DungeonClass getSelectedClass(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return selectedClass.get(playerId);
    }

    // -------------------------------------------------------------------------
    // Class XP / levels
    // -------------------------------------------------------------------------

    /** Adds class XP to the given class and returns the player's new total XP for it. */
    public double addClassXp(UUID playerId, DungeonClass dungeonClass, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative: " + amount);
        return classXp.computeIfAbsent(playerId, k -> new EnumMap<>(DungeonClass.class))
                .merge(dungeonClass, amount, Double::sum);
    }

    /** Returns the player's accumulated XP in the given class. */
    public double getClassXp(UUID playerId, DungeonClass dungeonClass) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        Map<DungeonClass, Double> xp = classXp.get(playerId);
        return xp == null ? 0.0 : xp.getOrDefault(dungeonClass, 0.0);
    }

    /** Returns the player's level (0..{@link #MAX_CLASS_LEVEL}) in the given class. */
    public int getClassLevel(UUID playerId, DungeonClass dungeonClass) {
        double xp = getClassXp(playerId, dungeonClass);
        int level = 0;
        while (level < MAX_CLASS_LEVEL && xp >= CLASS_XP_TABLE[level + 1]) {
            level++;
        }
        return level;
    }

    // -------------------------------------------------------------------------
    // Passive stat bonuses
    // -------------------------------------------------------------------------

    /**
     * Returns the passive bonus the given class grants at the player's current
     * level in it: {@code bonusPerLevel * level} of the class's primary stat.
     */
    public double getPassiveBonus(UUID playerId, DungeonClass dungeonClass) {
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        return dungeonClass.getBonusPerLevel() * getClassLevel(playerId, dungeonClass);
    }

    /**
     * Returns the passive stat bonus granted by the player's currently selected
     * class, keyed by the affected {@link Stat}. Empty if no class is selected or
     * the bonus is zero.
     */
    public Map<Stat, Double> getActivePassiveBonus(UUID playerId) {
        DungeonClass selected = getSelectedClass(playerId);
        if (selected == null) return Collections.emptyMap();
        double bonus = getPassiveBonus(playerId, selected);
        if (bonus == 0.0) return Collections.emptyMap();
        return Collections.singletonMap(selected.getPassiveStat(), bonus);
    }

    /** Removes all dungeon-class data for a player. */
    public void remove(UUID playerId) {
        selectedClass.remove(playerId);
        classXp.remove(playerId);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "dungeonclass.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        selectedClass.clear();
        classXp.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String cls = cfg.getString(key + ".selected");
                if (cls != null) {
                    try { selectedClass.put(uuid, DungeonClass.valueOf(cls)); }
                    catch (IllegalArgumentException ignored) {}
                }
                if (cfg.isConfigurationSection(key + ".classXp")) {
                    Map<DungeonClass, Double> xp = new EnumMap<>(DungeonClass.class);
                    for (DungeonClass dc : DungeonClass.values()) {
                        double val = cfg.getDouble(key + ".classXp." + dc.name(), 0.0);
                        if (val > 0) xp.put(dc, val);
                    }
                    if (!xp.isEmpty()) classXp.put(uuid, xp);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "dungeonclass.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, DungeonClass> entry : selectedClass.entrySet()) {
            cfg.set(entry.getKey().toString() + ".selected", entry.getValue().name());
        }
        for (Map.Entry<UUID, Map<DungeonClass, Double>> entry : classXp.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<DungeonClass, Double> e : entry.getValue().entrySet()) {
                cfg.set(key + ".classXp." + e.getKey().name(), e.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save dungeonclass.yml", e);
        }
    }
}
