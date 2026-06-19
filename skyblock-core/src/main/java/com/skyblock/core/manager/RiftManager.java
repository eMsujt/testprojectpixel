package com.skyblock.core.manager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Singleton tracking per-player Rift dimension state.
 *
 * <p>Tracks which area a player is in, how many seconds of Rift time they have
 * remaining, how many Rift mobs they have killed, their motes currency balance,
 * and the timecharms, Rift souls, and Enigma souls they have collected. Not
 * thread-safe.</p>
 */
public final class RiftManager implements Listener {

    /** Named areas inside the Rift dimension. */
    public enum RiftArea {
        DREADFARM, STILLGORE_CHATEAU, WYLD_WOODS, LIVING_CAVE, MIRRORVERSE, LAGOON, COLOSSEUM
    }

    /** Mob types that inhabit the Rift. */
    public enum RiftMobType {
        BACTE, BLOBBERCYST, CRUX, RIFT_WEIRDO, SOULFLOW_ENGINE, VOLT
    }

    /** Immutable snapshot of a player's current Rift state. */
    public static final class RiftData {
        public final boolean inRift;
        public final RiftArea zone;
        public final long timeRemainingSeconds;
        public final Map<RiftMobType, Integer> kills;
        public final long motes;
        public final int timecharms;
        public final int riftSouls;
        public final int enigmaSouls;

        public RiftData(boolean inRift, RiftArea zone, long timeRemainingSeconds,
                        Map<RiftMobType, Integer> kills, long motes,
                        int timecharms, int riftSouls, int enigmaSouls) {
            this.inRift = inRift;
            this.zone = zone;
            this.timeRemainingSeconds = timeRemainingSeconds;
            this.kills = Map.copyOf(kills);
            this.motes = motes;
            this.timecharms = timecharms;
            this.riftSouls = riftSouls;
            this.enigmaSouls = enigmaSouls;
        }
    }

    private static final long DEFAULT_TIME_SECONDS = 480L;

    /** The total number of Enigma souls hidden across the Rift. */
    public static final int ENIGMA_SOUL_TOTAL = 42;

    /**
     * The maximum motes a player's purse can hold. Motes earned beyond this cap
     * decay away rather than accumulating.
     */
    public static final long MOTES_PURSE_CAP = 4000L;

    private static final RiftManager INSTANCE = new RiftManager();

    private final Map<UUID, Boolean> inRift = new HashMap<>();
    private final Map<UUID, RiftArea> currentZone = new HashMap<>();
    private final Map<UUID, Long> timeRemaining = new HashMap<>();
    private final Map<UUID, Map<RiftMobType, Integer>> mobKills = new HashMap<>();
    private final Map<UUID, Long> motes = new HashMap<>();
    private final Map<UUID, Set<String>> timecharms = new HashMap<>();
    private final Map<UUID, Set<String>> riftSouls = new HashMap<>();
    private final Map<UUID, Set<Integer>> enigmaSouls = new HashMap<>();

    private RiftManager() {}

    /**
     * Returns the single shared {@code RiftManager} instance.
     *
     * @return the singleton instance
     */
    public static RiftManager getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        exitRift(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        exitRift(event.getPlayer().getUniqueId());
    }

    /**
     * Marks a player as having entered the Rift, placing them in the given zone
     * with the default time allocation.
     *
     * @param playerId the player entering the Rift
     * @param zone     the starting zone
     */
    public void enterRift(UUID playerId, RiftArea zone) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(zone, "zone");
        inRift.put(playerId, true);
        currentZone.put(playerId, zone);
        timeRemaining.put(playerId, DEFAULT_TIME_SECONDS);
    }

    /**
     * Removes the player from the Rift, clearing their zone but preserving kill
     * counts so progress survives re-entry.
     *
     * @param playerId the player leaving the Rift
     * @return {@code true} if the player was actually in the Rift
     */
    public boolean exitRift(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Boolean was = inRift.remove(playerId);
        currentZone.remove(playerId);
        timeRemaining.remove(playerId);
        return Boolean.TRUE.equals(was);
    }

    /**
     * Records a kill for the given mob type and decrements the player's
     * remaining time by {@code timeCostSeconds}.
     *
     * @param playerId       the player who made the kill
     * @param type           the mob type killed
     * @param timeCostSeconds seconds to deduct from the player's Rift timer
     * @return the player's total kill count for that mob type after the addition
     */
    public int addKill(UUID playerId, RiftMobType type, long timeCostSeconds) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<RiftMobType, Integer> kills = mobKills.computeIfAbsent(
                playerId, id -> new EnumMap<>(RiftMobType.class));
        int newCount = kills.merge(type, 1, Integer::sum);
        if (timeCostSeconds > 0) {
            long current = timeRemaining.getOrDefault(playerId, 0L);
            timeRemaining.put(playerId, Math.max(0L, current - timeCostSeconds));
        }
        return newCount;
    }

    /**
     * Returns the seconds of Rift time remaining for a player.
     *
     * @param playerId the player to look up
     * @return remaining seconds, or {@code 0} if the player is not in the Rift
     */
    public long getTimeRemaining(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return timeRemaining.getOrDefault(playerId, 0L);
    }

    /**
     * Returns a snapshot of the player's current Rift state.
     *
     * @param playerId the player to look up
     * @return a {@link RiftData} snapshot (never {@code null})
     */
    public RiftData getRiftData(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean active = Boolean.TRUE.equals(inRift.get(playerId));
        RiftArea zone = currentZone.get(playerId);
        long time = timeRemaining.getOrDefault(playerId, 0L);
        Map<RiftMobType, Integer> kills = mobKills.getOrDefault(
                playerId, new EnumMap<>(RiftMobType.class));
        return new RiftData(active, zone, time, kills, getMotes(playerId),
                getTimecharmCount(playerId), getRiftSoulCount(playerId),
                getEnigmaSoulCount(playerId));
    }

    /**
     * Returns the player's motes balance, the Rift's spendable currency.
     *
     * @param playerId the player to look up
     * @return the motes balance, or {@code 0} if the player has none
     */
    public long getMotes(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return motes.getOrDefault(playerId, 0L);
    }

    /**
     * Credits motes to a player, capping the resulting balance at
     * {@link #MOTES_PURSE_CAP}. Motes that would exceed the cap decay away and
     * are not stored.
     *
     * @param playerId the player to credit
     * @param amount   the number of motes to add (must be non-negative)
     * @return the player's motes balance after the credit, never exceeding the cap
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addMotes(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
        long capped = Math.min(MOTES_PURSE_CAP, getMotes(playerId) + amount);
        motes.put(playerId, capped);
        return capped;
    }

    /**
     * Deducts motes from a player if they can afford it.
     *
     * @param playerId the player to charge
     * @param amount   the number of motes to spend (must be non-negative)
     * @return {@code true} if the player had enough motes and was charged
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public boolean spendMotes(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
        long balance = getMotes(playerId);
        if (balance < amount) {
            return false;
        }
        motes.put(playerId, balance - amount);
        return true;
    }

    /**
     * Records that the player obtained a timecharm.
     *
     * @param playerId the player
     * @param id       the timecharm identifier
     * @return {@code true} if the timecharm was newly obtained
     */
    public boolean collectTimecharm(UUID playerId, String id) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(id, "id");
        return timecharms.computeIfAbsent(playerId, k -> new HashSet<>()).add(id);
    }

    /**
     * Returns the number of distinct timecharms the player has obtained.
     *
     * @param playerId the player
     * @return the timecharm count, {@code 0} if none
     */
    public int getTimecharmCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<String> owned = timecharms.get(playerId);
        return owned == null ? 0 : owned.size();
    }

    /**
     * Records that the player collected a Rift soul.
     *
     * @param playerId the player
     * @param id       the Rift soul identifier
     * @return {@code true} if the Rift soul was newly collected
     */
    public boolean collectRiftSoul(UUID playerId, String id) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(id, "id");
        return riftSouls.computeIfAbsent(playerId, k -> new HashSet<>()).add(id);
    }

    /**
     * Returns the number of distinct Rift souls the player has collected.
     *
     * @param playerId the player
     * @return the Rift soul count, {@code 0} if none
     */
    public int getRiftSoulCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<String> owned = riftSouls.get(playerId);
        return owned == null ? 0 : owned.size();
    }

    /**
     * Records that the player collected the Enigma soul at the given index.
     *
     * @param playerId  the player
     * @param soulIndex the 1-based Enigma soul index
     * @return {@code true} if the Enigma soul was newly collected
     * @throws IllegalArgumentException if {@code soulIndex} is outside 1..{@link #ENIGMA_SOUL_TOTAL}
     */
    public boolean collectEnigmaSoul(UUID playerId, int soulIndex) {
        Objects.requireNonNull(playerId, "playerId");
        if (soulIndex < 1 || soulIndex > ENIGMA_SOUL_TOTAL) {
            throw new IllegalArgumentException(
                    "soulIndex must be between 1 and " + ENIGMA_SOUL_TOTAL);
        }
        return enigmaSouls.computeIfAbsent(playerId, k -> new HashSet<>()).add(soulIndex);
    }

    /**
     * Returns the number of Enigma souls the player has collected.
     *
     * @param playerId the player
     * @return the Enigma soul count, {@code 0} if none
     */
    public int getEnigmaSoulCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<Integer> owned = enigmaSouls.get(playerId);
        return owned == null ? 0 : owned.size();
    }

    /**
     * Resets all Rift data for the given player.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any Rift data
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = inRift.remove(playerId) != null;
        hadData |= currentZone.remove(playerId) != null;
        hadData |= timeRemaining.remove(playerId) != null;
        hadData |= mobKills.remove(playerId) != null;
        hadData |= motes.remove(playerId) != null;
        hadData |= timecharms.remove(playerId) != null;
        hadData |= riftSouls.remove(playerId) != null;
        hadData |= enigmaSouls.remove(playerId) != null;
        return hadData;
    }
}
