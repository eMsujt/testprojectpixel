package com.skyblock.plugin.items;

/**
 * Stat types used by the custom item framework.
 *
 * <p>Each constant carries its human-readable display name, the symbol shown
 * next to it in item lore, and the base value every player starts with before
 * any item, skill or pet bonuses are applied.</p>
 */
public enum StatType {

    HEALTH("Health", "❤", 100.0),
    DEFENSE("Defense", "❈", 0.0),
    STRENGTH("Strength", "❁", 0.0),
    CRIT_CHANCE("Crit Chance", "☣", 30.0),
    CRIT_DAMAGE("Crit Damage", "☠", 50.0),
    SPEED("Speed", "✦", 100.0),
    INTELLIGENCE("Intelligence", "✎", 0.0),
    ATTACK_SPEED("Bonus Attack Speed", "⚔", 0.0);

    private final String displayName;
    private final String symbol;
    private final double baseValue;

    StatType(String displayName, String symbol, double baseValue) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.baseValue = baseValue;
    }

    /** Returns the human-readable name of this stat, e.g. {@code "Crit Chance"}. */
    public String getDisplayName() {
        return displayName;
    }

    /** Returns the symbol shown next to this stat in item lore and menus. */
    public String getSymbol() {
        return symbol;
    }

    /** Returns the base value every player starts with for this stat. */
    public double getBaseValue() {
        return baseValue;
    }
}
