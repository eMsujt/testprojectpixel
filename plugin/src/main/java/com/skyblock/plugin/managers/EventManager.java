package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class EventManager {

    public enum SkyblockEvent {
        DOUBLE_XP, DOUBLE_COINS, FISHING_FEST, SPOOKY_FESTIVAL, TRAVELING_ZOO, DARK_AUCTION, JERRY_WORKSHOP
    }

    private static final EventManager INSTANCE = new EventManager();

    private SkyblockEvent activeEvent;

    private EventManager() {}

    public static EventManager getInstance() {
        return INSTANCE;
    }

    public SkyblockEvent getActiveEvent() {
        return activeEvent;
    }

    public void setActiveEvent(SkyblockEvent event) {
        Objects.requireNonNull(event, "event");
        this.activeEvent = event;
    }

    public void clearActiveEvent() {
        this.activeEvent = null;
    }

    public boolean hasActiveEvent() {
        return activeEvent != null;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "event.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String active = cfg.getString("activeEvent");
        if (active != null) {
            try {
                activeEvent = SkyblockEvent.valueOf(active);
            } catch (IllegalArgumentException ignored) {
                // skip unknown event name
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "event.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        if (activeEvent != null) {
            cfg.set("activeEvent", activeEvent.name());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save event.yml", e);
        }
    }
}
