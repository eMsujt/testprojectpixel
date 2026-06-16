package com.skyblock.plugin.listener.skills;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.plugin.profile.PlayerProfile;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;

/**
 * Awards Fishing XP directly to the player's {@link PlayerProfile} whenever
 * a fish item is caught ({@link PlayerFishEvent.State#CAUGHT_FISH}) or a sea
 * creature is reeled in ({@link PlayerFishEvent.State#CAUGHT_ENTITY}).
 */
public final class FishingXpListener implements Listener {

    private static final long CAUGHT_FISH_XP = 50L;

    private static final Map<EntityType, Long> SEA_CREATURE_XP = Map.ofEntries(
            Map.entry(EntityType.SQUID,          8L),
            Map.entry(EntityType.GUARDIAN,       10L),
            Map.entry(EntityType.ELDER_GUARDIAN, 50L),
            Map.entry(EntityType.ZOMBIE,          5L),
            Map.entry(EntityType.DROWNED,         5L),
            Map.entry(EntityType.SKELETON,        8L),
            Map.entry(EntityType.WITCH,          18L),
            Map.entry(EntityType.IRON_GOLEM,    120L)
    );

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Entity caught = event.getCaught();

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (!(caught instanceof Item item)) return;
            Material type = item.getItemStack().getType();
            PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
            profile.addSkillXp("fishing", CAUGHT_FISH_XP);
            XpActionBar.send(player, "fishing", CAUGHT_FISH_XP, profile.getSkillXp("fishing"));
            CollectionManager.getInstance().addItems(player.getUniqueId(), type.name(), 1);

        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && caught != null) {
            Long xp = SEA_CREATURE_XP.get(caught.getType());
            if (xp == null) return;
            PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
            profile.addSkillXp("fishing", xp);
            XpActionBar.send(player, "fishing", xp, profile.getSkillXp("fishing"));
        }
    }
}
