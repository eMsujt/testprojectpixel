package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.IslandManager} instead.
 */
@Deprecated
public final class IslandManager {

    private static final IslandManager INSTANCE = new IslandManager();
    private final com.skyblock.core.manager.IslandManager delegate =
            com.skyblock.core.manager.IslandManager.getInstance();

    private IslandManager() {}

    public static IslandManager getInstance() {
        return INSTANCE;
    }

    public String getIslandBiome(UUID playerId) {
        return delegate.getIslandBiome(playerId);
    }

    public void setIslandBiome(UUID playerId, String biome) {
        delegate.setIslandBiome(playerId, biome);
    }

    public Map<UUID, String> getAllIslandBiomes() {
        return delegate.getAllIslandBiomes();
    }

    public boolean isIslandUnlocked(UUID playerId) {
        return delegate.isIslandUnlocked(playerId);
    }

    public void setIslandUnlocked(UUID playerId, boolean unlocked) {
        delegate.setIslandUnlocked(playerId, unlocked);
    }

    public Map<UUID, Boolean> getIslandUnlocked() {
        return delegate.getAllIslandUnlocked();
    }

    public int getIslandLevel(UUID playerId) {
        return delegate.getIslandLevel(playerId);
    }

    public void setIslandLevel(UUID playerId, int level) {
        delegate.setLevel(playerId, Math.max(0, level));
    }

    public void addIslandLevel(UUID playerId, int amount) {
        setIslandLevel(playerId, getIslandLevel(playerId) + amount);
    }

    public Map<UUID, Integer> getIslandLevels() {
        return delegate.getAllIslandLevels();
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

    public Map<UUID, Integer> getVisitorCounts() {
        return delegate.getAllVisitorCounts();
    }

    public List<String> getBuildings(UUID playerId) {
        return Collections.emptyList();
    }

    public void addBuilding(UUID playerId, String building) {}

    public Map<UUID, List<String>> getAllIslandBuildings() {
        return Collections.emptyMap();
    }

    public void recordVisit(UUID islandOwner, UUID visitor) {}

    public List<UUID> getIslandVisitors(UUID islandOwner) {
        return Collections.emptyList();
    }

    public Map<UUID, List<UUID>> getAllIslandVisitors() {
        return Collections.emptyMap();
    }

    public void addMember(UUID owner, UUID member) {
        delegate.addMember(owner, member);
    }

    public void removeMember(UUID owner, UUID member) {
        delegate.removeMember(owner, member);
    }

    public List<UUID> getIslandMembers(UUID owner) {
        return delegate.getIsland(owner)
                .map(com.skyblock.core.island.manager.IslandManager.SkyBlockIsland::getMembers)
                .orElse(Collections.emptyList());
    }

    public Map<UUID, List<UUID>> getAllIslandMembers() {
        return delegate.getAllIslandMembers();
    }

    public void recordVisit(UUID visitorId, String islandOwnerName) {
        delegate.recordVisit(visitorId, islandOwnerName);
    }

    public List<String> getVisitLog(UUID visitorId) {
        return delegate.getVisitLog(visitorId);
    }

    public Map<UUID, List<String>> getAllVisitLog() {
        return delegate.getAllVisitLog();
    }

    public void recordIslandEvent(UUID playerId, String summary) {
        delegate.recordIslandEvent(playerId, summary);
    }

    public List<String> getIslandHistory(UUID playerId) {
        return delegate.getIslandHistory(playerId);
    }

    public Map<UUID, List<String>> getAllIslandHistory() {
        return delegate.getAllIslandHistory();
    }

    public String getIslandStats(UUID playerId) {
        return delegate.getIslandStats(playerId);
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
