package com.skyblock.plugin.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class CollectionsManager {

    private static final CollectionsManager INSTANCE = new CollectionsManager();

    private final Map<UUID, Map<String, Long>> collectionCounts = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> collectionMilestones = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> collectionItems = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> collectionHistory = new HashMap<>();
    private final Map<UUID, List<String>> unlockHistory = new HashMap<>();
    private final Map<UUID, List<String>> collectionsHistory = new HashMap<>();

    private CollectionsManager() {}

    public static CollectionsManager getInstance() {
        return INSTANCE;
    }

    public long getCollectionCount(UUID playerId, String collection) {
        Map<String, Long> counts = collectionCounts.get(playerId);
        if (counts == null) return 0L;
        return counts.getOrDefault(collection, 0L);
    }

    public void addCollectionCount(UUID playerId, String collection, long amount) {
        collectionCounts
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(collection, amount, Long::sum);
    }

    public void setCollectionCount(UUID playerId, String collection, long amount) {
        collectionCounts
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .put(collection, amount);
    }

    public Map<String, Long> getCollectionCounts(UUID playerId) {
        return collectionCounts.getOrDefault(playerId, new HashMap<>());
    }

    public int getCollectionMilestone(UUID playerId, String collection) {
        Map<String, Integer> milestones = collectionMilestones.get(playerId);
        if (milestones == null) return 0;
        return milestones.getOrDefault(collection, 0);
    }

    public void setCollectionMilestone(UUID playerId, String collection, int tier) {
        collectionMilestones
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .put(collection, tier);
    }

    public Map<String, Integer> getCollectionMilestones(UUID playerId) {
        return collectionMilestones.getOrDefault(playerId, new HashMap<>());
    }

    public void addCollectionItem(UUID playerId, String item, int amount) {
        collectionItems
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(item, amount, Integer::sum);
    }

    public Map<String, Integer> getCollectionItems(UUID playerId) {
        return collectionItems.getOrDefault(playerId, new HashMap<>());
    }

    public Map<UUID, Map<String, Integer>> getAllCollectionItems() {
        return collectionItems;
    }

    public void recordCollection(UUID playerId, String item, int amount) {
        collectionHistory
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(item, amount, Integer::sum);
    }

    public Map<String, Integer> getCollectionHistory(UUID playerId) {
        return collectionHistory.getOrDefault(playerId, new HashMap<>());
    }

    public Map<UUID, Map<String, Integer>> getAllCollectionHistory() {
        return collectionHistory;
    }

    public void recordUnlock(UUID playerId, String summary) {
        unlockHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getUnlockHistory(UUID playerId) {
        return Collections.unmodifiableList(unlockHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllUnlockHistory() {
        return Collections.unmodifiableMap(unlockHistory);
    }

    public void recordCollectionEvent(UUID playerId, String summary) {
        collectionsHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getCollectionsHistory(UUID playerId) {
        return Collections.unmodifiableList(collectionsHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllCollectionsHistory() {
        return Collections.unmodifiableMap(collectionsHistory);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "collections.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        collectionCounts.clear();
        collectionMilestones.clear();
        collectionItems.clear();
        collectionHistory.clear();
        unlockHistory.clear();
        collectionsHistory.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                ConfigurationSection section = cfg.getConfigurationSection(key);
                if (section == null) continue;
                ConfigurationSection countsSection = section.getConfigurationSection("counts");
                if (countsSection != null) {
                    Map<String, Long> counts = new HashMap<>();
                    for (String collection : countsSection.getKeys(false)) {
                        counts.put(collection, countsSection.getLong(collection));
                    }
                    collectionCounts.put(uuid, counts);
                }
                ConfigurationSection msSection = section.getConfigurationSection("milestones");
                if (msSection != null) {
                    Map<String, Integer> milestones = new HashMap<>();
                    for (String collection : msSection.getKeys(false)) {
                        milestones.put(collection, msSection.getInt(collection));
                    }
                    collectionMilestones.put(uuid, milestones);
                }
                ConfigurationSection itemsSection = section.getConfigurationSection("items");
                if (itemsSection != null) {
                    Map<String, Integer> items = new HashMap<>();
                    for (String item : itemsSection.getKeys(false)) {
                        items.put(item, itemsSection.getInt(item));
                    }
                    collectionItems.put(uuid, items);
                }
                ConfigurationSection historySection = section.getConfigurationSection("history");
                if (historySection != null) {
                    Map<String, Integer> history = new HashMap<>();
                    for (String item : historySection.getKeys(false)) {
                        history.put(item, historySection.getInt(item));
                    }
                    collectionHistory.put(uuid, history);
                }
                List<String> unlocks = cfg.getStringList(key + ".unlockHistory");
                if (!unlocks.isEmpty()) {
                    unlockHistory.put(uuid, new ArrayList<>(unlocks));
                }
                List<String> colHistory = cfg.getStringList(key + ".collectionsHistory");
                if (!colHistory.isEmpty()) {
                    collectionsHistory.put(uuid, new ArrayList<>(colHistory));
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "collections.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        Set<UUID> allPlayers = new HashSet<>(collectionCounts.keySet());
        allPlayers.addAll(collectionMilestones.keySet());
        allPlayers.addAll(collectionItems.keySet());
        allPlayers.addAll(collectionHistory.keySet());
        allPlayers.addAll(unlockHistory.keySet());
        allPlayers.addAll(collectionsHistory.keySet());
        for (UUID uuid : allPlayers) {
            String uuidKey = uuid.toString();
            Map<String, Long> counts = collectionCounts.getOrDefault(uuid, new HashMap<>());
            for (Map.Entry<String, Long> countEntry : counts.entrySet()) {
                cfg.set(uuidKey + ".counts." + countEntry.getKey(), countEntry.getValue());
            }
            Map<String, Integer> milestones = collectionMilestones.getOrDefault(uuid, new HashMap<>());
            for (Map.Entry<String, Integer> msEntry : milestones.entrySet()) {
                cfg.set(uuidKey + ".milestones." + msEntry.getKey(), msEntry.getValue());
            }
            Map<String, Integer> items = collectionItems.getOrDefault(uuid, new HashMap<>());
            for (Map.Entry<String, Integer> itemEntry : items.entrySet()) {
                cfg.set(uuidKey + ".items." + itemEntry.getKey(), itemEntry.getValue());
            }
            Map<String, Integer> history = collectionHistory.getOrDefault(uuid, new HashMap<>());
            for (Map.Entry<String, Integer> histEntry : history.entrySet()) {
                cfg.set(uuidKey + ".history." + histEntry.getKey(), histEntry.getValue());
            }
            List<String> unlocks = unlockHistory.get(uuid);
            if (unlocks != null && !unlocks.isEmpty()) {
                cfg.set(uuidKey + ".unlockHistory", unlocks);
            }
            List<String> colHistory = collectionsHistory.get(uuid);
            if (colHistory != null && !colHistory.isEmpty()) {
                cfg.set(uuidKey + ".collectionsHistory", colHistory);
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save collections.yml", e);
        }
    }
}
