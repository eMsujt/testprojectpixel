package com.skyblock.plugin.listener;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.Collection;
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

    private static final Map<EntityType, String> MOB_COLLECTION = new EnumMap<>(EntityType.class);

    static {
        MOB_COLLECTION.put(EntityType.ZOMBIE,   "ROTTEN_FLESH");
        MOB_COLLECTION.put(EntityType.SKELETON, "BONE");
        MOB_COLLECTION.put(EntityType.SPIDER,   "STRING");
        MOB_COLLECTION.put(EntityType.CREEPER,  "GUNPOWDER");
        MOB_COLLECTION.put(EntityType.ENDERMAN, "ENDER_PEARL");
        MOB_COLLECTION.put(EntityType.SLIME,    "SLIME_BALL");
        MOB_COLLECTION.put(EntityType.BLAZE,    "BLAZE_ROD");
    }

    private final CollectionManager cm = CollectionManager.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        award(event.getPlayer(), event.getBlock().getType().name());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        String collection = MOB_COLLECTION.get(event.getEntityType());
        if (collection == null) {
            return;
        }
        award(killer, collection);
    }

    private void award(Player player, String collection) {
        Collection c = Collection.parse(collection);
        int tierBefore = c == null ? 0 : cm.getTier(player.getUniqueId(), c);
        cm.addItems(player.getUniqueId(), collection, 1L);
        int tierAfter = c == null ? 0 : cm.getTier(player.getUniqueId(), c);
        if (tierAfter > tierBefore) {
            player.sendMessage("§a§lCOLLECTION UNLOCKED §7" + collection.toLowerCase() + " §eTier " + tierAfter);
        }
    }
}
