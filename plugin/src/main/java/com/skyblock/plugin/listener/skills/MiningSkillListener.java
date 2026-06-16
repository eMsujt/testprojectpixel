package com.skyblock.plugin.listener.skills;

import com.skyblock.core.manager.SkillManager;
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
 * Awards Mining XP through {@link SkillManager} whenever a player mines any ore
 * or stone type and fires level-up rewards when the player's level increases.
 *
 * <p>The block &rarr; XP table is loaded from the bundled {@code mining_blocks.yml}
 * resource (read straight from the jar); when that resource is missing or unreadable
 * the listener falls back to its built-in {@link #DEFAULT_MINING_XP defaults}.</p>
 */
public final class MiningSkillListener implements Listener {

    /** Built-in fallback Mining XP per block, keyed by ore/stone {@link Material}. */
    private static final Map<Material, Long> DEFAULT_MINING_XP = Map.ofEntries(
            Map.entry(Material.STONE,             1L),
            Map.entry(Material.COBBLESTONE,       1L),
            Map.entry(Material.COAL_ORE,          5L),
            Map.entry(Material.IRON_ORE,          5L),
            Map.entry(Material.GOLD_ORE,         10L),
            Map.entry(Material.DIAMOND_ORE,      30L),
            Map.entry(Material.EMERALD_ORE,      30L),
            Map.entry(Material.LAPIS_ORE,        25L),
            Map.entry(Material.REDSTONE_ORE,      7L),
            Map.entry(Material.NETHER_QUARTZ_ORE, 10L)
    );

    /** Mining XP granted per block broken, keyed by ore/stone {@link Material}. */
    private final Map<Material, Long> miningXp;

    private final SkillManager skillManager = SkillManager.getInstance();

    /**
     * Loads the block &rarr; XP table from {@code mining_blocks.yml}, falling back
     * to {@link #DEFAULT_MINING_XP} when the resource is absent or unreadable.
     *
     * @param plugin the owning plugin, used for resource access and logging
     */
    public MiningSkillListener(JavaPlugin plugin) {
        this.miningXp = loadMiningXp(plugin);
    }

    private static Map<Material, Long> loadMiningXp(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("mining_blocks.yml");
        if (resource == null) {
            return DEFAULT_MINING_XP;
        }
        YamlConfiguration cfg;
        try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read mining_blocks.yml: " + e.getMessage());
            return DEFAULT_MINING_XP;
        }
        ConfigurationSection section = cfg.getConfigurationSection("blocks");
        if (section == null) {
            return DEFAULT_MINING_XP;
        }
        Map<Material, Long> table = new EnumMap<>(Material.class);
        for (String key : section.getKeys(false)) {
            Material material = Material.matchMaterial(key);
            if (material == null) {
                plugin.getLogger().warning("Unknown material in mining_blocks.yml: " + key);
                continue;
            }
            table.put(material, section.getLong(key));
        }
        if (table.isEmpty()) {
            return DEFAULT_MINING_XP;
        }
        plugin.getLogger().info("Loaded Mining XP for " + table.size() + " block types.");
        return table;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Long xp = miningXp.get(event.getBlock().getType());
        if (xp == null) {
            return;
        }
        grantXP(event.getPlayer(), xp);
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, Skill.MINING);
        skillManager.addXP(id, Skill.MINING, amount);
        int after = skillManager.getLevel(id, Skill.MINING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, Skill.MINING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eMining §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
