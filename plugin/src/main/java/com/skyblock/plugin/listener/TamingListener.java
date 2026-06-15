package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.core.skills.SkillManager.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

public final class TamingListener implements Listener {

    private static final long KILL_XP = 10L;

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Wolf wolf)) {
            return;
        }
        AnimalTamer owner = wolf.getOwner();
        if (owner == null) {
            return;
        }
        Player player = Bukkit.getPlayer(owner.getUniqueId());
        if (player == null) {
            return;
        }
        grantXP(player, KILL_XP);
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
