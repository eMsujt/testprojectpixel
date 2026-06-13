package com.skyblock.core.fishing;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock fishing skill progression and loot rolls.
 *
 * <p>Tracks per-player fishing XP and level, and exposes a weighted loot table
 * keyed on minimum fishing skill level. Fishing level thresholds follow a simple
 * exponential curve (50 XP × level² per level-up, capped at level 50).</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class FishingManager {

    /** All fish types obtainable through SkyBlock fishing. */
    public enum FishType {
        COD,
        SALMON,
        PUFFERFISH,
        TROPICAL_FISH,
        INK_SAC,
        LILY_PAD,
        NAUTILUS_SHELL,
        PRISMARINE_CRYSTALS,
        PRISMARINE_SHARD,
        SPONGE,
        SEA_LANTERN,
        TREASURE_MAP
    }

    /** Loot entry: a fish type, its material drop, and the minimum fishing level required. */
    private static final class LootEntry {
        final FishType type;
        final Material material;
        final int minLevel;
        final double weight;

        LootEntry(FishType type, Material material, int minLevel, double weight) {
            this.type = type;
            this.material = material;
            this.minLevel = minLevel;
            this.weight = weight;
        }
    }

    private static final LootEntry[] LOOT_TABLE = {
        new LootEntry(FishType.COD,                 Material.COD,                  1,  50.0),
        new LootEntry(FishType.SALMON,              Material.SALMON,               1,  30.0),
        new LootEntry(FishType.TROPICAL_FISH,       Material.TROPICAL_FISH,        5,  10.0),
        new LootEntry(FishType.PUFFERFISH,          Material.PUFFERFISH,           5,   8.0),
        new LootEntry(FishType.INK_SAC,             Material.INK_SAC,             10,   5.0),
        new LootEntry(FishType.LILY_PAD,            Material.LILY_PAD,            15,   4.0),
        new LootEntry(FishType.NAUTILUS_SHELL,      Material.NAUTILUS_SHELL,      20,   2.0),
        new LootEntry(FishType.PRISMARINE_CRYSTALS, Material.PRISMARINE_CRYSTALS, 30,   1.5),
        new LootEntry(FishType.PRISMARINE_SHARD,    Material.PRISMARINE_SHARD,    25,   2.0),
        new LootEntry(FishType.SPONGE,              Material.SPONGE,              20,   1.0),
        new LootEntry(FishType.SEA_LANTERN,         Material.SEA_LANTERN,         35,   0.8),
        new LootEntry(FishType.TREASURE_MAP,        Material.MAP,                 40,   0.5),
    };

    /** Sea creatures that can be summoned while fishing. */
    public enum SeaCreature {
        SEA_WALKER(1,  0.30),
        NIGHT_SQUID(5,  0.25),
        SEA_GUARDIAN(15, 0.14),
        SEA_WITCH(20, 0.10),
        SEA_ARCHER(25, 0.08),
        MONSTER_OF_THE_DEEP(30, 0.06),
        CATFISH(35, 0.04),
        CARROT_KING(40, 0.03),
        DEEP_SEA_PROTECTOR(45, 0.02);

        /** Minimum fishing level required for this creature to appear. */
        public final int minLevel;
        /** Base spawn chance (0–1) when the player meets the level requirement. */
        public final double spawnChance;

        SeaCreature(int minLevel, double spawnChance) {
            this.minLevel = minLevel;
            this.spawnChance = spawnChance;
        }
    }

    /** Rarity tiers assignable to fish catches. */
    public enum FishRarity {
        COMMON("Common",      1,  0.55),
        UNCOMMON("Uncommon",  5,  0.25),
        RARE("Rare",         15,  0.12),
        EPIC("Epic",         25,  0.06),
        LEGENDARY("Legendary", 35, 0.02);

        /** Human-readable display name. */
        public final String displayName;
        /** Minimum fishing level required for this rarity to drop. */
        public final int minLevel;
        /** Base drop chance (0–1) when the player meets the level requirement. */
        public final double dropChance;

        FishRarity(String displayName, int minLevel, double dropChance) {
            this.displayName = displayName;
            this.minLevel = minLevel;
            this.dropChance = dropChance;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Treasure tiers obtainable from fishing at sufficient skill levels. */
    public enum FishingTreasure {
        COMMON_TREASURE(1,  0.40, "Common Treasure"),
        UNCOMMON_TREASURE(10, 0.25, "Uncommon Treasure"),
        RARE_TREASURE(20, 0.15, "Rare Treasure"),
        EPIC_TREASURE(30, 0.10, "Epic Treasure"),
        LEGENDARY_TREASURE(40, 0.05, "Legendary Treasure");

        /** Minimum fishing level required for this treasure to drop. */
        public final int minLevel;
        /** Base drop chance (0–1) when the player meets the level requirement. */
        public final double dropChance;
        /** Human-readable display name. */
        public final String displayName;

        FishingTreasure(int minLevel, double dropChance, String displayName) {
            this.minLevel = minLevel;
            this.dropChance = dropChance;
            this.displayName = displayName;
        }
    }

    /** Trophy fish types obtainable through SkyBlock trophy fishing. */
    public enum TrophyFish {
        MAHI_MAHI(1,   "Mahi Mahi"),
        SULPHUR_SKITTER(1,   "Sulphur Skitter"),
        OBFUSCATED_FISH_1(1,   "Obfuscated Fish 1"),
        OBFUSCATED_FISH_2(10,  "Obfuscated Fish 2"),
        OBFUSCATED_FISH_3(20,  "Obfuscated Fish 3"),
        STEAMING_HOT_FLOUNDER(5,   "Steaming-Hot Flounder"),
        GUSHER(5,   "Gusher"),
        BLOBFISH(10,  "Blobfish"),
        SLUGFISH(10,  "Slugfish"),
        FLYFISH(15,  "Flyfish"),
        LAVA_HORSE(20,  "Lava Horse"),
        MANA_RAY(20,  "Mana Ray"),
        VOLCANIC_STONEFISH(25,  "Volcanic Stonefish"),
        VANILLE(25,  "Vanille"),
        SKELETON_FISH(30,  "Skeleton Fish");

        /** Minimum fishing level required for this trophy fish to drop. */
        public final int minLevel;
        /** Human-readable display name. */
        public final String displayName;

        TrophyFish(int minLevel, String displayName) {
            this.minLevel = minLevel;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Trophy fish obtainable through SkyBlock trophy fishing (ocean and lava zones). */
    public enum FishingTrophy {
        SULKY_SHARK(1,   "Sulky Shark"),
        STEAMING_HOT_FLOUNDER(5,   "Steaming-Hot Flounder"),
        GUSHER(5,   "Gusher"),
        BLOBFISH(10,  "Blobfish"),
        SLUGFISH(10,  "Slugfish"),
        FLYFISH(15,  "Flyfish"),
        LAVA_HORSE(20,  "Lava Horse"),
        MANA_RAY(20,  "Mana Ray"),
        VOLCANIC_STONEFISH(25,  "Volcanic Stonefish"),
        VANILLE(25,  "Vanille"),
        SKELETON_FISH(30,  "Skeleton Fish"),
        MAHI_MAHI(1,   "Mahi-Mahi"),
        SULPHUR_SKITTER(1,   "Sulphur Skitter"),
        OBFUSCATED_FISH_1(1,   "Obfuscated Fish 1"),
        OBFUSCATED_FISH_2(10,  "Obfuscated Fish 2"),
        OBFUSCATED_FISH_3(20,  "Obfuscated Fish 3");

        /** Minimum fishing level required for this trophy fish to drop. */
        public final int minLevel;
        /** Human-readable display name. */
        public final String displayName;

        FishingTrophy(int minLevel, String displayName) {
            this.minLevel = minLevel;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Overall chance (0–1) that a fishing catch triggers a sea-creature spawn check. */
    public static final double BASE_SEA_CREATURE_CHANCE = 0.20;

    private static final int MAX_LEVEL = 50;
    /** Base XP awarded per successful catch. */
    public static final double XP_PER_CATCH = 10.0;

    private static final FishingManager INSTANCE = new FishingManager();

    /** Per-player accumulated fishing XP. */
    private final Map<UUID, Double> fishingXp = new HashMap<>();
    /** Per-player fishing level cache. */
    private final Map<UUID, Integer> fishingLevel = new HashMap<>();

    private final Random random = new Random();

    private FishingManager() {
    }

    /**
     * Returns the single shared {@code FishingManager} instance.
     *
     * @return the singleton instance
     */
    public static FishingManager getInstance() {
        return INSTANCE;
    }

    // ---------------------------------------------------------------------------
    // XP and levelling
    // ---------------------------------------------------------------------------

    /**
     * Adds fishing XP to the player and updates their level if thresholds are crossed.
     *
     * @param playerId the player receiving XP
     * @param amount   XP to add, must not be negative
     * @return the player's new total XP
     */
    public double addXp(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        double total = fishingXp.merge(playerId, amount, Double::sum);
        int newLevel = computeLevel(total);
        fishingLevel.put(playerId, newLevel);
        return total;
    }

    /**
     * Returns the player's current fishing XP.
     *
     * @param playerId the player to look up
     * @return total XP, {@code 0} if none recorded
     */
    public double getXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return fishingXp.getOrDefault(playerId, 0.0);
    }

    /**
     * Returns the player's current fishing level (1–{@value #MAX_LEVEL}).
     *
     * @param playerId the player to look up
     * @return fishing level
     */
    public int getLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return fishingLevel.getOrDefault(playerId, 1);
    }

    // ---------------------------------------------------------------------------
    // Loot
    // ---------------------------------------------------------------------------

    /**
     * Rolls the loot table for the given fishing level and returns an
     * {@link ItemStack} for the chosen entry.
     *
     * @param level the player's fishing level
     * @return the chosen loot item
     */
    public ItemStack rollLoot(int level) {
        double totalWeight = 0.0;
        for (LootEntry entry : LOOT_TABLE) {
            if (entry.minLevel <= level) {
                totalWeight += entry.weight;
            }
        }

        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (LootEntry entry : LOOT_TABLE) {
            if (entry.minLevel > level) {
                continue;
            }
            cumulative += entry.weight;
            if (roll < cumulative) {
                return new ItemStack(entry.material, 1);
            }
        }

        // Fallback — should never be reached when LOOT_TABLE is non-empty
        return new ItemStack(Material.COD, 1);
    }

    // ---------------------------------------------------------------------------
    // Sea creatures
    // ---------------------------------------------------------------------------

    /**
     * Rolls whether a sea creature spawns for the given fishing level.
     * Returns the chosen {@link SeaCreature}, or {@code null} if none spawns.
     *
     * <p>First checks the overall {@link #BASE_SEA_CREATURE_CHANCE}, then selects
     * a random eligible creature weighted by its {@code spawnChance}.</p>
     *
     * @param level the player's fishing level
     * @return the sea creature to spawn, or {@code null}
     */
    public SeaCreature rollSeaCreature(int level) {
        if (random.nextDouble() >= BASE_SEA_CREATURE_CHANCE) {
            return null;
        }

        double totalWeight = 0.0;
        for (SeaCreature creature : SeaCreature.values()) {
            if (creature.minLevel <= level) {
                totalWeight += creature.spawnChance;
            }
        }
        if (totalWeight == 0.0) {
            return null;
        }

        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (SeaCreature creature : SeaCreature.values()) {
            if (creature.minLevel > level) {
                continue;
            }
            cumulative += creature.spawnChance;
            if (roll < cumulative) {
                return creature;
            }
        }
        return null;
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /**
     * Computes the fishing level for the given total XP.
     * Formula: level {@code n} requires {@code 50 * n^2} cumulative XP.
     *
     * @param totalXp total accumulated fishing XP
     * @return level between 1 and {@value #MAX_LEVEL}
     */
    private static int computeLevel(double totalXp) {
        int level = 1;
        while (level < MAX_LEVEL) {
            double threshold = 50.0 * (level + 1) * (level + 1);
            if (totalXp < threshold) {
                break;
            }
            level++;
        }
        return level;
    }
}
