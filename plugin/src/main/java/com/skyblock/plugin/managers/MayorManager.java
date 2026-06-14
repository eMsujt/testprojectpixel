package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MayorManager {

    private static final MayorManager INSTANCE = new MayorManager();

    private String currentMayor = "Finnegan";
    private int electionDay = 0;
    private final Map<UUID, String> mayorVotes = new HashMap<>();

    private MayorManager() {}

    public static MayorManager getInstance() {
        return INSTANCE;
    }

    public String getCurrentMayor() {
        return currentMayor;
    }

    public void setCurrentMayor(String mayor) {
        this.currentMayor = mayor;
    }

    public int getElectionDay() {
        return electionDay;
    }

    public void setElectionDay(int day) {
        this.electionDay = day;
    }

    public String getMayorVote(UUID playerId) {
        return mayorVotes.get(playerId);
    }

    public void setMayorVote(UUID playerId, String mayor) {
        mayorVotes.put(playerId, mayor);
    }

    public boolean clearMayorVote(UUID playerId) {
        return mayorVotes.remove(playerId) != null;
    }

    public Map<UUID, String> getMayorVotes() {
        return Collections.unmodifiableMap(mayorVotes);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "mayor.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String mayor = cfg.getString("currentMayor");
        if (mayor != null) {
            currentMayor = mayor;
        }
        electionDay = cfg.getInt("electionDay", 0);
        mayorVotes.clear();
        if (cfg.isConfigurationSection("votes")) {
            for (String key : cfg.getConfigurationSection("votes").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String vote = cfg.getString("votes." + key);
                    if (vote != null) {
                        mayorVotes.put(uuid, vote);
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "mayor.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("currentMayor", currentMayor);
        cfg.set("electionDay", electionDay);
        for (Map.Entry<UUID, String> entry : mayorVotes.entrySet()) {
            cfg.set("votes." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save mayor.yml", e);
        }
    }
}
