package com.skyblock.plugin.listener;

import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;

/**
 * Awards Combat XP on the player's {@link SkyBlockProfile} whenever a player
 * kills a mob mapped in {@code MOB_XP}.
 */
public final class CombatXpListener implements Listener {

    private static final Map<EntityType, String> MOB_DROP = Map.ofEntries(
            Map.entry(EntityType.ZOMBIE,          "ROTTEN_FLESH"),
            Map.entry(EntityType.SKELETON,        "BONE"),
            Map.entry(EntityType.CREEPER,         "GUNPOWDER"),
            Map.entry(EntityType.SPIDER,          "STRING"),
            Map.entry(EntityType.CAVE_SPIDER,     "STRING"),
            Map.entry(EntityType.ENDERMAN,        "ENDER_PEARL"),
            Map.entry(EntityType.BLAZE,           "BLAZE_ROD"),
            Map.entry(EntityType.GHAST,           "GHAST_TEAR"),
            Map.entry(EntityType.WITCH,           "GLASS_BOTTLE"),
            Map.entry(EntityType.SLIME,           "SLIMEBALL"),
            Map.entry(EntityType.MAGMA_CUBE,      "MAGMA_CREAM"),
            Map.entry(EntityType.WITHER_SKELETON, "WITHER_SKELETON_SKULL")
    );

    private static final Map<EntityType, Long> MOB_XP = Map.ofEntries(
            Map.entry(EntityType.ZOMBIE,          5L),
            Map.entry(EntityType.SKELETON,        5L),
            Map.entry(EntityType.CREEPER,         6L),
            Map.entry(EntityType.SPIDER,          4L),
            Map.entry(EntityType.CAVE_SPIDER,     5L),
            Map.entry(EntityType.ENDERMAN,       12L),
            Map.entry(EntityType.BLAZE,          10L),
            Map.entry(EntityType.GHAST,          15L),
            Map.entry(EntityType.WITCH,          10L),
            Map.entry(EntityType.SLIME,           3L),
            Map.entry(EntityType.MAGMA_CUBE,      5L),
            Map.entry(EntityType.WITHER_SKELETON,15L)
    );

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        Long xp = MOB_XP.get(event.getEntityType());
        if (xp == null) {
            return;
        }
        SkyBlockProfile profile = ProfileManager.getInstance()
                .getOrCreateProfile(killer.getUniqueId());
        profile.addSkillXp("combat", xp);
        String drop = MOB_DROP.get(event.getEntityType());
        if (drop == null) return;
        profile.incrementCollection(drop, 1);
    }
}
