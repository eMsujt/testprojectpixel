package com.skyblock.core.minions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Location;

/**
 * @deprecated Use {@link com.skyblock.core.manager.MinionManager} instead.
 */
@Deprecated
public final class MinionManager {

    public static final int MAX_MINIONS_PER_PLAYER = 25;

    private static final MinionManager INSTANCE = new MinionManager();

    private MinionManager() {
    }

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    private final Map<Location, MinionData> placedMinions = new HashMap<>();
    private final Map<UUID, List<Location>> ownerIndex = new HashMap<>();

    public MinionData placeMinion(UUID ownerId, MinionType type, Location location) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(location, "location");
        if (placedMinions.containsKey(location)) {
            throw new IllegalStateException("a minion is already placed at " + location);
        }
        List<Location> owned = ownerIndex.computeIfAbsent(ownerId, id -> new ArrayList<>());
        if (owned.size() >= MAX_MINIONS_PER_PLAYER) {
            throw new IllegalStateException(
                    "player already has the maximum of " + MAX_MINIONS_PER_PLAYER + " minions: " + ownerId);
        }
        Location key = location.clone();
        MinionData minion = new MinionData(ownerId, type, key);
        placedMinions.put(key, minion);
        owned.add(key);
        return minion;
    }

    public MinionData removeMinion(UUID ownerId, Location location) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(location, "location");
        MinionData minion = placedMinions.remove(location);
        if (minion != null) {
            List<Location> owned = ownerIndex.get(ownerId);
            if (owned != null) {
                owned.remove(location);
            }
        }
        return minion;
    }

    public MinionData getMinion(Location location) {
        Objects.requireNonNull(location, "location");
        return placedMinions.get(location);
    }

    public List<MinionData> getMinions(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        List<Location> locations = ownerIndex.getOrDefault(ownerId, Collections.emptyList());
        List<MinionData> result = new ArrayList<>(locations.size());
        for (Location loc : locations) {
            MinionData m = placedMinions.get(loc);
            if (m != null) {
                result.add(m);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public int getMinionCount(UUID ownerId) {
        return ownerIndex.getOrDefault(ownerId, Collections.emptyList()).size();
    }

    public enum MinionType {
        COBBLESTONE("Cobblestone Minion"),
        WHEAT("Wheat Minion"),
        SUGAR_CANE("Sugar Cane Minion"),
        FISHING("Fishing Minion"),
        CARROT("Carrot Minion"),
        POTATO("Potato Minion"),
        PUMPKIN("Pumpkin Minion"),
        COAL("Coal Minion"),
        IRON("Iron Minion"),
        GOLD("Gold Minion"),
        DIAMOND("Diamond Minion"),
        ZOMBIE("Zombie Minion"),
        SKELETON("Skeleton Minion"),
        SPIDER("Spider Minion"),
        OAK("Oak Minion");

        private final String displayName;

        MinionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static final class MinionData {

        private final UUID ownerId;
        private final MinionType type;
        private final Location location;
        private int tier;
        private long lastActionTime;

        private MinionData(UUID ownerId, MinionType type, Location location) {
            this.ownerId = ownerId;
            this.type = type;
            this.location = location;
            this.tier = 1;
            this.lastActionTime = 0L;
        }

        public UUID getOwnerId() {
            return ownerId;
        }

        public MinionType getType() {
            return type;
        }

        public Location getLocation() {
            return location.clone();
        }

        public int getTier() {
            return tier;
        }

        public void setTier(int tier) {
            if (tier < 1) {
                throw new IllegalArgumentException("tier must be at least 1, got " + tier);
            }
            this.tier = tier;
        }

        public long getLastActionTime() {
            return lastActionTime;
        }

        public void setLastActionTime(long lastActionTime) {
            this.lastActionTime = lastActionTime;
        }

        @Override
        public String toString() {
            return "MinionData{ownerId=" + ownerId + ", type=" + type + ", location=" + location
                    + ", tier=" + tier + ", lastActionTime=" + lastActionTime + '}';
        }
    }
}
