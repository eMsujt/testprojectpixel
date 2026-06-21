package com.skyblock.core.listener;

import com.skyblock.core.manager.CombatManager;
import com.skyblock.core.manager.EconomyManager;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Monster;
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
        dropCoins(event, killer);
    }

    /** Hostile mobs drop coins to the killer's purse, scaled to the mob's max health. */
    private static void dropCoins(EntityDeathEvent event, Player killer) {
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }
        double maxHealth = 20.0;
        AttributeInstance attr = event.getEntity().getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            maxHealth = attr.getValue();
        }
        long coins = Math.max(1L, Math.round(maxHealth * 0.5));
        EconomyManager.getInstance().addCoins(killer.getUniqueId(), coins);
        killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.5f);
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
