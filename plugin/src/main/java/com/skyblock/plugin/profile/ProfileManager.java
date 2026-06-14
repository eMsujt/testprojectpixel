package com.skyblock.plugin.profile;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of {@link PlayerProfile} instances keyed by player UUID.
 *
 * <p>Profiles are persisted to {@code plugins/SkyBlock/profiles/<uuid>.yml}.
 * On {@link PlayerJoinEvent} a player's profile is loaded asynchronously and,
 * on {@link PlayerQuitEvent}, saved asynchronously so file I/O never runs on
 * the server main thread.</p>
 *
 * <p>The {@link PlayerProfile} map is mutated only on the main thread; access
 * it from the server main thread or guard it externally.</p>
 */
public final class ProfileManager implements Listener {

    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();

    private final Plugin plugin;
    private final File profilesDir;

    /**
     * Creates a registry backed by per-player YAML files under the plugin's
     * {@code profiles} data directory.
     *
     * @param plugin the owning plugin, used for scheduling and the data folder
     */
    public ProfileManager(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.profilesDir = new File(plugin.getDataFolder(), "profiles");
    }

    /**
     * Returns the profile for the given player, creating and registering a new
     * empty one if none exists yet.
     *
     * @param uuid unique identifier of the player
     * @return the player's profile, never {@code null}
     */
    public PlayerProfile getOrCreate(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.computeIfAbsent(uuid, PlayerProfile::new);
    }

    /**
     * Returns the profile for the given player, or {@code null} if none has
     * been registered.
     *
     * @param uuid unique identifier of the player
     * @return the player's profile, or {@code null}
     */
    public PlayerProfile getProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.get(uuid);
    }

    /**
     * Returns whether a profile is registered for the given player.
     *
     * @param uuid unique identifier of the player
     * @return {@code true} if a profile exists
     */
    public boolean hasProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.containsKey(uuid);
    }

    /**
     * Removes the profile for the given player.
     *
     * @param uuid unique identifier of the player
     * @return the removed profile, or {@code null} if none existed
     */
    public PlayerProfile removeProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.remove(uuid);
    }

    /**
     * Returns an immutable snapshot of all registered profiles keyed by UUID.
     *
     * @return the registered profiles
     */
    public Map<UUID, PlayerProfile> getProfiles() {
        return Collections.unmodifiableMap(profiles);
    }

    /**
     * Loads the joining player's profile from disk asynchronously, then applies
     * it to the in-memory registry on the main thread.
     *
     * @param event the join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Map<String, Long> loaded = readSkillXp(uuid);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                PlayerProfile profile = getOrCreate(uuid);
                loaded.forEach(profile::setSkillXp);
            });
        });
    }

    /**
     * Saves the quitting player's profile to disk asynchronously, snapshotting
     * its state on the main thread first.
     *
     * @param event the quit event
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = profiles.get(player.getUniqueId());
        if (profile == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        Map<String, Long> snapshot = profile.getSkillXp();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,
                () -> writeSkillXp(uuid, snapshot));
    }

    private Map<String, Long> readSkillXp(UUID uuid) {
        File file = new File(profilesDir, uuid + ".yml");
        Map<String, Long> result = new HashMap<>();
        if (!file.exists()) {
            return result;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if (cfg.isConfigurationSection("skillXp")) {
            for (String skill : cfg.getConfigurationSection("skillXp").getKeys(false)) {
                result.put(skill, cfg.getLong("skillXp." + skill));
            }
        }
        return result;
    }

    private void writeSkillXp(UUID uuid, Map<String, Long> skillXp) {
        if (!profilesDir.exists()) {
            profilesDir.mkdirs();
        }
        File file = new File(profilesDir, uuid + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, Long> entry : skillXp.entrySet()) {
            cfg.set("skillXp." + entry.getKey(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save profile " + uuid + ": " + e.getMessage());
        }
    }
}
