package com.skyblock.plugin.profile;

import com.skyblock.plugin.SkyBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Singleton listener that loads and saves a player's core data when they join
 * or quit.
 *
 * <p>On {@link PlayerJoinEvent} the player's {@link PlayerProfile} is resolved
 * from {@link ProfileManager} and their {@link com.skyblock.core.manager.PlayerDataManager.PlayerData}
 * is created in the in-memory cache, then hydrated from
 * {@code plugins/SkyBlock/players/<uuid>.yml} on an async thread.</p>
 *
 * <p>On {@link PlayerQuitEvent} a YAML snapshot of the player's
 * {@link com.skyblock.core.manager.PlayerDataManager.PlayerData} is written to
 * disk asynchronously, then the cache entry is evicted.</p>
 *
 * <p>This type is registered as an event listener in
 * {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}.</p>
 */
public final class PlayerDataManager implements Listener {

    private static final PlayerDataManager INSTANCE = new PlayerDataManager();

    private PlayerDataManager() {}

    public static PlayerDataManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the joining player's profile, loading or creating it as needed.
     *
     * @param uuid unique identifier of the player
     * @return the player's profile, never {@code null}
     */
    public PlayerProfile load(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return ProfileManager.getInstance().getOrCreate(uuid);
    }

    /**
     * Creates the player's in-memory profile and core data entry, then
     * asynchronously hydrates the core data from
     * {@code plugins/SkyBlock/players/<uuid>.yml} if a snapshot exists.
     *
     * @param event the join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        load(uuid);

        com.skyblock.core.manager.PlayerDataManager.PlayerData data =
                com.skyblock.core.manager.PlayerDataManager.getInstance().getOrCreate(uuid);

        SkyBlockPlugin plugin = SkyBlockPlugin.getInstance();
        File file = new File(new File(plugin.getDataFolder(), "players"), uuid + ".yml");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!file.exists()) {
                return;
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            Bukkit.getScheduler().runTask(plugin, () -> applyFromDisk(data, cfg));
        });
    }

    /**
     * Asynchronously persists the player's core data to
     * {@code plugins/SkyBlock/players/<uuid>.yml}, then evicts the cache entry.
     *
     * <p>The YAML snapshot is built on the main thread before the async write
     * begins, so the data object is only ever read under the main-thread
     * guarantee.</p>
     *
     * @param event the quit event
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        com.skyblock.core.manager.PlayerDataManager mgr =
                com.skyblock.core.manager.PlayerDataManager.getInstance();
        Optional<com.skyblock.core.manager.PlayerDataManager.PlayerData> opt = mgr.get(uuid);
        if (opt.isEmpty()) {
            return;
        }
        YamlConfiguration cfg = buildSnapshot(opt.get());
        mgr.remove(uuid);

        SkyBlockPlugin plugin = SkyBlockPlugin.getInstance();
        File dir = new File(plugin.getDataFolder(), "players");
        File file = new File(dir, uuid + ".yml");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                cfg.save(file);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to save player data for " + uuid + ": " + e.getMessage());
            }
        });
    }

    private static YamlConfiguration buildSnapshot(
            com.skyblock.core.manager.PlayerDataManager.PlayerData data) {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("coins", data.getCoins());
        data.getSkillLevels().forEach((skill, level) -> cfg.set("skills." + skill, level));
        return cfg;
    }

    private static void applyFromDisk(
            com.skyblock.core.manager.PlayerDataManager.PlayerData data, YamlConfiguration cfg) {
        data.setCoins(cfg.getLong("coins", 0L));
        ConfigurationSection skills = cfg.getConfigurationSection("skills");
        if (skills != null) {
            for (String skill : skills.getKeys(false)) {
                data.setSkillLevel(skill, cfg.getInt("skills." + skill, 0));
            }
        }
    }
}
