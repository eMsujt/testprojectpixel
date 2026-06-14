package com.skyblock.plugin.listener;

import com.skyblock.plugin.SkyBlockPlugin;
import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

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
 * XP per entity type is read from the bundled {@code mob_xp.yml} resource;
 * any entity not listed there falls back to {@link #DEFAULT_XP}.
 */
public final class CombatListener implements Listener {

    private static final long DEFAULT_XP = 4L;

    private final SkillManager skillManager = SkillManager.getInstance();
    private final Map<EntityType, Long> mobXp = new EnumMap<>(EntityType.class);

    public CombatListener() {
        InputStream resource = SkyBlockPlugin.getInstance().getResource("mob_xp.yml");
        if (resource == null) {
            return;
        }
        try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(reader);
            for (String key : cfg.getKeys(false)) {
                try {
                    mobXp.put(EntityType.valueOf(key.toUpperCase()), cfg.getLong(key));
                } catch (IllegalArgumentException ignored) {
                    // unknown entity type in config — skip
                }
            }
        } catch (IOException e) {
            SkyBlockPlugin.getInstance().getLogger().warning("Failed to read mob_xp.yml: " + e.getMessage());
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        long xp = mobXp.getOrDefault(event.getEntity().getType(), DEFAULT_XP);
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
