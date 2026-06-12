package com.skyblock.alchemy;

/**
 * The ingredients that can be added to a brewing stand to modify a potion.
 *
 * <p>Each ingredient carries its human-readable display name and the alchemy
 * experience awarded when it is brewed, e.g. via
 * {@link AlchemyManager#addExperience(java.util.UUID, long)}.</p>
 */
public enum BrewingIngredient {

    NETHER_WART("Nether Wart", 160L),
    SUGAR("Sugar", 18L),
    RABBIT_FOOT("Rabbit's Foot", 20L),
    BLAZE_POWDER("Blaze Powder", 25L),
    GLISTERING_MELON("Glistering Melon", 25L),
    SPIDER_EYE("Spider Eye", 15L),
    FERMENTED_SPIDER_EYE("Fermented Spider Eye", 20L),
    GHAST_TEAR("Ghast Tear", 30L),
    MAGMA_CREAM("Magma Cream", 25L),
    PUFFERFISH("Pufferfish", 22L),
    GOLDEN_CARROT("Golden Carrot", 25L),
    PHANTOM_MEMBRANE("Phantom Membrane", 28L),
    GLOWSTONE_DUST("Glowstone Dust", 10L),
    REDSTONE_DUST("Redstone Dust", 10L);

    private final String displayName;
    private final long alchemyXp;

    BrewingIngredient(String displayName, long alchemyXp) {
        this.displayName = displayName;
        this.alchemyXp = alchemyXp;
    }

    /**
     * Returns the human-readable name shown in brewing menus.
     *
     * @return the display name, e.g. {@code "Nether Wart"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the alchemy experience awarded for brewing with this ingredient.
     *
     * @return the experience granted per brew
     */
    public long getAlchemyXp() {
        return alchemyXp;
    }
}
