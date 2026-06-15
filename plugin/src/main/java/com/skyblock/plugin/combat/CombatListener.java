package com.skyblock.plugin.combat;

import com.skyblock.core.combat.StatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public final class CombatListener implements Listener {

    private final StatManager statManager = StatManager.getInstance();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }
        UUID uuid = attacker.getUniqueId();
        int weaponDamage = (int) event.getDamage();
        int strength = (int) statManager.getStat(uuid, StatManager.CombatStat.STRENGTH);
        double baseDamage = 5 + weaponDamage;
        double scaled = baseDamage * (1 + strength / 100.0);
        int critDamage = (int) statManager.getStat(uuid, StatManager.CombatStat.CRIT_DAMAGE);
        double damage = scaled * (1 + critDamage / 100.0);
        event.setDamage(damage);
    }
}
