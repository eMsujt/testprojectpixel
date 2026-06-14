package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class MayorManager {

    private static final Map<String, List<String>> DEFAULT_PERKS;

    static {
        Map<String, List<String>> perks = new HashMap<>();
        perks.put("Jerry",    Arrays.asList("Jerrypocalypse", "Jerry's Gifts", "Gift Hunt"));
        perks.put("Aatrox",   Arrays.asList("Slayer XP Buff", "Slayer Quest Limit", "Slayer's Will", "Blood Thirst"));
        perks.put("Diana",    Arrays.asList("Great Spook", "Mythological Ritual", "Lucky!"));
        perks.put("Finnegan", Arrays.asList("Cultivation", "Shining Armor", "Stead Fast", "Blooming Business"));
        perks.put("Cole",     Arrays.asList("Prospection", "Mining Fiesta", "Molten Forge"));
        perks.put("Foxy",     Arrays.asList("What the Dog Doin?", "Extra Pets", "Good Doggy"));
        perks.put("Marina",   Arrays.asList("Fishing Festival", "Luck of the Sea", "Quiver", "Water Breathing"));
        perks.put("Paul",     Arrays.asList("Marauder", "Goblin Raid", "Supply Drop", "Show Off"));
        perks.put("Scorpius", Arrays.asList("Bribe", "Scorched", "Plague"));
        DEFAULT_PERKS = Collections.unmodifiableMap(perks);
    }

    private static final MayorManager INSTANCE = new MayorManager();

    private String currentMayor = "Finnegan";
    private int electionDay = 0;
    private final Map<String, List<String>> mayorPerks = new HashMap<>(DEFAULT_PERKS);
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

    public List<String> getPerks(String mayor) {
        return mayorPerks.getOrDefault(mayor, Collections.emptyList());
    }

    public void setPerks(String mayor, List<String> perks) {
        mayorPerks.put(mayor, new ArrayList<>(perks));
    }

    public Map<String, List<String>> getMayorPerks() {
        return Collections.unmodifiableMap(mayorPerks);
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
        mayorPerks.clear();
        mayorPerks.putAll(DEFAULT_PERKS);
        if (cfg.isConfigurationSection("perks")) {
            for (String name : cfg.getConfigurationSection("perks").getKeys(false)) {
                List<String> stored = cfg.getStringList("perks." + name);
                if (!stored.isEmpty()) {
                    mayorPerks.put(name, new ArrayList<>(stored));
                }
            }
        }
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
        for (Map.Entry<String, List<String>> entry : mayorPerks.entrySet()) {
            cfg.set("perks." + entry.getKey(), entry.getValue());
        }
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
