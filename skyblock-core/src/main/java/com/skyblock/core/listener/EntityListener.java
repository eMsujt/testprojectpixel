package com.skyblock.core.listener;

import com.skyblock.core.manager.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

public final class EntityListener implements Listener {

    private static final EntityListener INSTANCE = new EntityListener();

    private EntityListener() {}

    public static EntityListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        UUID uuid = killer.getUniqueId();
        CombatManager cm = CombatManager.getInstance();
        String typeName = event.getEntity().getType().name();
        CombatManager.Monster monster = resolveMonster(typeName);
        if (monster != null) {
            cm.recordKill(uuid, monster);
        } else {
            cm.addXp(uuid, CombatManager.XP_PER_KILL);
        }
    }

    private static CombatManager.Monster resolveMonster(String entityTypeName) {
        for (CombatManager.Monster m : CombatManager.Monster.values()) {
            if (m.name().equalsIgnoreCase(entityTypeName)) {
                return m;
            }
        }
        return null;
    }
}
