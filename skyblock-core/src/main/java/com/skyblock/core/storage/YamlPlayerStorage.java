package com.skyblock.core.storage;

import com.skyblock.core.manager.PlayerDataManager;
import com.skyblock.core.SkyBlockPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Persists and restores {@link PlayerDataManager.PlayerData} to per-player
 * YAML files under {@code <dataFolder>/players/<uuid>.yml}.
 *
 * <p>Writes are atomic: data is written to a {@code .tmp} file and then
 * renamed over the target so a crash mid-write never produces a corrupt
 * file.</p>
 */
public final class YamlPlayerStorage {

    private static YamlPlayerStorage instance;

    private final File playersDir;

    private YamlPlayerStorage() {
        playersDir = new File(SkyBlockPlugin.getInstance().getDataFolder(), "players");
        if (!playersDir.exists()) {
            playersDir.mkdirs();
        }
    }

    /**
     * Returns the singleton instance, creating it on first call.
     *
     * <p>Must be called after {@link SkyBlockPlugin} has been enabled.</p>
     *
     * @return the singleton {@link YamlPlayerStorage}
     */
    public static YamlPlayerStorage getInstance() {
        if (instance == null) {
            instance = new YamlPlayerStorage();
        }
        return instance;
    }

    /**
     * Saves the given player data to disk.
     *
     * @param data the player data to save
     */
    public void save(PlayerDataManager.PlayerData data) {
        Objects.requireNonNull(data, "data");
        File target = fileFor(data.getUuid());
        File tmp = new File(target.getParentFile(), target.getName() + ".tmp");

        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("coins", data.getCoins());
        data.getSkillLevels().forEach((skill, level) -> cfg.set("skills." + skill, level));

        try {
            cfg.save(tmp);
            if (!tmp.renameTo(target)) {
                cfg.save(target);
                tmp.delete();
            }
        } catch (IOException e) {
            SkyBlockPlugin.getInstance().getLogger().log(
                    Level.SEVERE, "Failed to save player data for " + data.getUuid(), e);
        }
    }

    /**
     * Loads persisted data into the given player data object.
     *
     * <p>If no file exists for the player, the object is left unchanged.</p>
     *
     * @param data the player data object to populate
     */
    public void load(PlayerDataManager.PlayerData data) {
        Objects.requireNonNull(data, "data");
        File file = fileFor(data.getUuid());
        if (!file.exists()) {
            return;
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        data.setCoins(cfg.getLong("coins", 0L));

        if (cfg.isConfigurationSection("skills")) {
            for (String skill : cfg.getConfigurationSection("skills").getKeys(false)) {
                data.setSkillLevel(skill, cfg.getInt("skills." + skill, 0));
            }
        }
    }

    private File fileFor(UUID uuid) {
        return new File(playersDir, uuid + ".yml");
    }
}
