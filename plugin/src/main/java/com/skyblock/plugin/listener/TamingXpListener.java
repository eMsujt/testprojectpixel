package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import java.util.Set;

/**
 * Awards Taming XP on the player's {@link SkyBlockProfile} whenever a player
 * successfully tames a wolf or cat (500 XP per tame).
 */
public final class TamingXpListener implements Listener {

    private static final long TAME_XP = 500L;

    private static final Set<EntityType> TAMEABLE = Set.of(
            EntityType.WOLF,
            EntityType.CAT
    );

    @EventHandler
    public void onEntityTame(EntityTameEvent event) {
        if (!TAMEABLE.contains(event.getEntityType())) return;
        if (!(event.getOwner() instanceof Player player)) return;
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());
        profile.addSkillXp("taming", TAME_XP);
        XpActionBar.send(player, "taming", TAME_XP, profile.getSkillXp("taming"));
    }
}
