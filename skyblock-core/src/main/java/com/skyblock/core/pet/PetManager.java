package com.skyblock.core.pet;

import com.skyblock.core.combat.StatManager.CombatStat;
import com.skyblock.core.pets.PetManager.PetType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Singleton holding static {@link PetDefinition} records for every {@link PetType}.
 *
 * <p>This manager is concerned with pet metadata (display name, description, which
 * stat the pet boosts, and the flat bonus applied per level). Player-specific state
 * (XP, active pet) is handled by {@link com.skyblock.core.pets.PetManager}.</p>
 */
public final class PetManager {

    /**
     * Immutable metadata record for a single pet type.
     *
     * @param displayName  human-readable name shown to players
     * @param description  short flavour text describing the pet
     * @param bonusStat    the {@link CombatStat} this pet boosts
     * @param bonusPerLevel flat stat bonus applied per pet level
     */
    public record PetDefinition(
            String displayName,
            String description,
            CombatStat bonusStat,
            double bonusPerLevel) {
    }

    private static final Map<PetType, PetDefinition> DEFINITIONS;

    static {
        DEFINITIONS = new EnumMap<>(PetType.class);
        DEFINITIONS.put(PetType.CHICKEN,  new PetDefinition("Chicken",  "A friendly farm chicken.",          CombatStat.SPEED,        0.3));
        DEFINITIONS.put(PetType.WOLF,     new PetDefinition("Wolf",     "A fierce wolf companion.",          CombatStat.STRENGTH,     0.5));
        DEFINITIONS.put(PetType.RABBIT,   new PetDefinition("Rabbit",   "Hops its way to extra speed.",      CombatStat.SPEED,        0.4));
        DEFINITIONS.put(PetType.BEE,      new PetDefinition("Bee",      "Buzzes with magical energy.",       CombatStat.INTELLIGENCE, 0.5));
        DEFINITIONS.put(PetType.LION,     new PetDefinition("Lion",     "Roars with offensive power.",       CombatStat.STRENGTH,     0.8));
        DEFINITIONS.put(PetType.TIGER,    new PetDefinition("Tiger",    "Strikes with ferocious speed.",     CombatStat.FEROCITY,     0.4));
        DEFINITIONS.put(PetType.ELEPHANT, new PetDefinition("Elephant", "Thick hide grants extra defense.",  CombatStat.DEFENSE,      0.6));
        DEFINITIONS.put(PetType.HORSE,    new PetDefinition("Horse",    "Gallops ahead with swift attacks.", CombatStat.ATTACK_SPEED, 0.3));
        DEFINITIONS.put(PetType.CAT,      new PetDefinition("Cat",      "Lucky cat improves magic find.",    CombatStat.MAGIC_FIND,   0.5));
        DEFINITIONS.put(PetType.PARROT,   new PetDefinition("Parrot",   "Wise bird boosts intelligence.",    CombatStat.INTELLIGENCE, 0.4));
        DEFINITIONS.put(PetType.PENGUIN,  new PetDefinition("Penguin",  "Waddles toughly, raising defense.", CombatStat.DEFENSE,      0.4));
        DEFINITIONS.put(PetType.TURTLE,   new PetDefinition("Turtle",   "Shell provides true defense.",      CombatStat.TRUE_DEFENSE, 0.3));
        DEFINITIONS.put(PetType.SHEEP,    new PetDefinition("Sheep",    "Fluffy wool bolsters vitality.",    CombatStat.VITALITY,     0.4));
        DEFINITIONS.put(PetType.PIG,      new PetDefinition("Pig",      "Hearty pig increases max health.",  CombatStat.HEALTH,       1.0));
        DEFINITIONS.put(PetType.DOLPHIN,  new PetDefinition("Dolphin",  "Swift fins raise attack speed.",    CombatStat.ATTACK_SPEED, 0.4));
        DEFINITIONS.put(PetType.BLAZE,    new PetDefinition("Blaze",    "Blazing aura boosts crit damage.",  CombatStat.CRIT_DAMAGE,  0.5));
        DEFINITIONS.put(PetType.ENDERMAN, new PetDefinition("Enderman", "Teleports to land critical hits.",  CombatStat.CRIT_CHANCE,  0.3));
        DEFINITIONS.put(PetType.SKELETON, new PetDefinition("Skeleton", "Archer companion raises strength.", CombatStat.STRENGTH,     0.4));
        DEFINITIONS.put(PetType.SPIDER,   new PetDefinition("Spider",   "Venom drains crit chance bonus.",   CombatStat.CRIT_CHANCE,  0.4));
        DEFINITIONS.put(PetType.ZOMBIE,   new PetDefinition("Zombie",   "Undead resilience raises health.",  CombatStat.HEALTH,       1.5));
    }

    private static final PetManager INSTANCE = new PetManager();

    private PetManager() {
    }

    /**
     * Returns the single shared {@code PetManager} instance.
     *
     * @return the singleton instance
     */
    public static PetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the {@link PetDefinition} for the given pet type.
     *
     * @param type the pet type to look up
     * @return the definition, never {@code null}
     * @throws NullPointerException if {@code type} is null
     */
    public PetDefinition getDefinition(PetType type) {
        return DEFINITIONS.get(Objects.requireNonNull(type, "type"));
    }

    /**
     * Returns the total stat bonus for a pet of the given type at the specified level.
     *
     * @param type  the pet type
     * @param level the pet's current level (1–100)
     * @return the flat bonus value for that level
     */
    public double getBonus(PetType type, int level) {
        PetDefinition def = getDefinition(type);
        return def.bonusPerLevel() * level;
    }

    /**
     * Returns an unmodifiable view of all pet definitions keyed by type.
     *
     * @return unmodifiable map of all definitions
     */
    public Map<PetType, PetDefinition> getAllDefinitions() {
        return Collections.unmodifiableMap(DEFINITIONS);
    }
}
