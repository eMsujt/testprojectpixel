package com.skyblock.core.manager;

import com.skyblock.core.model.SkyBlockPlayer;
import com.skyblock.core.persistence.DataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class PlayerDataManager implements Listener {

    private static final PlayerDataManager INSTANCE = new PlayerDataManager();

    private final DataManager dataManager = DataManager.getInstance();
    private final Map<UUID, SkyBlockPlayer> cache = new HashMap<>();

    private PlayerDataManager() {}

    public static PlayerDataManager getInstance() {
        return INSTANCE;
    }

    public void load(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        dataManager.load(uuid);
        cache.computeIfAbsent(uuid, SkyBlockPlayer::new);
    }

    public void save(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        dataManager.save(uuid);
    }

    public void saveAndEvict(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        dataManager.saveAndEvict(uuid);
        cache.remove(uuid);
    }

    public Optional<SkyBlockPlayer> getPlayer(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return Optional.ofNullable(cache.get(uuid));
    }

    public SkyBlockPlayer getOrCreate(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return cache.computeIfAbsent(uuid, SkyBlockPlayer::new);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        load(event.getPlayer().getUniqueId());
        event.getPlayer().sendMessage("§aWelcome to §6SkyBlock§a!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        saveAndEvict(event.getPlayer().getUniqueId());
    }
}
