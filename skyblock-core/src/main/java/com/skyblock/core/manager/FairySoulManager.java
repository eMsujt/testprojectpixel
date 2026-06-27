package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton registry of the fairy souls scattered across each SkyBlock island,
 * tracking how many each player has found and the permanent stat bonuses earned.
 *
 * <p>Each soul is identified by its {@link FairyIsland} and a 1-based index within
 * that island's {@link FairyIsland#soulCount soul count}. Every
 * {@link #SOULS_PER_REWARD} souls a player finds grants the next permanent stat
 * reward from a fixed cycle; see {@link #getStatBonuses(UUID)}.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class FairySoulManager {

    /**
     * Hypixel SkyBlock's total fairy soul count (the wiki states 273). The
     * {@link FairyIsland} enum below maps the currently-placed subset; the
     * remaining souls live on zones not yet generated in-world.
     */
    public static final int MAX_SOULS = 273;

    /** SkyBlock islands that contain fairy souls, each with a display name and total soul count. */
    public enum FairyIsland {
        // Per-area counts from the wiki Fairy Souls Guide; they sum to MAX_SOULS (273).
        HUB("Hub", 80),
        THE_PARK("The Park", 12),
        GOLD_MINE("Gold Mine", 12),
        FARMING_ISLANDS("The Farming Islands", 20),
        SPIDERS_DEN("Spider's Den", 19),
        THE_END("The End", 12),
        DEEP_CAVERNS("Deep Caverns", 21),
        DWARVEN_MINES("Dwarven Mines", 15),
        CRIMSON_ISLE("Crimson Isle", 29),
        JERRYS_WORKSHOP("Jerry's Workshop", 5),
        DUNGEON_HUB("Dungeon Hub", 7),
        GALATEA("Galatea", 12),
        BACKWATER_BAYOU("Backwater Bayou", 5),
        LOTUS_ATOLL("Lotus Atoll", 6),
        THE_RIFT("The Rift", 1),
        MISCELLANEOUS("Miscellaneous", 17);

        private final String displayName;
        private final int soulCount;

        FairyIsland(String displayName, int soulCount) {
            this.displayName = displayName;
            this.soulCount = soulCount;
        }

        /** Returns the human-readable name shown to players. */
        public String getDisplayName() {
            return displayName;
        }

        /** Returns the total number of fairy souls available on this island. */
        public int getSoulCount() {
            return soulCount;
        }
    }

    /** Number of fairy souls a player must find to earn the next permanent stat reward. */
    public static final int SOULS_PER_REWARD = 5;

    /** The permanent stat reward granted at each {@link #SOULS_PER_REWARD}-soul milestone, applied in order and cycling. */
    private static final Stat[] REWARD_STATS = {
            Stat.HEALTH, Stat.DEFENSE, Stat.STRENGTH, Stat.SPEED, Stat.INTELLIGENCE
    };

    /** Amount granted for the matching entry in {@link #REWARD_STATS}. */
    private static final double[] REWARD_AMOUNTS = {3.0, 1.0, 0.5, 1.0, 1.0};

    private static final FairySoulManager INSTANCE = new FairySoulManager();

    /** Per-player set of collected soul keys; absent entries mean no souls found. */
    private final Map<UUID, Set<String>> collected = new HashMap<>();

    private FairySoulManager() {
    }

    /**
     * Returns the single shared {@code FairySoulManager} instance.
     *
     * @return the singleton instance
     */
    public static FairySoulManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the total number of fairy souls across every island.
     *
     * @return the combined soul count of all islands
     */
    public int getTotalSouls() {
        int total = 0;
        for (FairyIsland island : FairyIsland.values()) {
            total += island.soulCount;
        }
        return total;
    }

    /**
     * Records that the given player found the fairy soul at {@code soulIndex} on {@code island}.
     *
     * @param playerId  the player
     * @param island    the island the soul belongs to
     * @param soulIndex the 1-based index of the soul within the island
     * @return {@code true} if the soul was newly found, {@code false} if already found
     * @throws IllegalArgumentException if {@code soulIndex} is outside the island's soul count
     */
    public boolean collectSoul(UUID playerId, FairyIsland island, int soulIndex) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(island, "island");
        if (soulIndex < 1 || soulIndex > island.soulCount) {
            throw new IllegalArgumentException(
                    "soulIndex must be between 1 and " + island.soulCount + " for " + island);
        }
        return collected.computeIfAbsent(playerId, id -> new HashSet<>()).add(key(island, soulIndex));
    }

    /**
     * Returns whether the given player has already found a specific fairy soul.
     *
     * @param playerId  the player
     * @param island    the island the soul belongs to
     * @param soulIndex the 1-based index of the soul within the island
     * @return {@code true} if the soul has been found
     */
    public boolean hasCollected(UUID playerId, FairyIsland island, int soulIndex) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(island, "island");
        Set<String> souls = collected.get(playerId);
        return souls != null && souls.contains(key(island, soulIndex));
    }

    /**
     * Returns the total number of fairy souls the given player has found.
     *
     * @param playerId the player
     * @return the found count, {@code 0} if none
     */
    public int getFoundCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<String> souls = collected.get(playerId);
        return souls == null ? 0 : souls.size();
    }

    /**
     * Returns the number of fairy souls the given player has found on a specific island.
     *
     * @param playerId the player
     * @param island   the island to count
     * @return the found count for that island, {@code 0} if none
     */
    public int getFoundCount(UUID playerId, FairyIsland island) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(island, "island");
        Set<String> souls = collected.get(playerId);
        if (souls == null) {
            return 0;
        }
        int count = 0;
        for (int i = 1; i <= island.soulCount; i++) {
            if (souls.contains(key(island, i))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the permanent stat bonuses the player has earned from found fairy souls.
     *
     * <p>Each {@link #SOULS_PER_REWARD} souls grants the next reward from a fixed cycle;
     * the returned map aggregates every reward earned so far.</p>
     *
     * @param playerId the player
     * @return an unmodifiable map of stat to total bonus, never {@code null}
     */
    public Map<Stat, Double> getStatBonuses(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int rewards = getFoundCount(playerId) / SOULS_PER_REWARD;
        Map<Stat, Double> bonuses = new EnumMap<>(Stat.class);
        for (int i = 0; i < rewards; i++) {
            int slot = i % REWARD_STATS.length;
            bonuses.merge(REWARD_STATS[slot], REWARD_AMOUNTS[slot], Double::sum);
        }
        return Collections.unmodifiableMap(bonuses);
    }

    /**
     * Returns the total permanent Health bonus the player has earned from fairy souls.
     *
     * <p>Mirrors the Hypixel formula: each {@link #SOULS_PER_REWARD}-soul milestone that
     * falls on the {@link Stat#HEALTH} slot in the reward cycle contributes
     * {@code 3} Health.</p>
     *
     * @param playerId the player
     * @return the total Health bonus, {@code 0.0} if no souls found
     */
    public double getHealthBonus(UUID playerId) {
        return getStatBonuses(playerId).getOrDefault(Stat.HEALTH, 0.0);
    }

    /**
     * Clears all fairy soul data for the given player (e.g. on quit or profile reset).
     *
     * @param playerId the player
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean resetPlayer(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return collected.remove(playerId) != null;
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    /**
     * Loads all per-player collected souls from {@code dataFolder/fairysouls.yml}.
     * Each player is stored as a {@code <uuid>} section holding a string list of
     * soul keys (see {@link #key(FairyIsland, int)}).
     *
     * @param dataFolder the plugin's data folder
     */
    public void load(File dataFolder) {
        File file = new File(dataFolder, "fairysouls.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String uuidStr : cfg.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                continue;
            }
            collected.put(uuid, new HashSet<>(cfg.getStringList(uuidStr)));
        }
    }

    /**
     * Saves all in-memory collected souls to {@code dataFolder/fairysouls.yml}.
     *
     * @param dataFolder the plugin's data folder
     */
    public void save(File dataFolder) {
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            return;
        }
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Set<String>> entry : collected.entrySet()) {
            List<String> keys = new ArrayList<>(entry.getValue());
            cfg.set(entry.getKey().toString(), keys);
        }
        try {
            cfg.save(new File(dataFolder, "fairysouls.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String key(FairyIsland island, int soulIndex) {
        return island.name() + "#" + soulIndex;
    }
}
