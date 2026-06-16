package com.skyblock.plugin.listener;

import com.skyblock.core.skills.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.UUID;

/**
 * Awards Enchanting XP through {@link SkillManager} whenever a player enchants
 * an item and fires level-up rewards when the player's level increases.
 */
public final class EnchantingListener implements Listener {

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        int xp = event.getExp();
        if (xp <= 0) {
            return;
        }
        grantXP(event.getEnchanter(), xp);
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
