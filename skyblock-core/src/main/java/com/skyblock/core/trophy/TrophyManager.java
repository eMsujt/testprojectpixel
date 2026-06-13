package com.skyblock.core.trophy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock trophy fish collection.
 *
 * <p>Holds the static {@link TrophyType} catalogue and tracks each player's
 * unlocked trophies.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class TrophyManager {

    public enum TrophyType {
        // Kill / combat trophies
        FIRST_KILL(            "First Kill",              "Kill your first mob."),
        SLAYER_INITIATE(       "Slayer Initiate",         "Complete your first slayer quest."),
        DRAGON_SLAYER(         "Dragon Slayer",           "Defeat an Ender Dragon."),
        BEAST_TAMER(           "Beast Tamer",             "Defeat 100 combat mobs."),
        DUNGEON_NOVICE(        "Dungeon Novice",          "Complete a dungeon floor for the first time."),
        DUNGEON_MASTER(        "Dungeon Master",          "Complete Floor 7 of the Catacombs."),
        WITHER_WARRIOR(        "Wither Warrior",          "Defeat the Wither King."),
        VAMPIRE_HUNTER(        "Vampire Hunter",          "Complete a Vampire Slayer quest."),
        BLAZE_BANE(            "Blaze Bane",              "Complete a Blaze Slayer quest."),
        SPIDER_EXTERMINATOR(   "Spider Exterminator",     "Complete a Spider Slayer quest."),
        WOLF_PACK_DESTROYER(   "Wolf Pack Destroyer",     "Complete a Wolf Slayer quest."),
        ENDGAME_INITIATE(      "Endgame Initiate",        "Complete an Enderman Slayer quest."),
        ZOMBIE_SLAYER_NOVICE(  "Zombie Slayer Novice",    "Complete a Zombie Slayer quest."),
        KUUDRA_CONQUEROR(      "Kuudra Conqueror",        "Defeat Kuudra for the first time."),

        // Fishing trophies
        FIRST_CATCH(           "First Catch",             "Catch your first fish."),
        TROPHY_FISH_COLLECTOR( "Trophy Fish Collector",   "Catch your first trophy fish."),
        LAVA_FISHER(           "Lava Fisher",             "Catch a fish in lava."),
        DEEP_SEA_DIVER(        "Deep Sea Diver",          "Catch 50 rare fish."),
        MASTER_ANGLER(         "Master Angler",           "Catch 500 fish total."),
        GREAT_WHITE_HUNTER(    "Great White Hunter",      "Catch a Sea Creature 10 times."),
        MYTHIC_FISHER(         "Mythic Fisher",           "Catch a mythic trophy fish."),

        // Mining / collection trophies
        FIRST_ORE(             "First Ore",               "Mine your first ore."),
        MITHRIL_MINER(         "Mithril Miner",           "Mine 1,000 Mithril Ore."),
        GEMSTONE_COLLECTOR(    "Gemstone Collector",      "Collect all six gemstone types."),
        CRYSTAL_HUNTER(        "Crystal Hunter",          "Unlock all Crystal Hollows crystals."),
        HOTM_DEVOTEE(          "Heart of the Mountain Devotee", "Reach HOTM level 7."),

        // Farming trophies
        FIRST_HARVEST(         "First Harvest",           "Harvest your first crop."),
        GREEN_THUMB(           "Green Thumb",             "Harvest 10,000 crops total."),
        GARDEN_MASTER(         "Garden Master",           "Reach max plot tier for every crop."),
        JACOBS_LEGEND(         "Jacob's Legend",          "Earn a Diamond medal in Jacob's Contest."),

        // Skill / progression trophies
        SKILL_MILESTONE(       "Skill Milestone",         "Reach level 25 in any skill."),
        MAXED_SKILL(           "Maxed Skill",             "Reach the maximum level in any skill."),
        ALL_ROUNDER(           "All-Rounder",             "Reach level 10 in every skill."),

        // Social / miscellaneous trophies
        FIRST_TRADE(           "First Trade",             "Complete your first player trade."),
        MINION_MOGUL(          "Minion Mogul",            "Place 25 unique minion types."),
        PET_LOVER(             "Pet Lover",               "Unlock 10 different pets."),
        AUCTION_WINNER(        "Auction Winner",          "Win your first auction."),
        BAZAAR_BARON(          "Bazaar Baron",            "Buy or sell 100 items on the Bazaar.");

        private final String displayName;
        private final String description;

        TrophyType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }

    // ---------------------------------------------------------------------------
    // Trophy catalogue
    // ---------------------------------------------------------------------------

    private static final Map<String, TrophyType> BY_ID;

    static {
        Map<String, TrophyType> m = new HashMap<>();
        for (TrophyType t : TrophyType.values()) {
            m.put(t.name().toLowerCase(), t);
        }
        BY_ID = Collections.unmodifiableMap(m);
    }

    // ---------------------------------------------------------------------------
    // Singleton + state
    // ---------------------------------------------------------------------------

    private static final TrophyManager INSTANCE = new TrophyManager();

    /** Trophies unlocked per player. */
    private final Map<UUID, Set<TrophyType>> playerTrophies = new HashMap<>();

    private TrophyManager() {}

    /**
     * Returns the single shared {@code TrophyManager} instance.
     *
     * @return the singleton instance
     */
    public static TrophyManager getInstance() {
        return INSTANCE;
    }

    // ---------------------------------------------------------------------------
    // Catalogue access
    // ---------------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of all trophies, keyed by lower-case enum name.
     *
     * @return the trophy catalogue
     */
    public Map<String, TrophyType> getTrophies() {
        return BY_ID;
    }

    /**
     * Returns the trophy with the given ID, or {@code null} if unknown.
     *
     * @param id trophy identifier (case-insensitive enum name)
     * @return matching {@link TrophyType}, or {@code null}
     */
    public TrophyType getTrophy(String id) {
        return BY_ID.get(id.toLowerCase());
    }

    // ---------------------------------------------------------------------------
    // Per-player trophy tracking
    // ---------------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of the trophies unlocked by the player.
     *
     * @param playerId the player to look up
     * @return set of unlocked {@link TrophyType} values
     */
    public Set<TrophyType> getUnlockedTrophies(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<TrophyType> set = playerTrophies.get(playerId);
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    /**
     * Unlocks a trophy for the player.
     *
     * @param playerId the player receiving the trophy
     * @param trophy   the trophy to unlock
     * @return {@code true} if this is a newly unlocked trophy, {@code false} if already owned
     */
    public boolean unlockTrophy(UUID playerId, TrophyType trophy) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(trophy, "trophy");
        return playerTrophies.computeIfAbsent(playerId, k -> new HashSet<>()).add(trophy);
    }

    /**
     * Returns whether the player has unlocked the given trophy.
     *
     * @param playerId the player to check
     * @param trophy   the trophy to query
     * @return {@code true} if the player owns this trophy
     */
    public boolean hasTrophy(UUID playerId, TrophyType trophy) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(trophy, "trophy");
        Set<TrophyType> set = playerTrophies.get(playerId);
        return set != null && set.contains(trophy);
    }

    /**
     * Resets all trophies for the player.
     *
     * @param playerId the player whose trophies to reset
     */
    public void resetTrophies(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerTrophies.remove(playerId);
    }
}
