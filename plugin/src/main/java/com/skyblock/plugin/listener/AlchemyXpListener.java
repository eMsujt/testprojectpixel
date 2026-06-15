package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Awards Alchemy XP on the player's {@link SkyBlockProfile} whenever a brewing
 * stand finishes a brew. XP is granted per filled output slot to the nearest
 * player within 16 blocks of the stand.
 */
public final class AlchemyXpListener implements Listener {

    private static final long POTION_XP = 35L;
    private static final double CREDIT_RADIUS = 16.0D;

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
        if (player == null) {
            return;
        }
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("alchemy", xp);
    }

    private static Player nearestPlayer(Block block) {
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
}
