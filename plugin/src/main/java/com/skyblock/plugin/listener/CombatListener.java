package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ProjectileSource;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

/**
 * Awards Combat XP through {@link SkillManager} when a player damages a living
 * entity and fires level-up rewards when the player's level increases.
 */
public final class CombatListener implements Listener {

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        Player attacker = resolveAttacker(event);
        if (attacker == null) {
            return;
        }
        long xp = Math.max(1L, Math.round(event.getFinalDamage()));
        UUID id = attacker.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.COMBAT);
        skillManager.addXP(id, SkillType.COMBAT, xp);
        int after = skillManager.getLevel(id, SkillType.COMBAT);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.COMBAT, before, after);
            attacker.sendTitle("§aSkill Level Up!", "§eCombat §a→ §eLVL " + after, 10, 60, 20);
        }
    }

    private Player resolveAttacker(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        }
        if (event.getDamager() instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        return null;
    }
}
