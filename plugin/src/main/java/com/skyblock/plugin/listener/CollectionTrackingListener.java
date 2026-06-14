package com.skyblock.plugin.listener;

import com.skyblock.plugin.collections.CollectionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.EnumMap;
import java.util.Map;

/**
 * Tracks player collections from gathering blocks via {@link BlockBreakEvent}
 * and slaying mobs via {@link EntityDeathEvent}, announcing any tiers unlocked.
 */
public final class CollectionTrackingListener implements Listener {

    /** Maps a slain mob to the collection material it contributes to. */
    private static final Map<EntityType, Material> MOB_COLLECTION = new EnumMap<>(EntityType.class);

    static {
        MOB_COLLECTION.put(EntityType.ZOMBIE,   Material.ROTTEN_FLESH);
        MOB_COLLECTION.put(EntityType.SKELETON, Material.BONE);
        MOB_COLLECTION.put(EntityType.SPIDER,   Material.STRING);
        MOB_COLLECTION.put(EntityType.CREEPER,  Material.GUNPOWDER);
        MOB_COLLECTION.put(EntityType.ENDERMAN, Material.ENDER_PEARL);
        MOB_COLLECTION.put(EntityType.SLIME,    Material.SLIME_BALL);
        MOB_COLLECTION.put(EntityType.BLAZE,    Material.BLAZE_ROD);
    }

    private final CollectionManager collectionManager = CollectionManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        award(event.getPlayer(), event.getBlock().getType());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        Material material = MOB_COLLECTION.get(event.getEntityType());
        if (material == null) {
            return;
        }
        award(killer, material);
    }

    private void award(Player player, Material material) {
        int unlocked = collectionManager.addCollection(player.getUniqueId(), material, 1L);
        if (unlocked > 0) {
            int tier = collectionManager.getTier(player.getUniqueId(), material);
            player.sendMessage("§a§lCOLLECTION UNLOCKED §7" + material.name().toLowerCase() + " §eTier " + tier);
        }
    }
}
