package com.skyblock.plugin.listener;

import com.skyblock.core.skills.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Awards Enchanting XP through {@link SkillManager} whenever a player enchants
 * an item and fires level-up rewards when the player's level increases.
 *
 * <p>The XP granted is the event's experience level cost multiplied by a factor
 * loaded from the bundled {@code enchanting.yml} resource (read straight from the
 * jar); when that resource is missing or unreadable the listener falls back to
 * its built-in {@link #DEFAULT_MULTIPLIER default}.</p>
 */
public final class EnchantingSkillListener implements Listener {

    /** Built-in fallback Enchanting XP multiplier per level spent on an enchant. */
    private static final long DEFAULT_MULTIPLIER = 6L;

    /** Enchanting XP granted per level spent on an enchant. */
    private final long multiplier;

    private final SkillManager skillManager = SkillManager.getInstance();

    /**
     * Loads the XP multiplier from {@code enchanting.yml}, falling back to
     * {@link #DEFAULT_MULTIPLIER} when the resource is absent or unreadable.
     *
     * @param plugin the owning plugin, used for resource access and logging
     */
    public EnchantingSkillListener(JavaPlugin plugin) {
        this.multiplier = loadMultiplier(plugin);
    }

    private static long loadMultiplier(JavaPlugin plugin) {
        InputStream resource = plugin.getResource("enchanting.yml");
        if (resource == null) {
            return DEFAULT_MULTIPLIER;
        }
        YamlConfiguration cfg;
        try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            cfg = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to read enchanting.yml: " + e.getMessage());
            return DEFAULT_MULTIPLIER;
        }
        if (!cfg.contains("multiplier")) {
            return DEFAULT_MULTIPLIER;
        }
        return cfg.getLong("multiplier");
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        grantXP(event.getEnchanter(), multiplier * event.getExpLevelCost());
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, Skill.ENCHANTING);
        skillManager.addXP(id, Skill.ENCHANTING, amount);
        int after = skillManager.getLevel(id, Skill.ENCHANTING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, Skill.ENCHANTING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eEnchanting §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
