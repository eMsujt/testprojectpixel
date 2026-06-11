package com.skyblock.combat;

/**
 * Combat-related stats a SkyBlock player can have.
 *
 * <p>Each stat carries its human-readable display name, the symbol shown
 * next to it in item lore and the stat menu, and the base value every
 * player starts with before any item, skill or pet bonuses.</p>
 */
public enum CombatStat {

    HEALTH("Health", "❤", 100.0),
    DEFENSE("Defense", "❈", 0.0),
    STRENGTH("Strength", "❁", 0.0),
    SPEED("Speed", "✦", 100.0),
    CRIT_CHANCE("Crit Chance", "☣", 30.0),
    CRIT_DAMAGE("Crit Damage", "☠", 50.0),
    INTELLIGENCE("Intelligence", "✎", 0.0),
    ATTACK_SPEED("Bonus Attack Speed", "⚔", 0.0),
    FEROCITY("Ferocity", "⫽", 0.0);

    private final String displayName;
    private final String symbol;
    private final double baseValue;

    CombatStat(String displayName, String symbol, double baseValue) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.baseValue = baseValue;
    }

    /**
     * Returns the human-readable name of this stat.
     *
     * @return the display name, e.g. {@code "Crit Chance"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the symbol shown next to this stat in item lore and menus.
     *
     * @return the stat symbol, e.g. {@code "❤"} for health
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the value every player starts with for this stat before
     * any item, skill or pet bonuses are applied.
     *
     * @return the base value, never negative
     */
    public double getBaseValue() {
        return baseValue;
    }
}
