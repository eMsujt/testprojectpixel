package com.skyblock.plugin.listener;

import com.skyblock.plugin.managers.WeatherManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public final class WeatherListener implements Listener {

    private final WeatherManager weatherManager = WeatherManager.getInstance();

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        WeatherManager.WeatherType active = weatherManager.getActiveWeather();
        if (active == null) {
            return;
        }
        boolean shouldRain = active == WeatherManager.WeatherType.RAIN
                || active == WeatherManager.WeatherType.STORM
                || active == WeatherManager.WeatherType.SNOW;
        if (event.toWeatherState() != shouldRain) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
        WeatherManager.WeatherType active = weatherManager.getActiveWeather();
        if (active == null) {
            return;
        }
        boolean shouldThunder = active == WeatherManager.WeatherType.STORM;
        if (event.toThunderState() != shouldThunder) {
            event.setCancelled(true);
        }
    }
}
