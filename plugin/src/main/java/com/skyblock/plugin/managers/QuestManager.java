package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class QuestManager {

    public enum Quest {
        SLAYER_QUEST, FISHING_QUEST, MINING_QUEST, FARMING_QUEST, COMBAT_QUEST,
        DUNGEON_QUEST, FORAGING_QUEST, ENCHANTING_QUEST, ALCHEMY_QUEST
    }

    private static final QuestManager INSTANCE = new QuestManager();

    private final Map<UUID, Quest> activeQuests = new HashMap<>();
    private final Map<UUID, Quest> completedQuests = new HashMap<>();

    private QuestManager() {}

    public static QuestManager getInstance() {
        return INSTANCE;
    }

    public void startQuest(UUID playerId, Quest quest) {
        activeQuests.put(playerId, quest);
    }

    public Quest getActiveQuest(UUID playerId) {
        return activeQuests.get(playerId);
    }

    public boolean hasActiveQuest(UUID playerId) {
        return activeQuests.containsKey(playerId);
    }

    public void completeQuest(UUID playerId) {
        Quest quest = activeQuests.remove(playerId);
        if (quest != null) {
            completedQuests.put(playerId, quest);
        }
    }

    public Quest getLastCompletedQuest(UUID playerId) {
        return completedQuests.get(playerId);
    }

    public void removePlayer(UUID playerId) {
        activeQuests.remove(playerId);
        completedQuests.remove(playerId);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "quests.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activeQuests.clear();
        completedQuests.clear();
        if (cfg.isConfigurationSection("active")) {
            for (String key : cfg.getConfigurationSection("active").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String questName = cfg.getString("active." + key);
                    if (questName != null) {
                        try {
                            activeQuests.put(uuid, Quest.valueOf(questName));
                        } catch (IllegalArgumentException ignored) {}
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("completed")) {
            for (String key : cfg.getConfigurationSection("completed").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String questName = cfg.getString("completed." + key);
                    if (questName != null) {
                        try {
                            completedQuests.put(uuid, Quest.valueOf(questName));
                        } catch (IllegalArgumentException ignored) {}
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "quests.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Quest> entry : activeQuests.entrySet()) {
            cfg.set("active." + entry.getKey().toString(), entry.getValue().name());
        }
        for (Map.Entry<UUID, Quest> entry : completedQuests.entrySet()) {
            cfg.set("completed." + entry.getKey().toString(), entry.getValue().name());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save quests.yml", e);
        }
    }
}
