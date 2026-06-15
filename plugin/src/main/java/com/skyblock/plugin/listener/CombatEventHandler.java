package com.skyblock.plugin.listener;

import com.skyblock.plugin.combat.calculator.DamageCalculator;
import com.skyblock.plugin.profile.ProfileManager;
import com.skyblock.plugin.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Overrides vanilla melee damage with the SkyBlock formula and awards Combat XP
 * to the attacking player on each hit.
 */
public final class CombatEventHandler implements Listener {

    private static final double DEFAULT_CRIT_CHANCE  = 20.0;
    private static final double DEFAULT_CRIT_DAMAGE  = 50.0;
    private static final long   COMBAT_XP_PER_HIT    = 4L;

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());

        double weaponDamage = 0.0;
        double strength     = 0.0;
        double critChance   = DEFAULT_CRIT_CHANCE;
        double critDamage   = DEFAULT_CRIT_DAMAGE;

        double damage = DamageCalculator.computeDamage(weaponDamage, strength, critChance, critDamage);
        event.setDamage(damage);

        profile.addSkillXp("combat", COMBAT_XP_PER_HIT);
        XpActionBar.send(player, "combat", COMBAT_XP_PER_HIT, profile.getSkillXp("combat"));
    }
}
