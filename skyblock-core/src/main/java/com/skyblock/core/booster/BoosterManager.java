package com.skyblock.core.booster;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking active per-player XP/coin boosters.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BoosterManager {

    public enum BoosterType {
        XP("XP Booster"),
        COINS("Coin Booster"),
        DROP_RATE("Drop Rate Booster");

        private final String displayName;

        BoosterType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    private static final BoosterManager INSTANCE = new BoosterManager();

    /** Active boosters: player UUID → booster type. */
    private final Map<UUID, BoosterType> activeBoosters = new HashMap<>();

    private BoosterManager() {}

    public static BoosterManager getInstance() {
        return INSTANCE;
    }

    public void activateBooster(UUID player, BoosterType type) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(type, "type");
        activeBoosters.put(player, type);
    }

    public void deactivateBooster(UUID player) {
        Objects.requireNonNull(player, "player");
        activeBoosters.remove(player);
    }

    public BoosterType getActiveBooster(UUID player) {
        Objects.requireNonNull(player, "player");
        return activeBoosters.get(player);
    }

    public boolean hasActiveBooster(UUID player) {
        Objects.requireNonNull(player, "player");
        return activeBoosters.containsKey(player);
    }

    public Map<UUID, BoosterType> getActiveBoosters() {
        return Collections.unmodifiableMap(activeBoosters);
    }
}
