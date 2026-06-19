package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking the active competition pet per player and applying its
 * Magic Find / Speed bonus via {@link StatManager} on join/quit.
 */
public final class TamingManager implements Listener {

    /** Magic Find bonus granted per competition level earned. */
    private static final double MAGIC_FIND_PER_LEVEL = 1.0;
    /** Speed bonus granted per competition level earned. */
    private static final double SPEED_PER_LEVEL = 0.25;

    private static final TamingManager INSTANCE = new TamingManager();

    /** Active competition pet name (e.g. "WOLF") per player. */
    private final Map<UUID, String> activeCompetitionPet = new HashMap<>();
    /** Competition levels earned per player. */
    private final Map<UUID, Integer> competitionLevel = new HashMap<>();

    private TamingManager() {}

    public static TamingManager getInstance() {
        return INSTANCE;
    }

    // --- competition pet ---

    public String getActiveCompetitionPet(UUID playerId) {
        return activeCompetitionPet.get(playerId);
    }

    public void setActiveCompetitionPet(UUID playerId, String petName) {
        activeCompetitionPet.put(playerId, petName);
    }

    public int getCompetitionLevel(UUID playerId) {
        return competitionLevel.getOrDefault(playerId, 0);
    }

    public int addCompetitionLevel(UUID playerId, int levels) {
        if (levels < 0) throw new IllegalArgumentException("levels must not be negative");
        int total = competitionLevel.getOrDefault(playerId, 0) + levels;
        competitionLevel.put(playerId, total);
        return total;
    }

    // --- stat application ---

    private void applyPetStats(UUID playerId) {
        int level = competitionLevel.getOrDefault(playerId, 0);
        if (level <= 0 || !activeCompetitionPet.containsKey(playerId)) return;
        StatManager stats = StatManager.getInstance();
        stats.addBonus(playerId, Stat.MAGIC_FIND, level * MAGIC_FIND_PER_LEVEL);
        stats.addBonus(playerId, Stat.SPEED, level * SPEED_PER_LEVEL);
    }

    private void removePetStats(UUID playerId) {
        int level = competitionLevel.getOrDefault(playerId, 0);
        if (level <= 0 || !activeCompetitionPet.containsKey(playerId)) return;
        StatManager stats = StatManager.getInstance();
        stats.addBonus(playerId, Stat.MAGIC_FIND, -(level * MAGIC_FIND_PER_LEVEL));
        stats.addBonus(playerId, Stat.SPEED, -(level * SPEED_PER_LEVEL));
    }

    // --- Bukkit listeners ---

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyPetStats(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removePetStats(event.getPlayer().getUniqueId());
    }

    // --- persistence ---

    public void load(File dataFolder) {
        File file = new File(dataFolder, "taming.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activeCompetitionPet.clear();
        competitionLevel.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String pet = cfg.getString(key + ".activePet");
                if (pet != null && !pet.isEmpty()) activeCompetitionPet.put(uuid, pet);
                int level = cfg.getInt(key + ".competitionLevel", 0);
                if (level > 0) competitionLevel.put(uuid, level);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "taming.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, String> entry : activeCompetitionPet.entrySet()) {
            cfg.set(entry.getKey().toString() + ".activePet", entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : competitionLevel.entrySet()) {
            cfg.set(entry.getKey().toString() + ".competitionLevel", entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save taming.yml", e);
        }
    }

    public boolean reset(UUID playerId) {
        boolean had = activeCompetitionPet.remove(playerId) != null;
        had |= competitionLevel.remove(playerId) != null;
        return had;
    }

    public Map<UUID, String> getActiveCompetitionPets() {
        return Collections.unmodifiableMap(activeCompetitionPet);
    }
}
