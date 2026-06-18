package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
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
 * Singleton tracking mayor candidates, the election cycle/voting, and the active mayor.
 *
 * <p>Each active mayor grants stat perks that apply to every player while in office;
 * see {@link #getActiveStatBonuses()} and {@link #applyPerks(Map)}.</p>
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
        MARINA("Marina", "Fishing Festival", "Luck of the Sea", "Quiver", "Water Breathing"),
        FOXY("Foxy", "What the Dog Doin?", "Extra Pets", "Good Doggy"),
        AATROX("Aatrox", "Slayer XP Buff", "Slayer Quest Limit", "Slayer's Will", "Blood Thirst"),
        DIAZ("Diaz", "Free Samples", "Barrier Street", "Inflation"),
        DERPY("Derpy", "TIME = MONEY!", "AAUUTOMATED!", "MOAR SKILLZ!!!");

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

    /** Stat bonuses each mayor grants to every player while active. */
    public static final Map<MayorCandidate, Map<Stat, Double>> MAYOR_STAT_PERKS;

    static {
        Map<MayorCandidate, Map<Stat, Double>> m = new EnumMap<>(MayorCandidate.class);
        m.put(MayorCandidate.PAUL, statBonus(Stat.STRENGTH, 25.0, Stat.DEFENSE, 25.0));
        m.put(MayorCandidate.DIANA, statBonus(Stat.PET_LUCK, 10.0, Stat.MAGIC_FIND, 10.0));
        m.put(MayorCandidate.JERRY, statBonus(Stat.MAGIC_FIND, 5.0));
        m.put(MayorCandidate.SCORPIUS, statBonus(Stat.MAGIC_FIND, 15.0, Stat.ABILITY_DAMAGE, 10.0));
        m.put(MayorCandidate.COLE, statBonus(Stat.MINING_SPEED, 100.0, Stat.MINING_FORTUNE, 50.0));
        m.put(MayorCandidate.FINNEGAN, statBonus(Stat.FARMING_FORTUNE, 50.0));
        m.put(MayorCandidate.MARINA, statBonus(Stat.FISHING_SPEED, 50.0, Stat.SEA_CREATURE_CHANCE, 5.0));
        m.put(MayorCandidate.FOXY, statBonus(Stat.SPEED, 20.0, Stat.PET_LUCK, 7.0));
        m.put(MayorCandidate.AATROX, statBonus(Stat.STRENGTH, 30.0, Stat.FEROCITY, 10.0));
        m.put(MayorCandidate.DIAZ, statBonus(Stat.INTELLIGENCE, 10.0));
        m.put(MayorCandidate.DERPY, statBonus(Stat.SPEED, 10.0));
        MAYOR_STAT_PERKS = Collections.unmodifiableMap(m);
    }

    private static Map<Stat, Double> statBonus(Object... pairs) {
        Map<Stat, Double> bonuses = new EnumMap<>(Stat.class);
        for (int i = 0; i < pairs.length; i += 2) {
            bonuses.put((Stat) pairs[i], (Double) pairs[i + 1]);
        }
        return Collections.unmodifiableMap(bonuses);
    }

    /** Length of a full Skyblock election cycle, in days. */
    public static final int ELECTION_CYCLE_DAYS = 53;

    private static final MayorManager INSTANCE = new MayorManager();

    /** The currently active mayor (null if none set). */
    private MayorCandidate currentMayor;

    /** Number of elections that have been run so far. */
    private int electionCycle;

    /** Day within the current 53-day cycle, in the range {@code [0, ELECTION_CYCLE_DAYS)}. */
    private int cycleDay;

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
     * Returns the number of elections that have been run so far.
     *
     * @return the election cycle count
     */
    public int getElectionCycle() {
        return electionCycle;
    }

    /**
     * Returns the current day within the 53-day election cycle, in {@code [0, ELECTION_CYCLE_DAYS)}.
     *
     * @return the current cycle day
     */
    public int getCycleDay() {
        return cycleDay;
    }

    /**
     * Returns the number of days remaining until the next election in the current cycle.
     *
     * @return the days until the next election
     */
    public int getDaysUntilElection() {
        return ELECTION_CYCLE_DAYS - cycleDay;
    }

    /**
     * Advances the cycle by one day. When the 53-day cycle completes, the day counter resets
     * and an election is automatically run.
     *
     * @return the newly elected mayor if the cycle completed, otherwise {@code null}
     */
    public MayorCandidate advanceDay() {
        cycleDay++;
        if (cycleDay >= ELECTION_CYCLE_DAYS) {
            cycleDay = 0;
            return runElection();
        }
        return null;
    }

    /**
     * Tallies the current votes, returning the number of votes cast per candidate.
     * Candidates with no votes are omitted.
     *
     * @return a map of candidate to vote count
     */
    public Map<MayorCandidate, Integer> tallyVotes() {
        Map<MayorCandidate, Integer> tally = new EnumMap<>(MayorCandidate.class);
        for (MayorCandidate vote : playerVotes.values()) {
            tally.merge(vote, 1, Integer::sum);
        }
        return tally;
    }

    /**
     * Runs an election: elects the candidate with the most votes as the active mayor,
     * advances the election cycle, and clears all cast votes for the next cycle.
     * Ties are broken by the candidate's declaration order.
     *
     * @return the newly elected mayor, or {@code null} if no votes were cast
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
        electionCycle++;
        playerVotes.clear();
        if (winner != null) {
            setCurrentMayor(winner);
        }
        return winner;
    }

    // -------------------------------------------------------------------------
    // Active-mayor perks
    // -------------------------------------------------------------------------

    /**
     * Returns the stat bonuses granted by the active mayor, or an empty map if no
     * mayor is currently in office.
     *
     * @return an unmodifiable map of stat to bonus value
     */
    public Map<Stat, Double> getActiveStatBonuses() {
        if (currentMayor == null) {
            return Collections.emptyMap();
        }
        return MAYOR_STAT_PERKS.getOrDefault(currentMayor, Collections.emptyMap());
    }

    /**
     * Applies the active mayor's stat perks on top of the given base stats, returning a
     * new map. The input map is not modified; missing stats are treated as zero.
     *
     * @param baseStats the player's base stats before mayor perks
     * @return a new map with the active mayor's bonuses added in
     */
    public Map<Stat, Double> applyPerks(Map<Stat, Double> baseStats) {
        Objects.requireNonNull(baseStats, "baseStats");
        Map<Stat, Double> result = new EnumMap<>(Stat.class);
        result.putAll(baseStats);
        for (Map.Entry<Stat, Double> bonus : getActiveStatBonuses().entrySet()) {
            result.merge(bonus.getKey(), bonus.getValue(), Double::sum);
        }
        return result;
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
        electionCycle = cfg.getInt("electionCycle", 0);
        cycleDay = cfg.getInt("cycleDay", 0);
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
        cfg.set("electionCycle", electionCycle);
        cfg.set("cycleDay", cycleDay);
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
