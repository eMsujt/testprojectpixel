package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityTameEvent;

/**
 * Awards Taming XP on the player's {@link SkyBlockProfile} whenever a player
 * tames an entity (50 XP) or breeds two entities (100 XP).
 */
public final class TamingXpListener implements Listener {

    private static final long TAME_XP  = 50L;
    private static final long BREED_XP = 100L;

    @EventHandler
    public void onEntityTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player player)) {
            return;
        }
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("taming", TAME_XP);
        XpActionBar.send(player, "taming", TAME_XP, profile.getSkillXp("taming"));
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player player)) {
            return;
        }
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("taming", BREED_XP);
        XpActionBar.send(player, "taming", BREED_XP, profile.getSkillXp("taming"));
    }
}
