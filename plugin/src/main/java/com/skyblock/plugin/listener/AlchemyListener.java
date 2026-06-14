package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.UUID;

/**
 * Awards Alchemy XP through {@link SkillManager} whenever a player drinks a
 * potion and fires level-up rewards when the player's level increases.
 */
public final class AlchemyListener implements Listener {

    /** Alchemy XP granted per potion consumed. */
    private static final long POTION_XP = 6L;

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Material type = event.getItem().getType();
        if (type != Material.POTION && type != Material.SPLASH_POTION && type != Material.LINGERING_POTION) {
            return;
        }
        grantXP(event.getPlayer(), POTION_XP);
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.ALCHEMY);
        skillManager.addXP(id, SkillType.ALCHEMY, amount);
        int after = skillManager.getLevel(id, SkillType.ALCHEMY);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.ALCHEMY, before, after);
            player.sendTitle("§aSkill Level Up!", "§eAlchemy §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
