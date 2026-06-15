package com.skyblock.plugin.listener;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.skills.SkillManager.SkillType;
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
        int before = skillManager.getLevel(id, SkillType.ENCHANTING);
        skillManager.addXP(id, SkillType.ENCHANTING, amount);
        int after = skillManager.getLevel(id, SkillType.ENCHANTING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.ENCHANTING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eEnchanting §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
