package com.skyblock.plugin.listener;

import com.skyblock.plugin.combat.calculator.HypixelDamageCalculator;
import com.skyblock.plugin.manager.ProfileManager;
import com.skyblock.plugin.profile.SkyBlockProfile;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Awards Combat Skill XP to the attacker's {@link SkyBlockProfile} on every player
 * hit, and replaces Minecraft's raw damage with the SkyBlock value from
 * {@link HypixelDamageCalculator}.
 */
public final class CombatEntityListener implements Listener {

    /** Hypixel awards roughly 4 Combat XP per point of final damage dealt. */
    private static final double XP_PER_DAMAGE = 4.0;

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }
        Entity victim = event.getEntity();
        if (!(victim instanceof LivingEntity)) {
            return;
        }

        Player player = (Player) damager;
        SkyBlockProfile profile = ProfileManager.getInstance().getOrCreateProfile(player.getUniqueId());

        double damage = HypixelDamageCalculator.calculate(profile, player.getInventory().getItemInMainHand(), (LivingEntity) victim);
        event.setDamage(damage);

        long xp = Math.max(1L, (long) (damage * XP_PER_DAMAGE));
        profile.addSkillXp("combat", xp);
        XpActionBar.send(player, "combat", xp, profile.getSkillXp("combat"));
    }
}
