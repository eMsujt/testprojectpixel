package com.skyblock.core.util;

/**
 * The plugin's own health model, like Hypixel's: SkyBlock max-health can be
 * thousands (and mob health far more), which overflows the vanilla {@code
 * MAX_HEALTH} cap (~1024). So every living thing's vanilla health bar is pinned
 * to a fixed {@link #DISPLAY_MAX}, and the real SkyBlock health is scaled onto
 * it — damage is converted to the bar's terms, and the bar's value is converted
 * back to real HP for display. A 100-HP entity maps 1:1 (factor 1), so low-HP
 * behaviour is unchanged; high HP simply scales down to fit the bar.
 */
public final class HealthScale {

    /** The fixed vanilla max-health every entity's bar is pinned to. */
    public static final double DISPLAY_MAX = 100.0;

    private HealthScale() {
    }

    /**
     * Converts {@code skyblockDamage} (in real SkyBlock HP) into the vanilla
     * damage to apply on a {@link #DISPLAY_MAX} bar, for an entity whose real max
     * health is {@code skyblockMaxHealth}.
     */
    public static double toVanilla(double skyblockDamage, double skyblockMaxHealth) {
        if (skyblockMaxHealth <= 0.0) {
            return skyblockDamage;
        }
        return skyblockDamage * DISPLAY_MAX / skyblockMaxHealth;
    }

    /**
     * Converts a vanilla health value (0..{@link #DISPLAY_MAX}) back into real
     * SkyBlock HP for the entity whose real max health is {@code skyblockMaxHealth}.
     */
    public static double toReal(double vanillaHealth, double skyblockMaxHealth) {
        return skyblockMaxHealth * vanillaHealth / DISPLAY_MAX;
    }
}
