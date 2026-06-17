package com.skyblock.core.manager;

import com.skyblock.core.manager.KuudraManager.KuudraTier;

import java.util.Objects;
import java.util.UUID;

/**
 * Canonical Crimson Isle coordinator.
 *
 * <p>The Crimson Isle area is split across two domain managers that this class
 * ties together as a single entry point:</p>
 * <ul>
 *   <li>{@link ReputationManager} — Mage/Barbarian faction alignment and
 *       reputation (Hated &hellip; Respected).</li>
 *   <li>{@link KuudraManager} — Kuudra fights and per-tier completions
 *       (Basic, Hot, Burning, Fiery, Infernal).</li>
 * </ul>
 *
 * <p>It does not duplicate their state; it composes them and owns the
 * cross-cutting Kuudra <em>tier-unlock progression</em>: a player starts with
 * only Basic available and unlocks each successive tier by completing the one
 * below it.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CrimsonIsleManager {

    private static final CrimsonIsleManager INSTANCE = new CrimsonIsleManager();

    private final ReputationManager reputationManager;
    private final KuudraManager kuudraManager;

    private CrimsonIsleManager() {
        this(ReputationManager.getInstance(), KuudraManager.getInstance());
    }

    // Package-private for tests that need isolated sub-managers.
    CrimsonIsleManager(ReputationManager reputationManager, KuudraManager kuudraManager) {
        this.reputationManager = Objects.requireNonNull(reputationManager, "reputationManager");
        this.kuudraManager = Objects.requireNonNull(kuudraManager, "kuudraManager");
    }

    public static CrimsonIsleManager getInstance() {
        return INSTANCE;
    }

    public ReputationManager reputation() {
        return reputationManager;
    }

    public KuudraManager kuudra() {
        return kuudraManager;
    }

    /**
     * The highest Kuudra tier the player has unlocked. Basic is always
     * available; each higher tier unlocks once the player has at least one
     * completion of the tier immediately below it.
     *
     * @return the highest unlocked {@link KuudraTier}, never {@code null}
     */
    public KuudraTier getHighestUnlockedTier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        KuudraTier[] tiers = KuudraTier.values();
        KuudraTier highest = tiers[0];
        for (int i = 1; i < tiers.length; i++) {
            if (kuudraManager.getCompletionCount(playerId, tiers[i - 1]) > 0) {
                highest = tiers[i];
            } else {
                break;
            }
        }
        return highest;
    }

    /**
     * Whether the player may join a run of the given tier — i.e. the tier is
     * at or below their highest unlocked tier.
     */
    public boolean canJoinTier(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        return tier.getTier() <= getHighestUnlockedTier(playerId).getTier();
    }

    /** A one-line summary of the player's Crimson Isle progress. */
    public String getSummary(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        ReputationManager.Faction faction = reputationManager.getFaction(playerId);
        String factionName = faction == null ? "None" : faction.getDisplayName();
        String factionRep = faction == null
                ? "n/a"
                : reputationManager.getReputationTier(playerId, faction).getDisplayName();
        return "Faction: " + factionName
                + " (" + factionRep + ")"
                + " | Highest Kuudra: " + getHighestUnlockedTier(playerId).getDisplayName();
    }
}
