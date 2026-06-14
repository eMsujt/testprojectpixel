package com.skyblock.plugin.accessories;

import com.skyblock.core.accessory.AccessoryBagManager;
import com.skyblock.core.talisman.TalismanManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AccessoryManager implements Listener {

    private static final AccessoryManager INSTANCE = new AccessoryManager();

    /** Persisted accessory lists keyed by player UUID (talisman type names). */
    private final Map<UUID, List<String>> accessories = new HashMap<>();

    private AccessoryManager() {}

    public static AccessoryManager getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        List<String> names = accessories.getOrDefault(uuid, Collections.emptyList());
        for (String name : names) {
            try {
                TalismanManager.TalismanType type = TalismanManager.TalismanType.valueOf(name);
                AccessoryBagManager.getInstance().addAccessory(uuid, type);
            } catch (IllegalArgumentException ignored) {
                // unknown talisman type — skip
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        List<String> names = new ArrayList<>();
        for (TalismanManager.TalismanType type : AccessoryBagManager.getInstance().getContents(uuid)) {
            names.add(type.name());
        }
        accessories.put(uuid, names);
    }

    public List<String> getAccessories(UUID playerId) {
        return Collections.unmodifiableList(accessories.getOrDefault(playerId, Collections.emptyList()));
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "accessories.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        accessories.clear();
        ConfigurationSection root = cfg.getConfigurationSection("accessories");
        if (root == null) {
            return;
        }
        for (String key : root.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                List<String> names = root.getStringList(key);
                if (!names.isEmpty()) {
                    accessories.put(uuid, new ArrayList<>(names));
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "accessories.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<String>> entry : accessories.entrySet()) {
            cfg.set("accessories." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save accessories.yml", e);
        }
    }
}
