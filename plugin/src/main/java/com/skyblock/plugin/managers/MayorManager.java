package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class MayorManager {

    public enum Mayor {
        PAUL, DIANA, JERRY, DERPY, FOXY, COLE, AATROX, MARINA, SCORPIUS
    }

    private static final MayorManager INSTANCE = new MayorManager();

    private Mayor activeMayor;
    private final Map<UUID, Mayor> playerVotes = new HashMap<>();

    private MayorManager() {}

    public static MayorManager getInstance() {
        return INSTANCE;
    }

    public Mayor getActiveMayor() {
        return activeMayor;
    }

    public void setActiveMayor(Mayor mayor) {
        this.activeMayor = mayor;
    }

    public void vote(UUID playerId, Mayor mayor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(mayor, "mayor");
        playerVotes.put(playerId, mayor);
    }

    public Mayor getVote(UUID playerId) {
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
        String active = cfg.getString("activeMayor");
        if (active != null) {
            try {
                activeMayor = Mayor.valueOf(active);
            } catch (IllegalArgumentException ignored) {
                // skip unknown mayor name
            }
        }
        playerVotes.clear();
        if (cfg.isConfigurationSection("votes")) {
            for (String key : cfg.getConfigurationSection("votes").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String vote = cfg.getString("votes." + key);
                    if (vote != null) {
                        try {
                            playerVotes.put(uuid, Mayor.valueOf(vote));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown mayor name
                        }
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
            cfg.set("activeMayor", activeMayor.name());
        }
        for (Map.Entry<UUID, Mayor> entry : playerVotes.entrySet()) {
            cfg.set("votes." + entry.getKey().toString(), entry.getValue().name());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save mayor.yml", e);
        }
    }
}
