package com.skyblock.plugin.listener;

import com.skyblock.plugin.skills.SkillManager;
import com.skyblock.plugin.skills.SkillManager.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.UUID;

/**
 * Awards Fishing XP through {@link SkillManager} whenever a player reels in a catch.
 */
public final class FishingSkillListener implements Listener {

    private static final long CATCH_XP = 6L;

    private final SkillManager skillManager = SkillManager.getInstance();

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        PlayerFishEvent.State state = event.getState();
        if (state != PlayerFishEvent.State.CAUGHT_FISH
                && state != PlayerFishEvent.State.CAUGHT_ENTITY) {
            return;
        }
        grantXP(event.getPlayer(), CATCH_XP);
    }

    private void grantXP(Player player, long amount) {
        UUID id = player.getUniqueId();
        int before = skillManager.getLevel(id, SkillType.FISHING);
        skillManager.addXP(id, SkillType.FISHING, amount);
        int after = skillManager.getLevel(id, SkillType.FISHING);
        if (after > before) {
            skillManager.grantLevelUpRewards(id, SkillType.FISHING, before, after);
            player.sendTitle("§aSkill Level Up!", "§eFishing §a→ §eLVL " + after, 10, 60, 20);
        }
    }
}
