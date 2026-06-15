package com.skyblock.core.stat;

/**
 * Every player stat tracked in SkyBlock, with its human-readable display name,
 * the symbol shown in item lore and menus, and the base value a fresh profile starts with.
 *
 * <p>This is the single canonical stat enum; all duplicate stat enums across the
 * project ({@code PlayerStat}, {@code StatType}, {@code CombatStat}) are deprecated
 * stubs that delegate here.</p>
 */
public enum Stat {

    HEALTH("Health", "❤", 100.0),
    DEFENSE("Defense", "❈", 0.0),
    STRENGTH("Strength", "❁", 0.0),
    INTELLIGENCE("Intelligence", "✎", 100.0),
    CRIT_CHANCE("Crit Chance", "☣", 30.0),
    CRIT_DAMAGE("Crit Damage", "☠", 50.0),
    ATTACK_SPEED("Bonus Attack Speed", "⚔", 0.0),
    ABILITY_DAMAGE("Ability Damage", "☄", 0.0),
    TRUE_DEFENSE("True Defense", "❂", 0.0),
    FEROCITY("Ferocity", "⫽", 0.0),
    SPEED("Speed", "✦", 100.0),
    MAGIC_FIND("Magic Find", "✯", 0.0),
    PET_LUCK("Pet Luck", "♣", 0.0),
    SEA_CREATURE_CHANCE("Sea Creature Chance", "α", 20.0),
    FISHING_SPEED("Fishing Speed", "☛", 0.0),
    MINING_SPEED("Mining Speed", "⸕", 0.0),
    MINING_FORTUNE("Mining Fortune", "☘", 0.0),
    FARMING_FORTUNE("Farming Fortune", "☘", 0.0),
    FORAGING_FORTUNE("Foraging Fortune", "☘", 0.0),
    PRISTINE("Pristine", "✧", 0.0),
    COMBAT_WISDOM("Combat Wisdom", "☯", 0.0),
    MINING_WISDOM("Mining Wisdom", "☯", 0.0),
    FARMING_WISDOM("Farming Wisdom", "☯", 0.0),
    HEALTH_REGEN("Health Regen", "❤", 100.0),
    VITALITY("Vitality", "♨", 100.0),
    SWING_RANGE("Swing Range", "❂", 3.0);

    private final String displayName;
    private final String symbol;
    private final double baseValue;

    Stat(String displayName, String symbol, double baseValue) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.baseValue = baseValue;
    }

    /** Returns the human-readable name of this stat. */
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
