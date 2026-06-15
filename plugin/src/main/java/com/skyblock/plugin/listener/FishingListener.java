package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Set;

/**
 * Awards Fishing XP and increments fish collections on the player's
 * {@link SkyBlockProfile} whenever a player reels in a catch.
 */
public final class FishingListener implements Listener {

    private static final Set<Material> COMMON_FISH = Set.of(
            Material.COD,
            Material.SALMON,
            Material.TROPICAL_FISH,
            Material.PUFFERFISH
    );

    private static final long COMMON_XP  = 5L;
    private static final long TREASURE_XP = 20L;

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        if (!(event.getCaught() instanceof Item caught)) {
            return;
        }
        Player player = event.getPlayer();
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        Material type = caught.getItemStack().getType();
        long xp = COMMON_FISH.contains(type) ? COMMON_XP : TREASURE_XP;
        profile.addSkillXp("fishing", xp);
        profile.incrementCollection(type.name().toLowerCase());
    }
}
