package com.skyblock.plugin.skill;

import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Detects skill level-ups and grants the Hypixel-accurate stat bonuses via
 * {@link SkillRewardManager}. Snapshots each player's skill levels at
 * {@link EventPriority#LOWEST} (before any XP listener runs) and compares at
 * {@link EventPriority#MONITOR} (after all XP has been added).
 */
public final class SkillLevelUpRewardHandler implements Listener {

    private final SkillsManager skillsManager = SkillsManager.getInstance();
    private final SkillRewardManager rewardManager = SkillRewardManager.getInstance();

    private final Map<UUID, Map<String, Integer>> snapshots = new HashMap<>();

    // --- BlockBreakEvent (farming / mining / foraging) ---

    @EventHandler(priority = EventPriority.LOWEST)
    public void snapshotBlockBreak(BlockBreakEvent event) {
        snapshot(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkBlockBreak(BlockBreakEvent event) {
        checkAndGrant(event.getPlayer());
    }

    // --- EntityDeathEvent (combat) ---

    @EventHandler(priority = EventPriority.LOWEST)
    public void snapshotEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            snapshot(killer);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            checkAndGrant(killer);
        }
    }

    // --- PlayerFishEvent (fishing) ---

    @EventHandler(priority = EventPriority.LOWEST)
    public void snapshotPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            snapshot(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            checkAndGrant(event.getPlayer());
        }
    }

    // --- EnchantItemEvent (enchanting) ---

    @EventHandler(priority = EventPriority.LOWEST)
    public void snapshotEnchantItem(EnchantItemEvent event) {
        snapshot(event.getEnchanter());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkEnchantItem(EnchantItemEvent event) {
        checkAndGrant(event.getEnchanter());
    }

    private void snapshot(Player player) {
        UUID uuid = player.getUniqueId();
        Map<String, Integer> levels = new HashMap<>();
        for (String skill : SkillsManager.SKILL_XP_TABLE.keySet()) {
            levels.put(skill, skillsManager.getSkillLevel(uuid, skill));
        }
        snapshots.put(uuid, levels);
    }

    private void checkAndGrant(Player player) {
        UUID uuid = player.getUniqueId();
        Map<String, Integer> before = snapshots.remove(uuid);
        if (before == null) {
            return;
        }
        for (String skill : SkillsManager.SKILL_XP_TABLE.keySet()) {
            int oldLevel = before.getOrDefault(skill, 0);
            int newLevel = skillsManager.getSkillLevel(uuid, skill);
            if (newLevel > oldLevel) {
                rewardManager.grantLevelUpRewards(player, skill, oldLevel, newLevel);
            }
        }
    }
}
