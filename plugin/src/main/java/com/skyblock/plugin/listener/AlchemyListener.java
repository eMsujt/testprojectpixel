package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
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
 * finishes brewing, granting XP for each potion slot produced and firing
 * level-up rewards when the nearest player's level increases.
 */
public final class AlchemyListener implements Listener {

    /** Alchemy XP granted per brewed potion slot. */
    private static final long POTION_XP = 6L;

    /** Maximum distance (blocks) to credit the nearest player for a brew. */
    private static final double CREDIT_RADIUS = 16.0D;

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onBrew(BrewEvent event) {
        long xp = 0L;
        for (ItemStack result : event.getResults()) {
            if (result != null && !result.getType().isAir()) {
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
        int before = skillManager.getLevel(id, SkillType.ALCHEMY);
        skillManager.addXP(id, SkillType.ALCHEMY, amount);
        int after = skillManager.getLevel(id, SkillType.ALCHEMY);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.ALCHEMY, before, after);
            player.sendTitle("§aSkill Level Up!", "§eAlchemy §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
