package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class MayorManager {

    private static final MayorManager INSTANCE = new MayorManager();

    private String activeMayor;
    private final Map<UUID, String> playerVotes = new HashMap<>();

    private MayorManager() {}

    public static MayorManager getInstance() {
        return INSTANCE;
    }

    public String getActiveMayor() {
        return activeMayor;
    }

    public void setActiveMayor(String mayor) {
        this.activeMayor = mayor;
    }

    public void vote(UUID playerId, String mayor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(mayor, "mayor");
        playerVotes.put(playerId, mayor);
    }

    public String getVote(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerVotes.get(playerId);
    }

    public boolean clearVote(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerVotes.remove(playerId) != null;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "mayor.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activeMayor = cfg.getString("activeMayor");
        playerVotes.clear();
        if (cfg.isConfigurationSection("votes")) {
            for (String key : cfg.getConfigurationSection("votes").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String vote = cfg.getString("votes." + key);
                    if (vote != null) {
                        playerVotes.put(uuid, vote);
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
        if (activeMayor != null) {
            cfg.set("activeMayor", activeMayor);
        }
        for (Map.Entry<UUID, String> entry : playerVotes.entrySet()) {
            cfg.set("votes." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save mayor.yml", e);
        }
    }
}
