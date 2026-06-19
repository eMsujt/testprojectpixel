package com.skyblock.core.listener;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Awards {@link Skill#COMBAT} XP to the player credited with a mob kill via the
 * canonical {@link SkillManager}.
 */
public final class CombatListener implements Listener {

    private final SkillManager skillManager;

    public CombatListener(SkillManager skillManager) {
        if (skillManager == null) {
            throw new IllegalArgumentException("skillManager must not be null");
        }
        this.skillManager = skillManager;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        skillManager.addXP(killer.getUniqueId(), Skill.COMBAT, 1L);
    }
}
