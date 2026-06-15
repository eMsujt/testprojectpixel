package com.skyblock.plugin.manager;

import com.skyblock.plugin.SkyBlockPlugin;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Handles YAML-based persistence for {@link SkyBlockProfile} instances.
 *
 * <p>Profiles are stored at {@code plugins/SkyBlock/profiles/<uuid>.yml}.
 * Snapshot builds always happen on the main server thread; file I/O is
 * dispatched asynchronously to avoid blocking the server. Not thread-safe;
 * call from the main thread only.</p>
 */
public final class ProfilePersistenceManager {

    private static final ProfilePersistenceManager INSTANCE = new ProfilePersistenceManager();

    private SkyBlockPlugin plugin;

    private ProfilePersistenceManager() {}

    public static ProfilePersistenceManager getInstance() {
        return INSTANCE;
    }

    /**
     * Initializes the manager with the owning plugin instance used for
     * scheduling async I/O. Must be called once during plugin enable.
     *
     * @param plugin the owning plugin instance
     */
    public void init(SkyBlockPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    /**
     * Persists the given profile to {@code plugins/SkyBlock/profiles/<uuid>.yml}
     * without blocking the main thread.
     *
     * <p>The YAML snapshot is built synchronously on the calling (main) thread,
     * then written to disk on an async scheduler thread. No-op if the plugin is
     * not initialized.</p>
     *
     * @param profile the profile to persist
     */
    public void saveAsync(SkyBlockProfile profile) {
        Objects.requireNonNull(profile, "profile");
        SkyBlockPlugin plugin = this.plugin;
        if (plugin == null) {
            return;
        }

        YamlConfiguration cfg = buildConfig(profile);
        File dir = profileDir(plugin);
        File file = new File(dir, profile.getUuid() + ".yml");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                cfg.save(file);
            } catch (IOException e) {
                plugin.getLogger().warning(
                        "Failed to save profile for " + profile.getUuid() + ": " + e.getMessage());
            }
        });
    }

    /**
     * Persists the given profile to {@code plugins/SkyBlock/profiles/<uuid>.yml}
     * without blocking the main thread, returning a future that completes once
     * the write has finished.
     *
     * @param profile the profile to persist
     * @return a future completed when the profile has been written to disk
     */
    public CompletableFuture<Void> save(SkyBlockProfile profile) {
        Objects.requireNonNull(profile, "profile");
        SkyBlockPlugin plugin = this.plugin;
        if (plugin == null) {
            return CompletableFuture.completedFuture(null);
        }

        YamlConfiguration cfg = buildConfig(profile);
        File dir = profileDir(plugin);
        File file = new File(dir, profile.getUuid() + ".yml");

        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                cfg.save(file);
                future.complete(null);
            } catch (IOException e) {
                plugin.getLogger().warning(
                        "Failed to save profile for " + profile.getUuid() + ": " + e.getMessage());
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /**
     * Loads a persisted snapshot for the given UUID and applies it to
     * {@code profile} on the main thread. No-op if no file exists or the
     * plugin is not initialized.
     *
     * @param uuid    the player's UUID
     * @param profile the profile to populate in place
     */
    public void loadAsync(UUID uuid, SkyBlockProfile profile) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(profile, "profile");
        SkyBlockPlugin plugin = this.plugin;
        if (plugin == null) {
            return;
        }

        File file = new File(profileDir(plugin), uuid + ".yml");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!file.exists()) {
                return;
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            Bukkit.getScheduler().runTask(plugin, () -> applyFromDisk(profile, cfg));
        });
    }

    // -- helpers --

    private static File profileDir(SkyBlockPlugin plugin) {
        return new File(plugin.getDataFolder(), "profiles");
    }

    private static YamlConfiguration buildConfig(SkyBlockProfile profile) {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("purse", profile.getPurse());
        cfg.set("bank", profile.getBank());
        cfg.set("notifications.skills", profile.isShowSkillNotifications());
        cfg.set("notifications.collections", profile.isShowCollectionNotifications());
        cfg.set("notifications.pets", profile.isShowPetNotifications());
        for (Map.Entry<String, Long> entry : profile.getSkillXp().entrySet()) {
            cfg.set("skills." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Long> entry : profile.getCollectionXp().entrySet()) {
            cfg.set("collections." + entry.getKey(), entry.getValue());
        }
        return cfg;
    }

    private static void applyFromDisk(SkyBlockProfile profile, YamlConfiguration cfg) {
        profile.setPurse(cfg.getLong("purse", profile.getPurse()));
        profile.setBank(cfg.getLong("bank", profile.getBank()));
        profile.setShowSkillNotifications(cfg.getBoolean("notifications.skills", profile.isShowSkillNotifications()));
        profile.setShowCollectionNotifications(cfg.getBoolean("notifications.collections", profile.isShowCollectionNotifications()));
        profile.setShowPetNotifications(cfg.getBoolean("notifications.pets", profile.isShowPetNotifications()));

        ConfigurationSection skills = cfg.getConfigurationSection("skills");
        if (skills != null) {
            for (String skill : skills.getKeys(false)) {
                profile.setSkillXp(skill, skills.getLong(skill));
            }
        }

        ConfigurationSection collections = cfg.getConfigurationSection("collections");
        if (collections != null) {
            for (String collection : collections.getKeys(false)) {
                profile.setCollectionXp(collection, collections.getLong(collection));
            }
        }
    }
}
