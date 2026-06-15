package com.skyblock.plugin.skill;

import com.skyblock.core.skills.SkillManager.SkillType;
import com.skyblock.plugin.skills.SkillXPConfig;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton holder for each skill's per-level XP requirements, loaded from the
 * bundled {@code skill_xp.yml} resource.
 *
 * <p>The requirements live under a {@code requirements} section keyed by each
 * skill's {@link SkillType#key() lowercase name}; entry {@code i} is the XP needed
 * to advance from level {@code i} to level {@code i + 1}. This is the
 * non-cumulative counterpart to the threshold curves {@code SkillManager} reads
 * from {@code skills.yml}. Any skill left unconfigured falls back to the shared
 * {@link SkillXPConfig#STANDARD} table.</p>
 */
public final class SkillXpConfig {

    private static final SkillXpConfig INSTANCE = new SkillXpConfig();

    /** Per-skill, per-level XP requirements loaded from {@code skill_xp.yml}. */
    private final Map<SkillType, long[]> requirements = new EnumMap<>(SkillType.class);

    private SkillXpConfig() {}

    public static SkillXpConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Loads each skill's per-level XP requirements from the bundled
     * {@code skill_xp.yml} resource (read straight from the jar). Any skill left
     * unconfigured keeps using the shared {@link SkillXPConfig#STANDARD} table.
     *
     * @param plugin the owning plugin, used for resource access and logging
     */
    public void load(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("skill_xp.yml");
        if (resource == null) {
            return;
        }
        YamlConfiguration cfg;
        try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read skill_xp.yml: " + e.getMessage());
            return;
        }
        ConfigurationSection section = cfg.getConfigurationSection("requirements");
        if (section == null) {
            return;
        }
        requirements.clear();
        for (SkillType skill : SkillType.values()) {
            List<Long> values = section.getLongList(skill.key());
            if (values.isEmpty()) {
                continue;
            }
            long[] table = new long[values.size()];
            for (int i = 0; i < table.length; i++) {
                table[i] = values.get(i);
            }
            requirements.put(skill, table);
        }
        plugin.getLogger().info("Loaded per-level XP requirements for " + requirements.size() + " skills.");
    }

    /**
     * The full per-level XP requirement table for {@code skill}, or the shared
     * {@link SkillXPConfig#STANDARD} default when none was configured.
     */
    public long[] requirements(SkillType skill) {
        long[] table = requirements.get(skill);
        return table != null ? table : SkillXPConfig.STANDARD;
    }

    /**
     * XP needed to advance from {@code level} to {@code level + 1} in {@code skill}.
     * Returns {@code 0} once {@code level} is at or beyond the configured cap.
     */
    public long xpToNextLevel(SkillType skill, int level) {
        long[] table = requirements(skill);
        if (level < 0 || level >= table.length) {
            return 0L;
        }
        return table[level];
    }
}
