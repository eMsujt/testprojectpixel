package com.skyblock.plugin.combat;

import com.skyblock.core.combat.StatManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Singleton owning SkyBlock combat resolution. Registers
 * {@link EntityDamageByEntityEvent} and, when the damager is a {@link Player},
 * replaces Minecraft's raw damage with the value from {@link DamageFormula}
 * computed from the attacker's combat stats.
 */
public final class CombatManager implements Listener {

    private static final CombatManager INSTANCE = new CombatManager();

    private final StatManager statManager = StatManager.getInstance();

    private CombatManager() {
    }

    /**
     * Returns the single shared {@code CombatManager} instance.
     *
     * @return the singleton instance
     */
    public static CombatManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers this manager as a Bukkit listener so it begins resolving combat.
     *
     * @param plugin the owning plugin
     */
    public void register(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (!(attacker instanceof Player)) {
            return;
        }

        UUID uuid = attacker.getUniqueId();
        double strength   = statManager.getStat(uuid, StatManager.CombatStat.STRENGTH);
        double critChance = statManager.getStat(uuid, StatManager.CombatStat.CRIT_CHANCE);
        double critDamage = statManager.getStat(uuid, StatManager.CombatStat.CRIT_DAMAGE);

        // The vanilla damage of the swung weapon feeds the formula's weapon-damage term.
        double weaponDamage = event.getDamage();
        boolean crit = ThreadLocalRandom.current().nextDouble() * 100.0 < critChance;
        double damage = DamageFormula.calculate(weaponDamage, strength, crit ? critDamage : 0.0);

        event.setDamage(damage);
    }
}
