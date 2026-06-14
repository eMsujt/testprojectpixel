package com.skyblock.plugin.profile;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Async YAML persistence for {@link SkyBlockProfile}.
 *
 * <p>Files are written to {@code plugins/SkyBlock/profiles/<uuid>.yml}.
 * All disk I/O runs on a Bukkit async thread; the returned
 * {@link CompletableFuture} completes there as well, so callers that need to
 * touch Bukkit state must schedule back to the main thread themselves.</p>
 */
public final class ProfileRepository {

    private static final ProfileRepository INSTANCE = new ProfileRepository();

    private ProfileRepository() {}

    public static ProfileRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Saves the profile to disk asynchronously.
     *
     * <p>The YAML snapshot is built on the calling thread (reading the profile
     * accessors), then written off-thread so the profile is only ever read on
     * the main thread.</p>
     *
     * @param plugin  the owning plugin, used for the scheduler and data folder
     * @param profile the profile to persist
     * @return a future that completes when the file has been written, or
     *         completes exceptionally if the write fails
     */
    public CompletableFuture<Void> saveAsync(Plugin plugin, SkyBlockProfile profile) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(profile, "profile");

        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("uuid", profile.getUuid().toString());
        cfg.set("purse", profile.getPurse());
        cfg.set("bank", profile.getBank());
        for (Map.Entry<String, Long> entry : profile.getSkillXp().entrySet()) {
            cfg.set("skills." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Long> entry : profile.getCollectionXp().entrySet()) {
            cfg.set("collections." + entry.getKey(), entry.getValue());
        }

        File dir = new File(plugin.getDataFolder(), "profiles");
        File file = new File(dir, profile.getUuid() + ".yml");

        CompletableFuture<Void> future = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
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
     * Loads the profile from disk asynchronously.
     *
     * <p>Returns a new empty {@link SkyBlockProfile} if no file exists yet.</p>
     *
     * @param plugin the owning plugin, used for the scheduler and data folder
     * @param uuid   the UUID whose profile to load
     * @return a future that completes with the loaded (or freshly created)
     *         profile, or completes exceptionally if the read fails
     */
    public CompletableFuture<SkyBlockProfile> loadAsync(Plugin plugin, UUID uuid) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(uuid, "uuid");

        File file = new File(new File(plugin.getDataFolder(), "profiles"), uuid + ".yml");

        CompletableFuture<SkyBlockProfile> future = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                SkyBlockProfile profile = new SkyBlockProfile(uuid);
                if (file.exists()) {
                    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                    profile.setPurse(cfg.getLong("purse", 0L));
                    profile.setBank(cfg.getLong("bank", 0L));

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
                future.complete(profile);
            } catch (Exception e) {
                plugin.getLogger().warning(
                        "Failed to load profile for " + uuid + ": " + e.getMessage());
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
