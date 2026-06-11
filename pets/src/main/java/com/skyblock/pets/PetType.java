package com.skyblock.pets;

/**
 * The kinds of companion pet a player can own.
 *
 * <p>Each type carries its human-readable display name and the skill
 * category whose experience levels the pet up while it is active.</p>
 */
public enum PetType {

    WOLF("Wolf", "Combat"),
    ENDERMAN("Enderman", "Combat"),
    ZOMBIE("Zombie", "Combat"),
    SPIDER("Spider", "Combat"),
    ROCK("Rock", "Mining"),
    SILVERFISH("Silverfish", "Mining"),
    RABBIT("Rabbit", "Farming"),
    ELEPHANT("Elephant", "Farming"),
    MONKEY("Monkey", "Foraging"),
    OCELOT("Ocelot", "Foraging"),
    SQUID("Squid", "Fishing"),
    DOLPHIN("Dolphin", "Fishing");

    private final String displayName;
    private final String skillCategory;

    PetType(String displayName, String skillCategory) {
        this.displayName = displayName;
        this.skillCategory = skillCategory;
    }

    /**
     * Returns the human-readable name of this pet type.
     *
     * @return the display name, e.g. {@code "Enderman"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the skill category this pet gains experience from while
     * active.
     *
     * @return the skill category name, e.g. {@code "Combat"}
     */
    public String getSkillCategory() {
        return skillCategory;
    }
}
