package com.skyblock.plugin.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton that loads per-skill cumulative XP threshold curves from the bundled
 * {@code skills.yml} resource and exposes level-lookup helpers.
 *
 * <p>Curves live under a {@code curves} section keyed by lowercase skill name
 * (e.g. {@code farming}, {@code combat}). Each list entry is the total XP required
 * to reach the matching level: index 0 = level 1, index N-1 = max level. Call
 * {@link #load(JavaPlugin)} once during {@code onEnable} before querying levels.</p>
 */
public final class SkillManager {

    private static final SkillManager INSTANCE = new SkillManager();

    /** Per-skill cumulative XP threshold arrays; empty until {@link #load} runs. */
    private final Map<String, long[]> curves = new HashMap<>();

    private SkillManager() {}

    public static SkillManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads the {@code curves} section of the bundled {@code skills.yml} and populates
     * the in-memory threshold table. Safe to call more than once; each call replaces
     * the previously loaded data.
     *
     * @param plugin the owning plugin, used for resource access and logging
     */
    public void load(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("skills.yml");
        if (resource == null) {
            plugin.getLogger().warning("skills.yml not found in jar — skill levels will always be 0.");
            return;
        }
        YamlConfiguration cfg;
        try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read skills.yml: " + e.getMessage());
            return;
        }
        ConfigurationSection section = cfg.getConfigurationSection("curves");
        if (section == null) {
            plugin.getLogger().warning("skills.yml is missing the 'curves' section.");
            return;
        }
        curves.clear();
        for (String skill : section.getKeys(false)) {
            List<Long> values = section.getLongList(skill);
            if (values.isEmpty()) {
                continue;
            }
            long[] threshold = new long[values.size()];
            for (int i = 0; i < threshold.length; i++) {
                threshold[i] = values.get(i);
            }
            curves.put(skill.toLowerCase(), threshold);
        }
        plugin.getLogger().info("SkillManager: loaded XP curves for " + curves.size() + " skills.");
    }

    /**
     * Returns the skill level corresponding to {@code totalXp} accumulated XP.
     * Returns 0 if no curve is loaded for the skill or the player has no XP.
     *
     * @param skill   the lowercase skill name (e.g. {@code "farming"})
     * @param totalXp cumulative XP the player holds in that skill
     * @return the computed level (0 = no levels reached)
     */
    public int levelForXp(String skill, long totalXp) {
        long[] curve = curves.get(skill == null ? null : skill.toLowerCase());
        if (curve == null) {
            return 0;
        }
        int level = 0;
        while (level < curve.length && totalXp >= curve[level]) {
            level++;
        }
        return level;
    }

    /**
     * Returns the maximum level defined by the loaded curve for the given skill,
     * or 0 if no curve was loaded for it.
     *
     * @param skill the lowercase skill name
     * @return the skill's maximum level
     */
    public int maxLevel(String skill) {
        long[] curve = curves.get(skill == null ? null : skill.toLowerCase());
        return curve == null ? 0 : curve.length;
    }

    /**
     * Returns an unmodifiable view of the loaded skill-name to threshold-array map.
     * Each array entry at index {@code i} is the total XP required to reach level
     * {@code i + 1}.
     *
     * @return the loaded XP curves, keyed by lowercase skill name
     */
    public Map<String, long[]> getCurves() {
        return Collections.unmodifiableMap(curves);
    }
}
