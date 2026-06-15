package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.core.skills.SkillManager.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

/**
 * Awards Combat XP through {@link SkillManager} when a player kills a living
 * entity and fires level-up rewards when the player's level increases.
 */
public final class CombatMobListener implements Listener {

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        long xp = Math.max(1L, Math.round(event.getEntity().getMaxHealth()));
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
