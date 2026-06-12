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
        RAW_FISH,
        RAW_SALMON,
        CLOWNFISH,
        PUFFERFISH,
        INK_SAC,
        LILY_PAD,
        NAUTILUS_SHELL,
        PRISMARINE_CRYSTALS,
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
        new LootEntry(FishType.RAW_FISH,            Material.COD,                  1,  50.0),
        new LootEntry(FishType.RAW_SALMON,          Material.SALMON,               1,  30.0),
        new LootEntry(FishType.CLOWNFISH,           Material.TROPICAL_FISH,        5,  10.0),
        new LootEntry(FishType.PUFFERFISH,          Material.PUFFERFISH,           5,   8.0),
        new LootEntry(FishType.INK_SAC,             Material.INK_SAC,             10,   5.0),
        new LootEntry(FishType.LILY_PAD,            Material.LILY_PAD,            15,   4.0),
        new LootEntry(FishType.NAUTILUS_SHELL,      Material.NAUTILUS_SHELL,      20,   2.0),
        new LootEntry(FishType.PRISMARINE_CRYSTALS, Material.PRISMARINE_CRYSTALS, 30,   1.5),
        new LootEntry(FishType.TREASURE_MAP,        Material.MAP,                 40,   0.5),
    };

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
