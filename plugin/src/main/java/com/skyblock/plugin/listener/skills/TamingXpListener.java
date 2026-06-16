package com.skyblock.plugin.listener.skills;

import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.plugin.profile.PlayerProfile;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import java.util.Map;

/**
 * Awards Taming XP on the player's {@link PlayerProfile} whenever a player
 * successfully tames an animal.
 */
public final class TamingXpListener implements Listener {

    private static final Map<EntityType, Long> TAME_XP = Map.of(
            EntityType.WOLF, 10L
    );

    @EventHandler
    public void onEntityTame(EntityTameEvent event) {
        Long xp = TAME_XP.get(event.getEntityType());
        if (xp == null) return;
        if (!(event.getOwner() instanceof Player player)) return;
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.addSkillXp("taming", xp);
        XpActionBar.send(player, "taming", xp, profile.getSkillXp("taming"));
    }
}
