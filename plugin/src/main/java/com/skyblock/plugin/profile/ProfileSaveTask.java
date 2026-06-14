package com.skyblock.plugin.profile;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Async periodic task that serializes every loaded {@link PlayerProfile} to an
 * individual YAML file under {@code <dataFolder>/profiles/<uuid>.yml}.
 *
 * <p>Schedule with
 * {@link org.bukkit.scheduler.BukkitTask#runTaskAsynchronously(org.bukkit.plugin.Plugin)}
 * or a timed variant; it takes immutable snapshots from the profile accessors so
 * a short-lived data race on the main thread is the only risk.</p>
 */
public final class ProfileSaveTask extends BukkitRunnable {

    private final File dataFolder;
    private final Logger logger;

    public ProfileSaveTask(File dataFolder, Logger logger) {
        this.dataFolder = dataFolder;
        this.logger = logger;
    }

    @Override
    public void run() {
        File profilesDir = new File(dataFolder, "profiles");
        if (!profilesDir.exists()) {
            profilesDir.mkdirs();
        }
        for (Map.Entry<UUID, PlayerProfile> entry :
                ProfileManager.getInstance().getProfiles().entrySet()) {
            save(profilesDir, entry.getValue());
        }
    }

    private void save(File dir, PlayerProfile profile) {
        File file = new File(dir, profile.getUuid() + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();

        cfg.set("purse", profile.getPurse());
        cfg.set("bank", profile.getBank());

        for (Map.Entry<String, Long> entry : profile.getSkillXp().entrySet()) {
            cfg.set("skills." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Long> entry : profile.getCollectionXp().entrySet()) {
            cfg.set("collections." + entry.getKey(), entry.getValue());
        }

        try {
            cfg.save(file);
        } catch (IOException e) {
            logger.warning("Failed to save profile for " + profile.getUuid() + ": " + e.getMessage());
        }
    }
}
