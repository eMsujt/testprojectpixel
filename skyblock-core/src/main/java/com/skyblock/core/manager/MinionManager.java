package com.skyblock.core.manager;

import com.skyblock.core.minion.MinionManager.MinionData;
import com.skyblock.core.minion.MinionManager.MinionTier;
import com.skyblock.core.minion.MinionManager.MinionType;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Canonical singleton for per-player SkyBlock minion management.
 *
 * <p>Delegates all state to {@link com.skyblock.core.minion.MinionManager}. Use
 * {@link MinionType} for minion types, {@link MinionTier} for upgrade tiers, and
 * {@link MinionData} for per-minion state. The slot cap is
 * {@link com.skyblock.core.minion.MinionManager#MAX_SLOTS}.</p>
 */
public final class MinionManager {

    private static final MinionManager INSTANCE = new MinionManager();
    private final com.skyblock.core.minion.MinionManager delegate =
            com.skyblock.core.minion.MinionManager.getInstance();

    private MinionManager() {}

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    public MinionData placeMinion(UUID owner, MinionType type, MinionTier tier) {
        return delegate.placeMinion(owner, type, tier);
    }

    public boolean removeMinion(UUID minionId) {
        return delegate.removeMinion(minionId);
    }

    public MinionData getMinion(UUID minionId) {
        return delegate.getMinion(minionId);
    }

    public List<UUID> getMinions(UUID owner) {
        return delegate.getMinions(owner);
    }

    public boolean upgradeMinion(UUID minionId) {
        return delegate.upgradeMinion(minionId);
    }

    public int clearMinions(UUID owner) {
        return delegate.clearMinions(owner);
    }

    public MinionType getPlacement(UUID owner, String location) {
        return delegate.getPlacement(owner, location);
    }

    public void setPlacement(UUID owner, String location, MinionType type) {
        delegate.setPlacement(owner, location, type);
    }

    public boolean removePlacement(UUID owner, String location) {
        return delegate.removePlacement(owner, location);
    }

    public Map<String, MinionType> getPlacements(UUID owner) {
        return delegate.getPlacements(owner);
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
