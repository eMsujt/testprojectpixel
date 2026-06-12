package com.skyblock.core.achievement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton managing SkyBlock achievements and per-player completion tracking.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class AchievementManager {

    /** A single achievement definition. */
    public static final class Achievement {
        private final String id;
        private final String name;
        private final String description;
        private final int points;

        public Achievement(String id, String name, String description, int points) {
            this.id = Objects.requireNonNull(id, "id");
            this.name = Objects.requireNonNull(name, "name");
            this.description = Objects.requireNonNull(description, "description");
            this.points = points;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getPoints() { return points; }
    }

    private static final AchievementManager INSTANCE = new AchievementManager();

    /** Ordered map of id → Achievement for fast lookup and ordered listing. */
    private final Map<String, Achievement> achievements = new LinkedHashMap<>();
    /** playerId → set of completed achievement ids. */
    private final Map<UUID, Set<String>> completions = new HashMap<>();

    private AchievementManager() {
        register(new Achievement("first_steps",    "First Steps",      "Join SkyBlock for the first time.",          5));
        register(new Achievement("coin_hoarder",   "Coin Hoarder",     "Accumulate 10,000 coins.",                  10));
        register(new Achievement("master_farmer",  "Master Farmer",    "Reach level 10 in Farming.",                20));
        register(new Achievement("master_miner",   "Master Miner",     "Reach level 10 in Mining.",                 20));
        register(new Achievement("master_fisher",  "Master Fisher",    "Reach level 10 in Fishing.",                20));
        register(new Achievement("master_forager", "Master Forager",   "Reach level 10 in Foraging.",               20));
        register(new Achievement("dungeon_novice", "Dungeon Novice",   "Complete your first dungeon run.",           15));
        register(new Achievement("auction_winner", "Auction Winner",   "Win your first auction bid.",                10));
        register(new Achievement("slayer_initiate","Slayer Initiate",  "Complete your first slayer quest.",          15));
        register(new Achievement("bazaar_trader",  "Bazaar Trader",    "Buy or sell an item on the Bazaar.",         5));
        register(new Achievement("pet_lover",      "Pet Lover",        "Equip your first pet.",                      5));
        register(new Achievement("island_builder", "Island Builder",   "Expand your island for the first time.",    10));
        register(new Achievement("collection_tier","Collection Tier",  "Reach tier 2 in any collection.",           10));
        register(new Achievement("fairy_seeker",   "Fairy Seeker",     "Find your first Fairy Soul.",               10));
        register(new Achievement("legendary_crafter","Legendary Crafter","Craft a legendary-tier item.",            25));
    }

    /**
     * Returns the single shared {@code AchievementManager} instance.
     *
     * @return the singleton instance
     */
    public static AchievementManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a custom achievement. Replaces any existing achievement with the same id.
     *
     * @param achievement the achievement to register
     */
    public void register(Achievement achievement) {
        Objects.requireNonNull(achievement, "achievement");
        achievements.put(achievement.getId(), achievement);
    }

    /**
     * Returns the achievement with the given id, or {@code null} if not found.
     *
     * @param id the achievement id (case-insensitive)
     * @return the matching achievement or {@code null}
     */
    public Achievement getAchievement(String id) {
        Objects.requireNonNull(id, "id");
        return achievements.get(id.toLowerCase());
    }

    /** Returns an unmodifiable ordered list of all registered achievements. */
    public List<Achievement> getAllAchievements() {
        return Collections.unmodifiableList(new ArrayList<>(achievements.values()));
    }

    /**
     * Grants an achievement to a player.  Does nothing if already completed.
     *
     * @param playerId      the player's UUID
     * @param achievementId the achievement id
     * @return {@code true} if the achievement was newly granted, {@code false} if already held
     */
    public boolean grant(UUID playerId, String achievementId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(achievementId, "achievementId");
        if (!achievements.containsKey(achievementId.toLowerCase())) {
            return false;
        }
        return completions.computeIfAbsent(playerId, k -> new HashSet<>())
                .add(achievementId.toLowerCase());
    }

    /**
     * Returns whether the player has completed the given achievement.
     *
     * @param playerId      the player's UUID
     * @param achievementId the achievement id
     * @return {@code true} if completed
     */
    public boolean hasCompleted(UUID playerId, String achievementId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(achievementId, "achievementId");
        Set<String> done = completions.get(playerId);
        return done != null && done.contains(achievementId.toLowerCase());
    }

    /**
     * Returns an unmodifiable set of achievement ids completed by the player.
     *
     * @param playerId the player's UUID
     * @return completed achievement ids, never {@code null}
     */
    public Set<String> getCompletedIds(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<String> done = completions.get(playerId);
        return done == null ? Collections.emptySet() : Collections.unmodifiableSet(done);
    }

    /**
     * Returns the total achievement points earned by the player.
     *
     * @param playerId the player's UUID
     * @return total points
     */
    public int getTotalPoints(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<String> done = completions.get(playerId);
        if (done == null || done.isEmpty()) return 0;
        int total = 0;
        for (String id : done) {
            Achievement a = achievements.get(id);
            if (a != null) total += a.getPoints();
        }
        return total;
    }
}
