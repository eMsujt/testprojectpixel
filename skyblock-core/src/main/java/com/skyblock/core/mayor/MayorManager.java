package com.skyblock.core.mayor;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking the active mayor and each player's mayor vote.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MayorManager {

    /** Skyblock mayors that players can vote for. */
    public enum MayorCandidate {
        PAUL("Paul", "Marauder", "Goblin Raid", "Supply Drop", "Show Off"),
        DIANA("Diana", "Great Spook", "Mythological Ritual", "Lucky!"),
        JERRY("Jerry", "Jerrypocalypse", "Jerry's Gifts", "Gift Hunt"),
        SCORPIUS("Scorpius", "Bribe", "Scorched", "Plague"),
        COLE("Cole", "Prospection", "Mining Fiesta", "Molten Forge"),
        FINNEGAN("Finnegan", "Cultivation", "Shining Armor", "Stead Fast", "Blooming Business"),
        BARRY("Barry", "Bail Out", "Catch of the Day", "Crime Wave"),
        MARINA("Marina", "Fishing Festival", "Luck of the Sea", "Quiver", "Water Breathing"),
        FOXY("Foxy", "What the Dog Doin?", "Extra Pets", "Good Doggy"),
        AATROX("Aatrox", "Slayer XP Buff", "Slayer Quest Limit", "Slayer's Will", "Blood Thirst"),
        DIAZ("Diaz", "Free Samples", "Barrier Street", "Inflation");

        /** Human-readable display name shown to players. */
        public final String displayName;
        /** List of perk names this mayor provides. */
        public final List<String> perks;

        MayorCandidate(String displayName, String... perks) {
            this.displayName = displayName;
            this.perks = Collections.unmodifiableList(Arrays.asList(perks));
        }

        public String getDisplayName() {
            return displayName;
        }

        public List<String> getPerks() {
            return perks;
        }
    }

    private static final MayorManager INSTANCE = new MayorManager();

    /** The currently active mayor (null if none set). */
    private MayorCandidate currentMayor;

    /** Per-player vote, keyed by player UUID. */
    private final Map<UUID, MayorCandidate> playerVotes = new HashMap<>();

    private MayorManager() {
    }

    /**
     * Returns the single shared {@code MayorManager} instance.
     *
     * @return the singleton instance
     */
    public static MayorManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Current mayor
    // -------------------------------------------------------------------------

    /**
     * Returns the currently active mayor, or {@code null} if none is set.
     *
     * @return the active mayor
     */
    public MayorCandidate getCurrentMayor() {
        return currentMayor;
    }

    /**
     * Sets the currently active mayor.
     *
     * @param mayor the mayor to set (may be {@code null} to clear)
     */
    public void setCurrentMayor(MayorCandidate mayor) {
        this.currentMayor = mayor;
    }

    // -------------------------------------------------------------------------
    // Player votes
    // -------------------------------------------------------------------------

    /**
     * Records a player's vote for the given mayor.
     *
     * @param playerId the player casting the vote
     * @param mayor    the mayor being voted for
     */
    public void vote(UUID playerId, MayorCandidate mayor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(mayor, "mayor");
        playerVotes.put(playerId, mayor);
    }

    /**
     * Returns the mayor the player voted for, or {@code null} if they have not voted.
     *
     * @param playerId the player to look up
     * @return the voted-for mayor, or {@code null}
     */
    public MayorCandidate getVote(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerVotes.get(playerId);
    }

    /**
     * Clears the player's vote.
     *
     * @param playerId the player whose vote to clear
     * @return {@code true} if the player had voted, {@code false} otherwise
     */
    public boolean clearVote(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerVotes.remove(playerId) != null;
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Removes all data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerVotes.remove(playerId) != null;
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "mayor.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerVotes.clear();
        currentMayor = null;
        String mayorName = cfg.getString("currentMayor");
        if (mayorName != null) {
            try {
                currentMayor = MayorCandidate.valueOf(mayorName);
            } catch (IllegalArgumentException ignored) {
                // skip unknown mayor name
            }
        }
        if (cfg.isConfigurationSection("votes")) {
            for (String key : cfg.getConfigurationSection("votes").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String voteName = cfg.getString("votes." + key);
                    if (voteName != null) {
                        try {
                            playerVotes.put(uuid, MayorCandidate.valueOf(voteName));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown mayor name
                        }
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "mayor.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        if (currentMayor != null) {
            cfg.set("currentMayor", currentMayor.name());
        }
        for (Map.Entry<UUID, MayorCandidate> entry : playerVotes.entrySet()) {
            cfg.set("votes." + entry.getKey().toString(), entry.getValue().name());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save mayor.yml", e);
        }
    }
}
