package com.skyblock.core.mayor;

import com.skyblock.core.model.Stat;
import com.skyblock.core.stat.StatManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
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

    /**
     * Stat bonuses each mayor grants to every player while they hold office.
     * Mayors whose perks are not stat-based are simply absent from this map.
     */
    private static final Map<MayorCandidate, Map<Stat, Double>> PERK_BUFFS;

    static {
        Map<MayorCandidate, Map<Stat, Double>> buffs = new EnumMap<>(MayorCandidate.class);
        buffs.put(MayorCandidate.PAUL, statBuff(Stat.STRENGTH, 50.0));
        buffs.put(MayorCandidate.DIANA, statBuff(Stat.MAGIC_FIND, 25.0));
        buffs.put(MayorCandidate.COLE, statBuff(Stat.MINING_SPEED, 200.0, Stat.MINING_FORTUNE, 50.0));
        buffs.put(MayorCandidate.FINNEGAN, statBuff(Stat.FARMING_FORTUNE, 30.0));
        buffs.put(MayorCandidate.MARINA, statBuff(Stat.FISHING_SPEED, 30.0, Stat.SEA_CREATURE_CHANCE, 4.0));
        buffs.put(MayorCandidate.FOXY, statBuff(Stat.PET_LUCK, 25.0));
        PERK_BUFFS = Collections.unmodifiableMap(buffs);
    }

    /** Builds an immutable {@code Stat -> bonus} map from {@code stat, amount} pairs. */
    private static Map<Stat, Double> statBuff(Object... pairs) {
        Map<Stat, Double> map = new EnumMap<>(Stat.class);
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((Stat) pairs[i], (Double) pairs[i + 1]);
        }
        return Collections.unmodifiableMap(map);
    }

    private static final MayorManager INSTANCE = new MayorManager();

    /** The currently active mayor (null if none set). */
    private MayorCandidate currentMayor;

    /** Per-player vote, keyed by player UUID. */
    private final Map<UUID, MayorCandidate> playerVotes = new HashMap<>();

    /** Per-player mayor event history. */
    private final Map<UUID, List<String>> mayorHistory = new HashMap<>();

    /** Global election event history. */
    private final List<String> electionHistory = new ArrayList<>();

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
        if (mayor != null) {
            recordElectionEvent("Mayor elected: " + mayor.getDisplayName());
        }
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
    // Election cycle
    // -------------------------------------------------------------------------

    /**
     * Tallies every cast vote.
     *
     * @return a count of votes per candidate; candidates with no votes are omitted
     */
    public Map<MayorCandidate, Integer> tallyVotes() {
        Map<MayorCandidate, Integer> tally = new EnumMap<>(MayorCandidate.class);
        for (MayorCandidate vote : playerVotes.values()) {
            tally.merge(vote, 1, Integer::sum);
        }
        return tally;
    }

    /**
     * Runs an election: tallies all votes, makes the candidate with the most votes
     * the active mayor (recording it in the election history), then clears every
     * vote so the next cycle starts fresh. Ties are broken by enum declaration order.
     *
     * @return the elected mayor, or {@code null} if no votes had been cast
     */
    public MayorCandidate runElection() {
        Map<MayorCandidate, Integer> tally = tallyVotes();
        MayorCandidate winner = null;
        int best = 0;
        for (MayorCandidate candidate : MayorCandidate.values()) {
            int votes = tally.getOrDefault(candidate, 0);
            if (votes > best) {
                best = votes;
                winner = candidate;
            }
        }
        if (winner != null) {
            setCurrentMayor(winner);
            playerVotes.clear();
        }
        return winner;
    }

    // -------------------------------------------------------------------------
    // Active-mayor perk effects
    // -------------------------------------------------------------------------

    /**
     * Returns the stat bonuses granted by the given mayor's perks.
     *
     * @param mayor the mayor to inspect
     * @return an unmodifiable {@code Stat -> bonus} map, empty if the mayor grants no stats
     */
    public static Map<Stat, Double> getPerkBuffs(MayorCandidate mayor) {
        Objects.requireNonNull(mayor, "mayor");
        return PERK_BUFFS.getOrDefault(mayor, Collections.emptyMap());
    }

    /**
     * Applies the active mayor's perk stat bonuses to a player via the {@link StatManager}.
     * No-op if there is no active mayor or the mayor grants no stat buffs.
     *
     * @param playerId the player to buff
     */
    public void applyPerks(UUID playerId) {
        adjustPerks(playerId, 1);
    }

    /**
     * Removes the active mayor's perk stat bonuses from a player (the inverse of
     * {@link #applyPerks(UUID)}). No-op if there is no active mayor.
     *
     * @param playerId the player to un-buff
     */
    public void removePerks(UUID playerId) {
        adjustPerks(playerId, -1);
    }

    private void adjustPerks(UUID playerId, int sign) {
        Objects.requireNonNull(playerId, "playerId");
        if (currentMayor == null) {
            return;
        }
        StatManager stats = StatManager.getInstance();
        for (Map.Entry<Stat, Double> entry : getPerkBuffs(currentMayor).entrySet()) {
            stats.addBonus(playerId, entry.getKey(), sign * entry.getValue());
        }
    }

    // -------------------------------------------------------------------------
    // Mayor history
    // -------------------------------------------------------------------------

    public void recordMayorEvent(UUID playerId, String summary) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(summary, "summary");
        mayorHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getMayorHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return Collections.unmodifiableList(mayorHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllMayorHistory() {
        return Collections.unmodifiableMap(mayorHistory);
    }

    public void recordElectionEvent(String summary) {
        Objects.requireNonNull(summary, "summary");
        electionHistory.add(summary);
    }

    public List<String> getElectionHistory() {
        return Collections.unmodifiableList(electionHistory);
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
        mayorHistory.remove(playerId);
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
        mayorHistory.clear();
        electionHistory.clear();
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
        if (cfg.isConfigurationSection("mayorHistory")) {
            for (String key : cfg.getConfigurationSection("mayorHistory").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    List<String> entries = cfg.getStringList("mayorHistory." + key);
                    if (!entries.isEmpty()) {
                        mayorHistory.put(uuid, new ArrayList<>(entries));
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
        List<String> savedElectionHistory = cfg.getStringList("electionHistory");
        if (!savedElectionHistory.isEmpty()) {
            electionHistory.addAll(savedElectionHistory);
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
        for (Map.Entry<UUID, List<String>> entry : mayorHistory.entrySet()) {
            cfg.set("mayorHistory." + entry.getKey().toString(), entry.getValue());
        }
        if (!electionHistory.isEmpty()) {
            cfg.set("electionHistory", electionHistory);
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save mayor.yml", e);
        }
    }
}
