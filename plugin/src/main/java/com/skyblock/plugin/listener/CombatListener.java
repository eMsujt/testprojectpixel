package com.skyblock.plugin.listener;

import com.skyblock.core.model.Stat;
import com.skyblock.core.stat.StatManager;
import com.skyblock.plugin.combat.calculator.CombatDamageCalculator;
import com.skyblock.core.economy.manager.EconomyManager;
import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.plugin.profile.PlayerProfile;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

/**
 * Bukkit listener that intercepts {@link EntityDamageByEntityEvent} and, when the
 * damager is a {@link Player}, loads their {@link PlayerProfile}, replaces
 * Minecraft's raw damage with the SkyBlock value from {@link CombatDamageCalculator},
 * applies the defender's defense reduction, and awards Combat XP to the profile.
 *
 * <p>Hypixel defense formula: {@code effective = damage × (1 - defense / (defense + 100))},
 * followed by a flat reduction of {@code trueDefense}.</p>
 */
public final class CombatListener implements Listener {

    private static final double XP_PER_DAMAGE = 4.0;

    private final StatManager statManager = StatManager.getInstance();
    private final EconomyManager economy = EconomyManager.getInstance();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }

        Player player = (Player) damager;
        UUID attackerId = player.getUniqueId();
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(attackerId);

        double weaponDamage = event.getDamage();
        double strength     = statManager.getStat(attackerId, Stat.STRENGTH);
        double critDamage   = statManager.getStat(attackerId, Stat.CRIT_DAMAGE);

        double damage = CombatDamageCalculator.calculateDamage(weaponDamage, strength, critDamage);

        Entity victim = event.getEntity();
        if (victim instanceof Player) {
            UUID defenderId = victim.getUniqueId();
            double defense     = statManager.getStat(defenderId, Stat.DEFENSE);
            double trueDefense = statManager.getStat(defenderId, Stat.TRUE_DEFENSE);
            damage *= (1.0 - defense / (defense + 100.0));
            damage = Math.max(0.0, damage - trueDefense);
        }

        event.setDamage(damage);

        long xp = Math.max(1L, (long) (damage * XP_PER_DAMAGE));
        profile.addSkillXp("combat", xp);
        XpActionBar.send(player, "combat", xp, profile.getSkillXp("combat"));
    }

    /**
     * Rewards the killer with combat coins when they slay a mob. The drop scales
     * with the victim's max health so tougher mobs pay out more.
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) {
            return;
        }
        long coins = Math.max(1L, Math.round(victim.getMaxHealth()));
        economy.addPurse(killer.getUniqueId(), coins);
    }
}
