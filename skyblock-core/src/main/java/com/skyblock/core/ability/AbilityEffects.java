package com.skyblock.core.ability;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Locale;

/**
 * Real effects for lore-driven item abilities. Only abilities listed here actually fire; others
 * are recognised but left as no-ops until implemented, so mana is never spent on nothing.
 */
public final class AbilityEffects {

    private AbilityEffects() {}

    /** Whether {@link #run} has a real effect for this ability name. */
    public static boolean isImplemented(String abilityName) {
        if (abilityName == null) return false;
        return switch (abilityName.toLowerCase(Locale.ROOT)) {
            case "instant transmission" -> true;
            default -> false;
        };
    }

    /** Executes the ability's effect on the player. No-op for unimplemented abilities. */
    public static void run(LoreAbility ability, Player player) {
        switch (ability.name.toLowerCase(Locale.ROOT)) {
            case "instant transmission" ->
                    instantTransmission(player, ability.magnitude > 0 ? ability.magnitude : 8);
            default -> { }
        }
    }

    /** Teleport the player up to {@code blocks} ahead along their look direction, then a Speed boost. */
    private static void instantTransmission(Player player, int blocks) {
        Location from = player.getLocation();
        Vector dir = player.getEyeLocation().getDirection().normalize();
        Location dest = from.clone();
        for (int i = 1; i <= blocks; i++) {
            Location step = from.clone().add(dir.clone().multiply(i));
            if (step.getBlock().isPassable() && step.clone().add(0, 1, 0).getBlock().isPassable()) {
                dest = step;
            } else {
                break;
            }
        }
        dest.setYaw(from.getYaw());
        dest.setPitch(from.getPitch());
        player.getWorld().playSound(from, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        player.teleport(dest);
        player.getWorld().playSound(dest, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3 * 20, 1, true, false));
    }
}
