package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class QuestManager {

    public enum Quest {
        SLAYER_QUEST,
        FISHING_QUEST,
        MINING_QUEST,
        FARMING_QUEST,
        COMBAT_QUEST,
        FORAGING_QUEST,
        ENCHANTING_QUEST,
        ALCHEMY_QUEST,
        DUNGEON_QUEST
    }

    private static final QuestManager INSTANCE = new QuestManager();

    private final Map<UUID, Quest> activeQuests = new HashMap<>();

    private QuestManager() {}

    public static QuestManager getInstance() {
        return INSTANCE;
    }

    public Quest getActiveQuest(UUID playerId) {
        return activeQuests.get(playerId);
    }

    public void startQuest(UUID playerId, Quest quest) {
        if (quest == null) {
            activeQuests.remove(playerId);
        } else {
            activeQuests.put(playerId, quest);
        }
    }

    public boolean completeQuest(UUID playerId) {
        return activeQuests.remove(playerId) != null;
    }

    public boolean hasActiveQuest(UUID playerId) {
        return activeQuests.containsKey(playerId);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "quests.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activeQuests.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String questName = cfg.getString(key);
                if (questName != null) {
                    try {
                        activeQuests.put(uuid, Quest.valueOf(questName));
                    } catch (IllegalArgumentException ignored) {
                        // skip unknown quest name
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "quests.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Quest> entry : activeQuests.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue().name());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save quests.yml", e);
        }
    }
}
