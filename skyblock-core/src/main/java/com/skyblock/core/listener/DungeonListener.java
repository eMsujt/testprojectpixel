package com.skyblock.core.listener;

import com.skyblock.core.manager.DungeonsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Awards dungeon class XP whenever a player slays a mob inside a dungeon world.
 */
public final class DungeonListener implements Listener {

    private static final DungeonListener INSTANCE = new DungeonListener();

    private final DungeonsManager dungeonsManager = DungeonsManager.getInstance();

    private DungeonListener() {}

    public static DungeonListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        if (!isDungeonWorld(killer)) return;

        dungeonsManager.recordMob(killer.getUniqueId());
    }

    private static boolean isDungeonWorld(Player player) {
        return player.getWorld().getName().toLowerCase().contains("dungeon");
    }
}
