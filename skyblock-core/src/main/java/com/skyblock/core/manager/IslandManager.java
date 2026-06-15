package com.skyblock.core.manager;

import com.skyblock.core.island.IslandManager.IslandData;
import com.skyblock.core.island.IslandManager.IslandUpgrade;
import com.skyblock.core.island.IslandManager.SkyBlockIsland;

import org.bukkit.World;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Canonical singleton for per-player SkyBlock island management.
 *
 * <p>Delegates all state to {@link com.skyblock.core.island.IslandManager}. Use
 * {@link IslandUpgrade} for upgrade classification, {@link SkyBlockIsland} for
 * member/upgrade data, and {@link IslandData} for trustee/blocks-placed records.</p>
 */
public final class IslandManager {

    private static final IslandManager INSTANCE = new IslandManager();
    private final com.skyblock.core.island.IslandManager delegate =
            com.skyblock.core.island.IslandManager.getInstance();

    private IslandManager() {}

    public static IslandManager getInstance() {
        return INSTANCE;
    }

    public SkyBlockIsland createIsland(UUID owner) {
        return delegate.createIsland(owner);
    }

    public Optional<World> getIslandWorld(UUID owner) {
        return delegate.getIslandWorld(owner);
    }

    public Optional<SkyBlockIsland> getIsland(UUID owner) {
        return delegate.getIsland(owner);
    }

    public Optional<SkyBlockIsland> getIslandByMember(UUID player) {
        return delegate.getIslandByMember(player);
    }

    public boolean hasIsland(UUID owner) {
        return delegate.hasIsland(owner);
    }

    public boolean addMember(UUID owner, UUID invitee) {
        return delegate.addMember(owner, invitee);
    }

    public boolean removeMember(UUID owner, UUID target) {
        return delegate.removeMember(owner, target);
    }

    public boolean leaveIsland(UUID member) {
        return delegate.leaveIsland(member);
    }

    public boolean applyUpgrade(UUID owner, IslandUpgrade upgrade) {
        return delegate.applyUpgrade(owner, upgrade);
    }

    public boolean setWarpName(UUID owner, String warpName) {
        return delegate.setWarpName(owner, warpName);
    }

    public String getWarpName(UUID owner) {
        return delegate.getWarpName(owner);
    }

    public boolean deleteIsland(UUID owner) {
        return delegate.deleteIsland(owner);
    }

    public void recordIslandEvent(UUID playerUuid, String summary) {
        delegate.recordIslandEvent(playerUuid, summary);
    }

    public List<String> getIslandHistory(UUID playerUuid) {
        return delegate.getIslandHistory(playerUuid);
    }

    public Map<UUID, List<String>> getAllIslandHistory() {
        return delegate.getAllIslandHistory();
    }

    public String getIslandStats(UUID playerId) {
        return delegate.getIslandStats(playerId);
    }

    public Optional<IslandData> getIslandData(UUID owner) {
        return delegate.getIslandData(owner);
    }

    public IslandData getOrCreateIslandData(UUID owner) {
        return delegate.getOrCreateIslandData(owner);
    }

    public int getIslandLevel(UUID owner) {
        return delegate.getIslandLevel(owner);
    }

    public void setLevel(UUID owner, int level) {
        delegate.setLevel(owner, level);
    }

    public boolean addTrustee(UUID owner, UUID trustee) {
        return delegate.addTrustee(owner, trustee);
    }

    public boolean removeTrustee(UUID owner, UUID trustee) {
        return delegate.removeTrustee(owner, trustee);
    }

    public void addBlocksPlaced(UUID owner, long amount) {
        delegate.addBlocksPlaced(owner, amount);
    }

    public String getIslandBiome(UUID playerId) {
        return delegate.getIslandBiome(playerId);
    }

    public void setIslandBiome(UUID playerId, String biome) {
        delegate.setIslandBiome(playerId, biome);
    }

    public boolean isIslandUnlocked(UUID playerId) {
        return delegate.isIslandUnlocked(playerId);
    }

    public void setIslandUnlocked(UUID playerId, boolean unlocked) {
        delegate.setIslandUnlocked(playerId, unlocked);
    }

    public int getVisitorCount(UUID playerId) {
        return delegate.getVisitorCount(playerId);
    }

    public void addVisitor(UUID islandOwner) {
        delegate.addVisitor(islandOwner);
    }

    public void setVisitorCount(UUID islandOwner, int count) {
        delegate.setVisitorCount(islandOwner, count);
    }

    public void recordVisit(UUID visitorId, String islandOwnerName) {
        delegate.recordVisit(visitorId, islandOwnerName);
    }

    public List<String> getVisitLog(UUID visitorId) {
        return delegate.getVisitLog(visitorId);
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
