package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Awards Alchemy XP directly to the player's {@link SkyBlockProfile} whenever
 * a brewing stand finishes a brew cycle (22.5 XP per filled output slot).
 */
public final class AlchemyXpListener implements Listener {

    private static final long XP_PER_SLOT = 22L;
    private static final double CREDIT_RADIUS = 8.0;

    @EventHandler
    public void onBrew(BrewEvent event) {
        int filledSlots = 0;
        for (ItemStack result : event.getResults()) {
            if (result != null && !result.getType().isAir()) {
                filledSlots++;
            }
        }
        if (filledSlots == 0) return;

        Player player = nearestPlayer(event);
        if (player == null) return;

        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("alchemy", filledSlots * XP_PER_SLOT);
    }

    private Player nearestPlayer(BrewEvent event) {
        Location origin = event.getBlock().getLocation();
        Player closest = null;
        double best = Double.MAX_VALUE;
        for (Player player : event.getBlock().getWorld().getNearbyPlayers(origin, CREDIT_RADIUS)) {
            double distance = player.getLocation().distanceSquared(origin);
            if (distance < best) {
                best = distance;
                closest = player;
            }
        }
        return closest;
    }
}
