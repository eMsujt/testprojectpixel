package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * Awards Combat XP through {@link SkillManager} when a player kills a living
 * entity and fires level-up rewards when the player's level increases.
 *
 * <p>The mob &rarr; XP table is loaded from the bundled {@code mob_xp.yml}
 * resource (read straight from the jar); when that resource is missing or unreadable
 * the listener falls back to its built-in {@link #DEFAULT_MOB_XP defaults}.</p>
 */
public final class CombatListener implements Listener {

    /** Built-in fallback Combat XP per mob, keyed by {@link EntityType}. */
    private static final Map<EntityType, Long> DEFAULT_MOB_XP = Map.ofEntries(
            Map.entry(EntityType.ZOMBIE,           5L),
            Map.entry(EntityType.SKELETON,         5L),
            Map.entry(EntityType.SPIDER,           5L),
            Map.entry(EntityType.CAVE_SPIDER,      5L),
            Map.entry(EntityType.CREEPER,          5L),
            Map.entry(EntityType.ENDERMAN,        12L),
            Map.entry(EntityType.WITCH,           12L),
            Map.entry(EntityType.SLIME,            4L),
            Map.entry(EntityType.MAGMA_CUBE,       4L),
            Map.entry(EntityType.BLAZE,           10L),
            Map.entry(EntityType.GHAST,           10L)
    );

    /** Combat XP granted per mob killed, keyed by {@link EntityType}. */
    private final Map<EntityType, Long> mobXp;

    private final SkillManager skillManager = SkillManager.getInstance();

    /**
     * Loads the mob &rarr; XP table from {@code mob_xp.yml}, falling back
     * to {@link #DEFAULT_MOB_XP} when the resource is absent or unreadable.
     *
     * @param plugin the owning plugin, used for resource access and logging
     */
    public CombatListener(JavaPlugin plugin) {
        this.mobXp = loadMobXp(plugin);
    }

    private static Map<EntityType, Long> loadMobXp(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("mob_xp.yml");
        if (resource == null) {
            return DEFAULT_MOB_XP;
        }
        YamlConfiguration cfg;
        try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read mob_xp.yml: " + e.getMessage());
            return DEFAULT_MOB_XP;
        }
        ConfigurationSection section = cfg.getConfigurationSection("mobs");
        if (section == null) {
            return DEFAULT_MOB_XP;
        }
        Map<EntityType, Long> table = new EnumMap<>(EntityType.class);
        for (String key : section.getKeys(false)) {
            EntityType type;
            try {
                type = EntityType.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Unknown entity type in mob_xp.yml: " + key);
                continue;
            }
            table.put(type, section.getLong(key));
        }
        if (table.isEmpty()) {
            return DEFAULT_MOB_XP;
        }
        plugin.getLogger().info("Loaded Combat XP for " + table.size() + " mob types.");
        return table;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        Long xp = mobXp.get(event.getEntityType());
        if (xp == null) {
            return;
        }
        UUID id = killer.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.COMBAT);
        skillManager.addXP(id, SkillType.COMBAT, xp);
        int after = skillManager.getLevel(id, SkillType.COMBAT);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.COMBAT, before, after);
            killer.sendTitle("§aSkill Level Up!", "§eCombat §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
