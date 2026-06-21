package com.skyblock.core.listener;

import com.skyblock.core.ability.AbilityEffects;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Applies ability-projectile damage on impact. Projectiles launched by an ability carry their
 * damage in the {@link AbilityEffects#projectileDamageKey()} PDC; on hit they burst for AoE damage
 * to nearby monsters and are removed. Non-ability projectiles are ignored.
 */
public final class ProjectileAbilityListener implements Listener {

    private static final ProjectileAbilityListener INSTANCE = new ProjectileAbilityListener();

    private ProjectileAbilityListener() {}

    public static ProjectileAbilityListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Double damage = projectile.getPersistentDataContainer()
                .get(AbilityEffects.projectileDamageKey(), PersistentDataType.DOUBLE);
        if (damage == null) {
            return;
        }
        ProjectileSource source = projectile.getShooter();
        Player shooter = source instanceof Player p ? p : null;
        Location loc = projectile.getLocation();
        double radius = 3.0;
        for (Entity entity : projectile.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
            if (entity instanceof Monster monster) {
                if (shooter != null) {
                    monster.damage(damage, shooter);
                } else {
                    monster.damage(damage);
                }
            }
        }
        projectile.getWorld().spawnParticle(Particle.EXPLOSION, loc, 2, 0.5, 0.5, 0.5, 0.0);
        projectile.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.3f);
        projectile.remove();
    }
}
