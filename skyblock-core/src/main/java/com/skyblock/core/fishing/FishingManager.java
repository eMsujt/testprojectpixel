package com.skyblock.core.fishing;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
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
        PUFFERFISH,
        SEA_CREATURE,
        FISHING_TREASURE,
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
        new LootEntry(FishType.SEA_CREATURE,        Material.NAUTILUS_SHELL,      20,   2.0),
        new LootEntry(FishType.FISHING_TREASURE,    Material.MAP,                 40,   0.5),
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

    /** Treasure items obtainable from fishing at sufficient skill levels. */
    public enum FishingTreasure {
        COMMON_FISH(1,  0.40, "Common Fish"),
        ENCHANTED_FISH(10, 0.25, "Enchanted Fish"),
        SPONGE(15, 0.15, "Sponge"),
        PRISMARINE(20, 0.10, "Prismarine"),
        MAGMA_FISH(30, 0.06, "Magma Fish"),
        SEA_CREATURE_LURE(40, 0.04, "Sea Creature Lure");

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
        // COMMON — level 1–10
        SULPHUR_SKITTER(1,   "Sulphur Skitter",        FishRarity.COMMON,   0.30),
        OBFUSCATED_FISH_1(1, "Obfuscated Fish 1",      FishRarity.COMMON,   0.25),
        MAHI_MAHI(1,         "Mahi Mahi",               FishRarity.COMMON,   0.28),
        STEAMING_HOT_FLOUNDER(5, "Steaming-Hot Flounder", FishRarity.COMMON, 0.20),
        GUSHER(5,            "Gusher",                  FishRarity.COMMON,   0.18),
        SLUGFISH(10,         "Slugfish",                FishRarity.COMMON,   0.16),
        PUFFERFISH_TROPHY(1, "Trophy Pufferfish",       FishRarity.COMMON,   0.22),
        INK_BLOB(1,          "Ink Blob",                FishRarity.COMMON,   0.24),
        SEA_LEECH(5,         "Sea Leech",               FishRarity.COMMON,   0.19),
        CORAL_GHOST(5,       "Coral Ghost",             FishRarity.COMMON,   0.17),
        SAND_SKIMMER(10,     "Sand Skimmer",            FishRarity.COMMON,   0.15),

        // UNCOMMON — level 5–20
        OBFUSCATED_FISH_2(10, "Obfuscated Fish 2",     FishRarity.UNCOMMON,  0.15),
        BLOBFISH(10,          "Blobfish",               FishRarity.UNCOMMON,  0.12),
        FLYFISH(15,           "Flyfish",                FishRarity.UNCOMMON,  0.10),
        LAVA_HORSE(20,        "Lava Horse",             FishRarity.UNCOMMON,  0.08),
        CRYSTAL_WORM(10,      "Crystal Worm",           FishRarity.UNCOMMON,  0.11),
        MAGMA_SLUG(15,        "Magma Slug",             FishRarity.UNCOMMON,  0.09),
        THUNDER_EEL(15,       "Thunder Eel",            FishRarity.UNCOMMON,  0.09),
        LAVA_CARP(10,         "Lava Carp",              FishRarity.UNCOMMON,  0.13),
        MOLTEN_BLOWFISH(15,   "Molten Blowfish",        FishRarity.UNCOMMON,  0.10),
        TOXIC_TOADFISH(20,    "Toxic Toadfish",         FishRarity.UNCOMMON,  0.07),

        // RARE — level 15–30
        OBFUSCATED_FISH_3(20, "Obfuscated Fish 3",     FishRarity.RARE,      0.08),
        MANA_RAY(20,          "Mana Ray",               FishRarity.RARE,      0.06),
        VOLCANIC_STONEFISH(25,"Volcanic Stonefish",     FishRarity.RARE,      0.05),
        VANILLE(25,           "Vanille",                FishRarity.RARE,      0.04),
        SKELETON_FISH(30,     "Skeleton Fish",          FishRarity.RARE,      0.03),
        BLAZING_SHARK(20,     "Blazing Shark",          FishRarity.RARE,      0.05),
        SCORCHED_PHANTOM(25,  "Scorched Phantom",       FishRarity.RARE,      0.04),
        VOLCANIC_BASS(15,     "Volcanic Bass",          FishRarity.RARE,      0.06),
        LAVA_SERPENT(25,      "Lava Serpent",           FishRarity.RARE,      0.04),
        PYROCLASTIC_DACE(30,  "Pyroclastic Dace",       FishRarity.RARE,      0.03),

        // EPIC — level 25–40
        CRIMSON_GLOWFISH(30,  "Crimson Glowfish",       FishRarity.EPIC,      0.02),
        PHANTOM_FISHER(30,    "Phantom Fisher",         FishRarity.EPIC,      0.02),
        ABYSSAL_MANTA(35,     "Abyssal Manta",          FishRarity.EPIC,      0.015),
        DEEP_LAVA_EEL(35,     "Deep Lava Eel",          FishRarity.EPIC,      0.015),
        PYROCLASTIC_FLOUNDER(35, "Pyroclastic Flounder",FishRarity.EPIC,      0.014),
        INFERNO_PIKE(40,      "Inferno Pike",           FishRarity.EPIC,      0.012),
        HELLFIRE_BARRACUDA(40,"Hellfire Barracuda",     FishRarity.EPIC,      0.010),

        // LEGENDARY — level 35–50
        HELLFIRE_TUNA(40,     "Hellfire Tuna",          FishRarity.LEGENDARY, 0.005),
        LAVA_LEVIATHAN(45,    "Lava Leviathan",         FishRarity.LEGENDARY, 0.004),
        MAGMATIC_BEHEMOTH(45, "Magmatic Behemoth",      FishRarity.LEGENDARY, 0.003),
        VOLCANIC_BASILISK(50, "Volcanic Basilisk",      FishRarity.LEGENDARY, 0.002);

        /** Minimum fishing level required for this trophy fish to drop. */
        public final int minLevel;
        /** Human-readable display name. */
        public final String displayName;
        /** Rarity tier of this trophy fish. */
        public final FishRarity rarity;
        /** Base drop chance (0–1) when the player meets the level requirement. */
        public final double dropChance;

        TrophyFish(int minLevel, String displayName, FishRarity rarity, double dropChance) {
            this.minLevel = minLevel;
            this.displayName = displayName;
            this.rarity = rarity;
            this.dropChance = dropChance;
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
    /** Per-player total fish caught. */
    private final Map<UUID, Integer> totalFishCaught = new HashMap<>();
    /** Per-player per-treasure catch counts. */
    private final Map<UUID, Map<FishingTreasure, Integer>> treasureCounts = new HashMap<>();

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
        return new ItemStack(Material.INK_SAC, 1);
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

    // ---------------------------------------------------------------------------
    // Catch tracking
    // ---------------------------------------------------------------------------

    /** Increments the player's total fish caught by one and returns the new total. */
    public int addFishCaught(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int total = totalFishCaught.merge(playerId, 1, Integer::sum);
        return total;
    }

    /** Returns the player's total fish caught, {@code 0} if none recorded. */
    public int getTotalFishCaught(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return totalFishCaught.getOrDefault(playerId, 0);
    }

    /** Increments the player's catch count for a specific treasure by one. */
    public void addTreasureCatch(UUID playerId, FishingTreasure treasure) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(treasure, "treasure");
        treasureCounts
            .computeIfAbsent(playerId, k -> new HashMap<>())
            .merge(treasure, 1, Integer::sum);
    }

    /** Returns the player's catch count for a specific treasure, {@code 0} if none recorded. */
    public int getTreasureCatchCount(UUID playerId, FishingTreasure treasure) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(treasure, "treasure");
        Map<FishingTreasure, Integer> counts = treasureCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(treasure, 0);
    }

    // ---------------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "fishing.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        fishingXp.clear();
        fishingLevel.clear();
        totalFishCaught.clear();
        treasureCounts.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double xp = cfg.getDouble(key + ".xp", 0.0);
                if (xp > 0.0) {
                    fishingXp.put(uuid, xp);
                    fishingLevel.put(uuid, computeLevel(xp));
                }
                int caught = cfg.getInt(key + ".totalFishCaught", 0);
                if (caught > 0) {
                    totalFishCaught.put(uuid, caught);
                }
                if (cfg.isConfigurationSection(key + ".treasure")) {
                    Map<FishingTreasure, Integer> counts = new HashMap<>();
                    for (FishingTreasure t : FishingTreasure.values()) {
                        int count = cfg.getInt(key + ".treasure." + t.name(), 0);
                        if (count > 0) {
                            counts.put(t, count);
                        }
                    }
                    if (!counts.isEmpty()) {
                        treasureCounts.put(uuid, counts);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "fishing.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : fishingXp.entrySet()) {
            cfg.set(entry.getKey().toString() + ".xp", entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : totalFishCaught.entrySet()) {
            cfg.set(entry.getKey().toString() + ".totalFishCaught", entry.getValue());
        }
        for (Map.Entry<UUID, Map<FishingTreasure, Integer>> playerEntry : treasureCounts.entrySet()) {
            String prefix = playerEntry.getKey().toString() + ".treasure.";
            for (Map.Entry<FishingTreasure, Integer> t : playerEntry.getValue().entrySet()) {
                cfg.set(prefix + t.getKey().name(), t.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save fishing.yml", e);
        }
    }
}
