package com.skyblock.core.crimsonisle;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CrimsonIsleManager {

    public enum CrimsonFaction {
        MAGE("Mages"),
        BARBARIAN("Barbarians");

        private final String displayName;

        CrimsonFaction(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum KuudraFaction {
        BARBARIAN("Barbarian"),
        MAGE("Mage"),
        BARBARIAN_DUKE("Barbarian Duke"),
        MAGE_LORD("Mage Lord"),
        KUUDRA_FOLLOWER("Kuudra Follower"),
        UNDEAD_WARRIOR("Undead Warrior"),
        FLAME_MAGE("Flame Mage"),
        INFERNAL_DEMON("Infernal Demon"),
        BLAZE_CULTIST("Blaze Cultist"),
        NETHER_GUARDIAN("Nether Guardian"),
        CRIMSON_SOLDIER("Crimson Soldier"),
        MAGMA_PRIEST("Magma Priest"),
        FIRE_SPIRIT("Fire Spirit"),
        LAVA_KNIGHT("Lava Knight"),
        EMBER_SORCERER("Ember Sorcerer");

        private final String displayName;

        KuudraFaction(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static final int MAX_REPUTATION = 10000;
    public static final int MAX_KUUDRA_STANDING = 5000;

    private static final CrimsonIsleManager INSTANCE = new CrimsonIsleManager();

    private final Map<UUID, CrimsonFaction> playerFactions = new HashMap<>();
    private final Map<UUID, Map<CrimsonFaction, Integer>> playerReputation = new HashMap<>();
    private final Map<UUID, KuudraFaction> playerKuudraFactions = new HashMap<>();
    private final Map<UUID, Map<KuudraFaction, Integer>> playerKuudraStanding = new HashMap<>();

    private CrimsonIsleManager() {}

    public static CrimsonIsleManager getInstance() {
        return INSTANCE;
    }

    public void setFaction(UUID playerId, CrimsonFaction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        playerFactions.put(playerId, faction);
    }

    public CrimsonFaction getFaction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerFactions.get(playerId);
    }

    public int addReputation(UUID playerId, CrimsonFaction faction, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<CrimsonFaction, Integer> repMap = playerReputation.computeIfAbsent(
                playerId, id -> new EnumMap<>(CrimsonFaction.class));
        int total = Math.min(repMap.getOrDefault(faction, 0) + amount, MAX_REPUTATION);
        repMap.put(faction, total);
        return total;
    }

    public int getReputation(UUID playerId, CrimsonFaction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        Map<CrimsonFaction, Integer> repMap = playerReputation.get(playerId);
        return repMap == null ? 0 : repMap.getOrDefault(faction, 0);
    }

    public void setKuudraFaction(UUID playerId, KuudraFaction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        playerKuudraFactions.put(playerId, faction);
    }

    public KuudraFaction getKuudraFaction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerKuudraFactions.get(playerId);
    }

    public int addKuudraStanding(UUID playerId, KuudraFaction faction, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<KuudraFaction, Integer> standingMap = playerKuudraStanding.computeIfAbsent(
                playerId, id -> new EnumMap<>(KuudraFaction.class));
        int total = Math.min(standingMap.getOrDefault(faction, 0) + amount, MAX_KUUDRA_STANDING);
        standingMap.put(faction, total);
        return total;
    }

    public int getKuudraStanding(UUID playerId, KuudraFaction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        Map<KuudraFaction, Integer> standingMap = playerKuudraStanding.get(playerId);
        return standingMap == null ? 0 : standingMap.getOrDefault(faction, 0);
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = playerFactions.remove(playerId) != null;
        hadData |= playerReputation.remove(playerId) != null;
        hadData |= playerKuudraFactions.remove(playerId) != null;
        hadData |= playerKuudraStanding.remove(playerId) != null;
        return hadData;
    }
}
