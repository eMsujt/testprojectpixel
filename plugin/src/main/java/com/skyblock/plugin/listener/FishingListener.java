package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.UUID;

/**
 * Awards Fishing XP through {@link SkillManager} when a player reels in a fish
 * and fires level-up rewards when the player's level increases.
 *
 * <p>Registered as an event listener in
 * {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()}.</p>
 */
public final class FishingListener implements Listener {

    private static final long FISH_XP = 5L;

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.FISHING);
        skillManager.addXP(id, SkillType.FISHING, FISH_XP);
        int after = skillManager.getLevel(id, SkillType.FISHING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.FISHING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eFishing §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
