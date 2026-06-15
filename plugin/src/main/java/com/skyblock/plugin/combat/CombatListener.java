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
        double weaponDamage = event.getDamage();
        double strength = statManager.getStat(uuid, StatManager.CombatStat.STRENGTH);
        double baseDamage = 5 + weaponDamage;
        double damage = baseDamage * (1 + strength / 100.0);
        event.setDamage(damage);
    }
}
