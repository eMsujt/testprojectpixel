package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import java.util.UUID;

public final class TamingListener implements Listener {

    private static final long TAME_XP = 500L;

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onEntityTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player)) {
            return;
        }
        grantXP((Player) event.getOwner(), TAME_XP);
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.TAMING);
        skillManager.addXP(id, SkillType.TAMING, amount);
        int after = skillManager.getLevel(id, SkillType.TAMING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.TAMING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eTaming §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
