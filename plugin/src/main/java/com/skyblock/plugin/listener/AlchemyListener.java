package com.skyblock.plugin.listener;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Awards Alchemy XP through {@link SkillManager} whenever a brewing stand
 * finishes brewing, granting XP for each active ingredient slot (0–2) and
 * firing level-up rewards when the nearest player's level increases.
 */
public final class AlchemyListener implements Listener {

    /** Alchemy XP granted per active ingredient slot. */
    private static final long POTION_XP = 6L;

    /** Maximum distance (blocks) to credit the nearest player for a brew. */
    private static final double CREDIT_RADIUS = 16.0D;

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onBrew(BrewEvent event) {
        ItemStack[] contents = event.getContents().getContents();
        long xp = 0L;
        for (int i = 0; i <= 2; i++) {
            ItemStack slot = contents[i];
            if (slot != null && !slot.getType().isAir()) {
                xp += POTION_XP;
            }
        }
        if (xp <= 0L) {
            return;
        }
        Player player = nearestPlayer(event.getBlock());
        if (player != null) {
            grantXP(player, xp);
        }
    }

    private Player nearestPlayer(Block block) {
        Location origin = block.getLocation();
        Player closest = null;
        double best = Double.MAX_VALUE;
        for (Player player : block.getWorld().getNearbyPlayers(origin, CREDIT_RADIUS)) {
            double distance = player.getLocation().distanceSquared(origin);
            if (distance < best) {
                best = distance;
                closest = player;
            }
        }
        return closest;
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, Skill.ALCHEMY);
        skillManager.addXP(id, Skill.ALCHEMY, amount);
        int after = skillManager.getLevel(id, Skill.ALCHEMY);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, Skill.ALCHEMY, before, after);
            player.sendTitle("§aSkill Level Up!", "§eAlchemy §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
