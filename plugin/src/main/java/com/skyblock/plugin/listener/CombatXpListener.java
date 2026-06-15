package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;

public final class CombatXpListener implements Listener {

    private static final Map<EntityType, Long> MOB_XP = Map.ofEntries(
            Map.entry(EntityType.ZOMBIE,          5L),
            Map.entry(EntityType.SKELETON,        5L),
            Map.entry(EntityType.SPIDER,          5L),
            Map.entry(EntityType.CAVE_SPIDER,     4L),
            Map.entry(EntityType.CREEPER,         5L),
            Map.entry(EntityType.SLIME,           3L),
            Map.entry(EntityType.WITCH,          10L),
            Map.entry(EntityType.ENDERMAN,       10L),
            Map.entry(EntityType.BLAZE,          10L),
            Map.entry(EntityType.ZOMBIFIED_PIGLIN, 8L),
            Map.entry(EntityType.PIGLIN_BRUTE,    8L),
            Map.entry(EntityType.GHAST,          12L),
            Map.entry(EntityType.MAGMA_CUBE,      6L)
    );

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) {
            return;
        }
        Long xp = MOB_XP.get(victim.getType());
        if (xp == null) {
            return;
        }
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(killer.getUniqueId());
        profile.addSkillXp("combat", xp);
    }
}
