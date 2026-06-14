package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class WeatherManager {

    public enum WeatherType {
        CLEAR, RAIN, STORM, SNOW
    }

    private static final WeatherManager INSTANCE = new WeatherManager();

    private WeatherType activeWeather = WeatherType.CLEAR;

    private WeatherManager() {}

    public static WeatherManager getInstance() {
        return INSTANCE;
    }

    public WeatherType getActiveWeather() {
        return activeWeather;
    }

    public void setActiveWeather(WeatherType type) {
        this.activeWeather = type;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "weather.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String raw = cfg.getString("weather");
        if (raw != null) {
            try {
                activeWeather = WeatherType.valueOf(raw.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // keep default
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "weather.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("weather", activeWeather.name());
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save weather.yml", e);
        }
    }
}
