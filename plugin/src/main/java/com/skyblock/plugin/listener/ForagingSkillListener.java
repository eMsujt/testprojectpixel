package com.skyblock.plugin.listener;

import com.skyblock.core.skills.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Awards Foraging XP through {@link SkillManager} whenever a player chops any
 * log type and fires level-up rewards when the player's level increases.
 *
 * <p>The block &rarr; XP table is loaded from the bundled {@code foraging_blocks.yml}
 * resource (read straight from the jar); when that resource is missing or unreadable
 * the listener falls back to its built-in {@link #DEFAULT_FORAGING_XP defaults}.</p>
 */
public final class ForagingSkillListener implements Listener {

    /** Built-in fallback Foraging XP per log broken, keyed by log {@link Material}. */
    private static final Map<Material, Long> DEFAULT_FORAGING_XP = Map.ofEntries(
            Map.entry(Material.OAK_LOG,       6L),
            Map.entry(Material.BIRCH_LOG,     6L),
            Map.entry(Material.SPRUCE_LOG,    6L),
            Map.entry(Material.JUNGLE_LOG,    6L),
            Map.entry(Material.ACACIA_LOG,    6L),
            Map.entry(Material.DARK_OAK_LOG,  6L),
            Map.entry(Material.MANGROVE_LOG,  6L),
            Map.entry(Material.CHERRY_LOG,    6L)
    );

    /** Foraging XP granted per log broken, keyed by log {@link Material}. */
    private final Map<Material, Long> foragingXp;

    private final SkillManager skillManager = SkillManager.getInstance();

    /**
     * Loads the block &rarr; XP table from {@code foraging_blocks.yml}, falling back
     * to {@link #DEFAULT_FORAGING_XP} when the resource is absent or unreadable.
     *
     * @param plugin the owning plugin, used for resource access and logging
     */
    public ForagingSkillListener(JavaPlugin plugin) {
        this.foragingXp = loadForagingXp(plugin);
    }

    private static Map<Material, Long> loadForagingXp(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("foraging_blocks.yml");
        if (resource == null) {
            return DEFAULT_FORAGING_XP;
        }
        YamlConfiguration cfg;
        try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read foraging_blocks.yml: " + e.getMessage());
            return DEFAULT_FORAGING_XP;
        }
        ConfigurationSection section = cfg.getConfigurationSection("blocks");
        if (section == null) {
            return DEFAULT_FORAGING_XP;
        }
        Map<Material, Long> table = new EnumMap<>(Material.class);
        for (String key : section.getKeys(false)) {
            Material material = Material.matchMaterial(key);
            if (material == null) {
                plugin.getLogger().warning("Unknown material in foraging_blocks.yml: " + key);
                continue;
            }
            table.put(material, section.getLong(key));
        }
        if (table.isEmpty()) {
            return DEFAULT_FORAGING_XP;
        }
        plugin.getLogger().info("Loaded Foraging XP for " + table.size() + " block types.");
        return table;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = foragingXp.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        grantXP(event.getPlayer(), xp);
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, Skill.FORAGING);
        skillManager.addXP(id, Skill.FORAGING, amount);
        int after = skillManager.getLevel(id, Skill.FORAGING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, Skill.FORAGING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eForaging §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
