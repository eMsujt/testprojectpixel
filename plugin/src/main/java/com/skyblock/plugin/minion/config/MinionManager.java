package com.skyblock.plugin.minion.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * YAML-driven registry of minion tier specifications loaded from
 * {@code minions.yml}.
 *
 * <p>Each key under the {@code minions} section is a minion id (e.g.
 * {@code COBBLESTONE}, the Cobblestone Miner) whose {@code tiers} map carries,
 * per tier numeral (I … XI), that tier's {@link TierSpec#productionIntervalTicks()
 * production interval} in server ticks and its
 * {@link TierSpec#storageSlots() storage-slot count}. The bundled resource is
 * read straight from the jar so it never collides with any player-data files
 * written to the data folder.</p>
 */
public final class MinionManager {

    private static final MinionManager INSTANCE = new MinionManager();

    /**
     * A single minion tier's production specification.
     *
     * @param productionIntervalTicks server ticks between production cycles
     * @param storageSlots            inventory slots the minion holds at this tier
     */
    public record TierSpec(int productionIntervalTicks, int storageSlots) {
    }

    /** Per-minion tier specs keyed by minion id, then by 1-based tier number. */
    private final Map<String, Map<Integer, TierSpec>> minions = new LinkedHashMap<>();

    private MinionManager() {
    }

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code minions.yml} from the jar and parses every minion's tier
     * specs. Has no effect if the resource is absent.
     *
     * @param plugin the owning plugin, used for resource access and logging
     */
    public void load(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("minions.yml");
        if (resource == null) {
            return;
        }
        YamlConfiguration cfg;
        try (Reader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (java.io.IOException e) {
            plugin.getLogger().warning("Failed to read minions.yml: " + e.getMessage());
            return;
        }
        ConfigurationSection root = cfg.isConfigurationSection("minions")
                ? cfg.getConfigurationSection("minions")
                : cfg;
        minions.clear();
        for (String id : root.getKeys(false)) {
            ConfigurationSection tiers = root.getConfigurationSection(id + ".tiers");
            if (tiers == null) {
                continue;
            }
            Map<Integer, TierSpec> specs = new LinkedHashMap<>();
            for (String numeral : tiers.getKeys(false)) {
                int tier = romanToInt(numeral);
                if (tier <= 0) {
                    continue;
                }
                ConfigurationSection spec = tiers.getConfigurationSection(numeral);
                if (spec == null) {
                    continue;
                }
                specs.put(tier, new TierSpec(
                        spec.getInt("productionIntervalTicks"),
                        spec.getInt("storageSlots")));
            }
            if (!specs.isEmpty()) {
                minions.put(id, specs);
            }
        }
        plugin.getLogger().info("Loaded tier specs for " + minions.size() + " minions.");
    }

    /**
     * Returns the tier spec for a minion at the given 1-based tier, or
     * {@code null} if the minion or tier is unknown.
     */
    public TierSpec getTier(String minion, int tier) {
        Map<Integer, TierSpec> specs = minions.get(minion);
        return specs == null ? null : specs.get(tier);
    }

    /** Returns the highest tier defined for a minion (0 if unknown). */
    public int getMaxTier(String minion) {
        Map<Integer, TierSpec> specs = minions.get(minion);
        return specs == null ? 0 : specs.size();
    }

    /**
     * Returns an unmodifiable view of a minion's tier specs keyed by 1-based
     * tier number, or an empty map if the minion is unknown.
     */
    public Map<Integer, TierSpec> getTiers(String minion) {
        Map<Integer, TierSpec> specs = minions.get(minion);
        return specs == null ? Collections.emptyMap() : Collections.unmodifiableMap(specs);
    }

    /** Parses a Roman numeral (I … XI) to its integer value, 0 if unparseable. */
    private static int romanToInt(String numeral) {
        int total = 0;
        int prev = 0;
        for (int i = numeral.length() - 1; i >= 0; i--) {
            int value;
            switch (Character.toUpperCase(numeral.charAt(i))) {
                case 'I' -> value = 1;
                case 'V' -> value = 5;
                case 'X' -> value = 10;
                default -> {
                    return 0;
                }
            }
            if (value < prev) {
                total -= value;
            } else {
                total += value;
                prev = value;
            }
        }
        return total;
    }
}
