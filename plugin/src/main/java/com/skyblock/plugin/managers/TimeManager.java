package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class TimeManager {

    public enum SkyblockTime {
        DAWN, MORNING, NOON, AFTERNOON, DUSK, EVENING, NIGHT, MIDNIGHT
    }

    private static final TimeManager INSTANCE = new TimeManager();

    private SkyblockTime currentTime = SkyblockTime.MORNING;

    private TimeManager() {}

    public static TimeManager getInstance() {
        return INSTANCE;
    }

    public SkyblockTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(SkyblockTime time) {
        this.currentTime = time;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "time.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String timeName = cfg.getString("current-time");
        if (timeName != null) {
            try {
                currentTime = SkyblockTime.valueOf(timeName);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "time.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("current-time", currentTime.name());
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save time.yml", e);
        }
    }
}
