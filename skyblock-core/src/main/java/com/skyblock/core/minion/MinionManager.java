package com.skyblock.core.minion;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.MinionManager} instead.
 */
@Deprecated
public final class MinionManager {

    private static final com.skyblock.core.manager.MinionManager DELEGATE =
            com.skyblock.core.manager.MinionManager.getInstance();

    private MinionManager() {}

    public static com.skyblock.core.manager.MinionManager getInstance() {
        return DELEGATE;
    }

    public com.skyblock.core.manager.MinionManager.MinionData placeMinion(
            UUID owner,
            com.skyblock.core.manager.MinionManager.MinionType type,
            com.skyblock.core.manager.MinionManager.MinionTier tier) {
        return DELEGATE.placeMinion(owner, type, tier);
    }

    public boolean removeMinion(UUID minionId) {
        return DELEGATE.removeMinion(minionId);
    }

    public com.skyblock.core.manager.MinionManager.MinionData getMinion(UUID minionId) {
        return DELEGATE.getMinion(minionId);
    }

    public List<UUID> getMinions(UUID owner) {
        return DELEGATE.getMinions(owner);
    }

    public boolean upgradeMinion(UUID minionId) {
        return DELEGATE.upgradeMinion(minionId);
    }

    public int clearMinions(UUID owner) {
        return DELEGATE.clearMinions(owner);
    }

    public com.skyblock.core.manager.MinionManager.MinionType getPlacement(UUID owner, String location) {
        return DELEGATE.getPlacement(owner, location);
    }

    public void setPlacement(UUID owner, String location,
            com.skyblock.core.manager.MinionManager.MinionType type) {
        DELEGATE.setPlacement(owner, location, type);
    }

    public boolean removePlacement(UUID owner, String location) {
        return DELEGATE.removePlacement(owner, location);
    }

    public Map<String, com.skyblock.core.manager.MinionManager.MinionType> getPlacements(UUID owner) {
        return DELEGATE.getPlacements(owner);
    }

    public void load(File dataFolder) {
        DELEGATE.load(dataFolder);
    }

    public void save(File dataFolder) {
        DELEGATE.save(dataFolder);
    }
}
