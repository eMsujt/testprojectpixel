package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class WeatherManager {

    public enum WeatherType {
        CLEAR, RAIN, STORM
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

    public void setActiveWeather(WeatherType weather) {
        this.activeWeather = weather;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "weather.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String weather = cfg.getString("activeWeather");
        if (weather != null) {
            try {
                activeWeather = WeatherType.valueOf(weather);
            } catch (IllegalArgumentException ignored) {
                // skip unknown weather type
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "weather.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("activeWeather", activeWeather.name());
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save weather.yml", e);
        }
    }
}
