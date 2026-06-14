package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class EventManager {

    public enum SkyblockEvent {
        NONE, FISHING_FESTIVAL, SPOOKY
    }

    private static final EventManager INSTANCE = new EventManager();

    private SkyblockEvent activeEvent = SkyblockEvent.NONE;

    private EventManager() {}

    public static EventManager getInstance() {
        return INSTANCE;
    }

    public SkyblockEvent getActiveEvent() {
        return activeEvent;
    }

    public void setActiveEvent(SkyblockEvent event) {
        this.activeEvent = event;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "event.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String event = cfg.getString("activeEvent");
        if (event != null) {
            try {
                activeEvent = SkyblockEvent.valueOf(event);
            } catch (IllegalArgumentException ignored) {
                // skip unknown event type
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "event.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("activeEvent", activeEvent.name());
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save event.yml", e);
        }
    }
}
