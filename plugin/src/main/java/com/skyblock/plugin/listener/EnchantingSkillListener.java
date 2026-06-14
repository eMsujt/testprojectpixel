package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.UUID;

/**
 * Awards Enchanting XP through {@link SkillManager} when a player enchants an
 * item and fires level-up rewards when the player's level increases.
 */
public final class EnchantingSkillListener implements Listener {

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player enchanter = event.getEnchanter();
        long xp = Math.max(1L, event.getExpLevelCost());
        UUID id = enchanter.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.ENCHANTING);
        skillManager.addXP(id, SkillType.ENCHANTING, xp);
        int after = skillManager.getLevel(id, SkillType.ENCHANTING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.ENCHANTING, before, after);
            enchanter.sendTitle("§aSkill Level Up!", "§eEnchanting §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
