package com.skyblock.core.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton manager for the SkyBlock Dwarven Mines and Crystal Hollows areas.
 *
 * <p>Tracks which {@link CrystalHollowsZone} each player is currently exploring,
 * records per-player crystal collection counts, and holds each player's mined
 * powder balances.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class CrystalHollowsManager {

    /** Sub-zones within the Dwarven Mines and the Crystal Hollows. */
    public enum CrystalHollowsZone {
        // Dwarven Mines
        DWARVEN_VILLAGE("Dwarven Village"),
        ROYAL_MINES("Royal Mines"),
        CLIFFSIDE_VEINS("Cliffside Veins"),
        FORGE_BASIN("Forge Basin"),
        RAMPARTS_QUARRY("Rampart's Quarry"),
        // Crystal Hollows
        CRYSTAL_NUCLEUS("Crystal Nucleus"),
        JUNGLE("Jungle"),
        GOBLIN_HOLDOUT("Goblin Holdout"),
        PRECURSOR_REMNANTS("Precursor Remnants"),
        MAGMA_FIELDS("Magma Fields"),
        MITHRIL_DEPOSITS("Mithril Deposits");

        private final String displayName;

        CrystalHollowsZone(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /** Qualities a mined gemstone can have, in ascending order of value. */
    public enum GemstoneQuality {
        ROUGH("Rough"),
        FLAWED("Flawed"),
        FINE("Fine"),
        FLAWLESS("Flawless"),
        PERFECT("Perfect");

        private final String displayName;

        GemstoneQuality(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /** Powder currencies earned from mining in the Mines and Hollows. */
    public enum PowderType {
        MITHRIL("Mithril Powder"),
        GEMSTONE("Gemstone Powder"),
        GLACITE("Glacite Powder");

        private final String displayName;

        PowderType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /**
     * Crystals that can be collected in the Crystal Hollows.
     *
     * <p>The five {@linkplain #isNucleusCrystal() nucleus crystals} (Jade, Amber,
     * Topaz, Sapphire and Amethyst) can be placed into the Crystal Nucleus to
     * grant their {@linkplain #getBuff() buff}; the remaining crystals are
     * collectibles with no nucleus buff.</p>
     */
    public enum CrystalType {
        JADE("Jade Crystal", true, "+12 Gemstone Powder per gemstone mined"),
        AMBER("Amber Crystal", true, "+20 Mithril Powder per Mithril mined"),
        TOPAZ("Topaz Crystal", true, "+30% bonus Mining XP from ores"),
        SAPPHIRE("Sapphire Crystal", true, "+50 Intelligence while in the Hollows"),
        AMETHYST("Amethyst Crystal", true, "+100 Mining Speed while in the Hollows"),
        JASPER("Jasper Crystal", false, null),
        RUBY("Ruby Crystal", false, null),
        OPAL("Opal Crystal", false, null);

        private final String displayName;
        private final boolean nucleusCrystal;
        private final String buff;

        CrystalType(String displayName, boolean nucleusCrystal, String buff) {
            this.displayName = displayName;
            this.nucleusCrystal = nucleusCrystal;
            this.buff = buff;
        }

        public String getDisplayName() { return displayName; }

        /** @return {@code true} if this crystal can be placed in the Crystal Nucleus. */
        public boolean isNucleusCrystal() { return nucleusCrystal; }

        /**
         * @return the buff granted while this crystal is placed in the nucleus,
         *         or {@code null} if this is not a nucleus crystal
         */
        public String getBuff() { return buff; }
    }

    /** The five crystals that can be slotted into the Crystal Nucleus. */
    private static final Set<CrystalType> NUCLEUS_CRYSTALS;
    static {
        EnumSet<CrystalType> set = EnumSet.noneOf(CrystalType.class);
        for (CrystalType type : CrystalType.values()) {
            if (type.isNucleusCrystal()) set.add(type);
        }
        NUCLEUS_CRYSTALS = Collections.unmodifiableSet(set);
    }

    private static final CrystalHollowsManager INSTANCE = new CrystalHollowsManager();

    /** Per-player currently occupied zone. */
    private final Map<UUID, CrystalHollowsZone> playerZones = new HashMap<>();
    /** Per-player crystal collection counts. */
    private final Map<UUID, Map<CrystalType, Integer>> crystalCounts = new HashMap<>();
    /** Per-player powder balances. */
    private final Map<UUID, Map<PowderType, Long>> powder = new HashMap<>();
    /** Per-player crystals currently placed in the Crystal Nucleus. */
    private final Map<UUID, EnumSet<CrystalType>> placedCrystals = new HashMap<>();
    /** Per-player set of zones the player has discovered (area progression). */
    private final Map<UUID, EnumSet<CrystalHollowsZone>> discoveredZones = new HashMap<>();

    private CrystalHollowsManager() {}

    /**
     * Returns the single shared {@code CrystalHollowsManager} instance.
     *
     * @return the singleton instance
     */
    public static CrystalHollowsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the zone the player is currently in, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the player's current zone, or {@code null}
     */
    public CrystalHollowsZone getZone(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerZones.get(playerId);
    }

    /**
     * Assigns the player to the given zone.
     *
     * @param playerId the player to update
     * @param zone     the zone to assign, must not be null
     */
    public void setZone(UUID playerId, CrystalHollowsZone zone) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(zone, "zone");
        playerZones.put(playerId, zone);
        discoveredZones
            .computeIfAbsent(playerId, k -> EnumSet.noneOf(CrystalHollowsZone.class))
            .add(zone);
    }

    /**
     * Removes any zone assignment for the player.
     *
     * @param playerId the player to clear
     */
    public void clearZone(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerZones.remove(playerId);
    }

    /**
     * Records that the player collected one crystal of the given type.
     *
     * @param playerId    the player
     * @param crystalType the type collected
     */
    public void addCrystal(UUID playerId, CrystalType crystalType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crystalType, "crystalType");
        crystalCounts
            .computeIfAbsent(playerId, k -> new HashMap<>())
            .merge(crystalType, 1, Integer::sum);
    }

    /**
     * Returns how many crystals of the given type the player has collected.
     *
     * @param playerId    the player to look up
     * @param crystalType the crystal type
     * @return collection count, {@code 0} if none
     */
    public int getCrystalCount(UUID playerId, CrystalType crystalType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crystalType, "crystalType");
        Map<CrystalType, Integer> counts = crystalCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(crystalType, 0);
    }

    /**
     * Awards the player powder of the given type.
     *
     * @param playerId the player
     * @param type     the powder type
     * @param amount   the amount to award, must be positive
     * @return the new balance for that powder type
     */
    public long addPowder(UUID playerId, PowderType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        Map<PowderType, Long> balances = powder.computeIfAbsent(playerId, k -> new HashMap<>());
        long newBalance = balances.getOrDefault(type, 0L) + amount;
        balances.put(type, newBalance);
        return newBalance;
    }

    /**
     * Returns how much powder of the given type the player holds.
     *
     * @param playerId the player to look up
     * @param type     the powder type
     * @return the balance, {@code 0} if none
     */
    public long getPowder(UUID playerId, PowderType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<PowderType, Long> balances = powder.get(playerId);
        return balances == null ? 0L : balances.getOrDefault(type, 0L);
    }

    /**
     * Spends powder of the given type if the player can afford it.
     *
     * @param playerId the player
     * @param type     the powder type
     * @param amount   the amount to spend, must be positive
     * @return {@code true} if the powder was deducted, {@code false} if the
     *         player had insufficient balance
     */
    public boolean spendPowder(UUID playerId, PowderType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        Map<PowderType, Long> balances = powder.get(playerId);
        long current = balances == null ? 0L : balances.getOrDefault(type, 0L);
        if (current < amount) return false;
        balances.put(type, current - amount);
        return true;
    }

    // -------------------------------------------------------------------------
    // Crystal Nucleus placement and buffs
    // -------------------------------------------------------------------------

    /**
     * Returns the five crystal types that can be slotted into the nucleus.
     *
     * @return an unmodifiable set of the nucleus crystals
     */
    public static Set<CrystalType> getNucleusCrystals() {
        return NUCLEUS_CRYSTALS;
    }

    /**
     * Places a nucleus crystal into the player's Crystal Nucleus, activating its
     * buff.
     *
     * @param playerId    the player
     * @param crystalType the crystal to place; must be a nucleus crystal
     * @return {@code true} if the crystal was newly placed, {@code false} if it
     *         was already in the nucleus
     * @throws IllegalArgumentException if {@code crystalType} is not a nucleus
     *                                  crystal
     */
    public boolean placeCrystal(UUID playerId, CrystalType crystalType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crystalType, "crystalType");
        if (!crystalType.isNucleusCrystal()) {
            throw new IllegalArgumentException(crystalType + " cannot be placed in the nucleus");
        }
        return placedCrystals
            .computeIfAbsent(playerId, k -> EnumSet.noneOf(CrystalType.class))
            .add(crystalType);
    }

    /**
     * Removes a crystal from the player's nucleus, deactivating its buff.
     *
     * @param playerId    the player
     * @param crystalType the crystal to remove
     * @return {@code true} if the crystal was present and removed
     */
    public boolean removeCrystal(UUID playerId, CrystalType crystalType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crystalType, "crystalType");
        EnumSet<CrystalType> placed = placedCrystals.get(playerId);
        return placed != null && placed.remove(crystalType);
    }

    /**
     * Returns whether the given crystal is currently placed in the player's
     * nucleus.
     *
     * @param playerId    the player to look up
     * @param crystalType the crystal type
     * @return {@code true} if placed
     */
    public boolean isCrystalPlaced(UUID playerId, CrystalType crystalType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crystalType, "crystalType");
        EnumSet<CrystalType> placed = placedCrystals.get(playerId);
        return placed != null && placed.contains(crystalType);
    }

    /**
     * Returns the crystals currently placed in the player's nucleus.
     *
     * @param playerId the player to look up
     * @return an unmodifiable snapshot of placed crystals (empty if none)
     */
    public Set<CrystalType> getPlacedCrystals(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        EnumSet<CrystalType> placed = placedCrystals.get(playerId);
        if (placed == null || placed.isEmpty()) return Collections.emptySet();
        return Collections.unmodifiableSet(EnumSet.copyOf(placed));
    }

    /**
     * Returns whether all five nucleus crystals are placed for the player.
     *
     * @param playerId the player to look up
     * @return {@code true} if the nucleus is fully assembled
     */
    public boolean isNucleusComplete(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        EnumSet<CrystalType> placed = placedCrystals.get(playerId);
        return placed != null && placed.containsAll(NUCLEUS_CRYSTALS);
    }

    /**
     * Returns the buff descriptions of every crystal currently placed in the
     * player's nucleus.
     *
     * @param playerId the player to look up
     * @return the active buff descriptions (empty if no crystals are placed)
     */
    public List<String> getActiveBuffs(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        EnumSet<CrystalType> placed = placedCrystals.get(playerId);
        if (placed == null || placed.isEmpty()) return Collections.emptyList();
        List<String> buffs = new ArrayList<>(placed.size());
        for (CrystalType crystal : placed) {
            buffs.add(crystal.getBuff());
        }
        return buffs;
    }

    // -------------------------------------------------------------------------
    // Area progression
    // -------------------------------------------------------------------------

    /**
     * Returns whether the player has ever entered the given zone.
     *
     * @param playerId the player to look up
     * @param zone     the zone
     * @return {@code true} if the zone has been discovered
     */
    public boolean hasDiscovered(UUID playerId, CrystalHollowsZone zone) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(zone, "zone");
        EnumSet<CrystalHollowsZone> discovered = discoveredZones.get(playerId);
        return discovered != null && discovered.contains(zone);
    }

    /**
     * Returns the zones the player has discovered by entering them.
     *
     * @param playerId the player to look up
     * @return an unmodifiable snapshot of discovered zones (empty if none)
     */
    public Set<CrystalHollowsZone> getDiscoveredZones(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        EnumSet<CrystalHollowsZone> discovered = discoveredZones.get(playerId);
        if (discovered == null || discovered.isEmpty()) return Collections.emptySet();
        return Collections.unmodifiableSet(EnumSet.copyOf(discovered));
    }

    /**
     * Returns the player's area-exploration progress as a fraction in
     * {@code [0.0, 1.0]} of all zones discovered.
     *
     * @param playerId the player to look up
     * @return the discovered fraction of all zones
     */
    public double getAreaProgress(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        EnumSet<CrystalHollowsZone> discovered = discoveredZones.get(playerId);
        int found = discovered == null ? 0 : discovered.size();
        return (double) found / CrystalHollowsZone.values().length;
    }
}
