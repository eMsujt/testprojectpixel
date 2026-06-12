package com.skyblock.core.stats;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Bukkit listener that records combat statistics into {@link CombatStatsManager}:
 * kills and deaths from {@link PlayerDeathEvent}, and damage dealt/taken from
 * {@link EntityDamageByEntityEvent}.
 */
public final class CombatStatsListener implements Listener {

    private final CombatStatsManager combatStatsManager;

    /**
     * Creates a listener backed by the given {@link CombatStatsManager}.
     *
     * @param combatStatsManager the stats manager, must not be null
     * @throws IllegalArgumentException if {@code combatStatsManager} is null
     */
    public CombatStatsListener(CombatStatsManager combatStatsManager) {
        if (combatStatsManager == null) {
            throw new IllegalArgumentException("combatStatsManager must not be null");
        }
        this.combatStatsManager = combatStatsManager;
    }

    /**
     * Records a death for the dying player, and a kill for the killer if the
     * killer is also a player.
     *
     * @param event the player-death event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        combatStatsManager.recordDeath(victim.getUniqueId());

        Player killer = victim.getKiller();
        if (killer != null) {
            combatStatsManager.recordKill(killer.getUniqueId());
        }
    }

    /**
     * Records damage dealt and damage taken when a player is involved in combat.
     *
     * @param event the entity-damage event
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        double damage = event.getFinalDamage();

        Entity attacker = event.getDamager();
        if (attacker instanceof Player) {
            combatStatsManager.addDamageDealt(attacker.getUniqueId(), damage);
        }

        Entity defender = event.getEntity();
        if (defender instanceof Player) {
            combatStatsManager.addDamageTaken(defender.getUniqueId(), damage);
        }
    }
}
