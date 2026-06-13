package com.skyblock.core.title;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TitleManager {

    private final Map<UUID, List<String>> titles = new HashMap<>();

    public List<String> getTitles(UUID playerId) {
        return Collections.unmodifiableList(titles.getOrDefault(playerId, Collections.emptyList()));
    }

    public void addTitle(UUID playerId, String title) {
        titles.computeIfAbsent(playerId, k -> new ArrayList<>()).add(title);
    }

    public boolean removeTitle(UUID playerId, String title) {
        List<String> list = titles.get(playerId);
        return list != null && list.remove(title);
    }

    public boolean hasTitle(UUID playerId, String title) {
        List<String> list = titles.get(playerId);
        return list != null && list.contains(title);
    }

    public void save(File dataFolder) {
        File dir = new File(dataFolder, "data/titles");
        dir.mkdirs();
        for (Map.Entry<UUID, List<String>> entry : titles.entrySet()) {
            File file = new File(dir, entry.getKey().toString() + ".yml");
            YamlConfiguration cfg = new YamlConfiguration();
            cfg.set("titles", entry.getValue());
            try {
                cfg.save(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save titles for " + entry.getKey(), e);
            }
        }
    }

    public void load(File dataFolder) {
        File dir = new File(dataFolder, "data/titles");
        titles.clear();
        if (!dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }
        for (File file : files) {
            String name = file.getName();
            try {
                UUID uuid = UUID.fromString(name.substring(0, name.length() - 4));
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                List<String> list = cfg.getStringList("titles");
                if (!list.isEmpty()) {
                    titles.put(uuid, new ArrayList<>(list));
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }
}
