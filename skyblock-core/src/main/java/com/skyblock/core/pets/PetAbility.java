package com.skyblock.core.pets;

import com.skyblock.core.manager.PetManager.PetType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Passive ability associated with each {@link PetType}.
 *
 * <p>Use {@link #forPet(PetType)} to look up the ability for a given pet.</p>
 */
public enum PetAbility {

    HONEY_TRANSMISSION("Honey Transmission"),
    FARMING_FORTUNE("Farming Fortune"),
    HUNTER("Hunter"),
    LEONINE("Leonine"),
    FEROCIOUS_STRIKES("Ferocious Strikes"),
    DOLPHIN_GRACE("Dolphin Grace"),
    GOOD_BOY("Good Boy"),
    STRONG_TRUNK("Strong Trunk"),
    GALLOP("Gallop"),
    NINE_LIVES("Nine Lives"),
    PARROT_EXPERIENCE_BOOST("Parrot Experience Boost"),
    COLD_BLOODED("Cold Blooded"),
    TURTLE_SHELL("Turtle Shell"),
    OVINE_AGILITY("Ovine Agility"),
    PORK_POWER("Pork Power"),
    CLUCK_CLUCK("Cluck Cluck"),
    BLAZE_SHIELD("Blaze Shield"),
    ENDERPEARL_CAST("Enderpearl Cast"),
    ARROW_STORM("Arrow Storm"),
    SPIDER_SENSE("Spider Sense"),
    ZOMBIE_HORDE("Zombie Horde"),
    LEGENDARY_STRIKE("Legendary Strike"),
    GRANITE_SOUL("Granite Soul"),
    HASTE("Haste"),
    FISH_BAIT("Fish Bait"),
    ECHOLOCATION("Echolocation"),
    INK_SPRAY("Ink Spray"),
    ELECTRIC_STING("Electric Sting"),
    DRACONIC_BLOOD("Draconic Blood"),
    DARK_SHADOW("Dark Shadow"),
    ICE_BREATH("Ice Breath"),
    REBIRTH("Rebirth"),
    GHOULISH_HUNGER("Ghoulish Hunger"),
    SANTA_HELP("Santa Help"),
    STICKY_TRAIL("Sticky Trail"),
    HARDY_SHELL("Hardy Shell"),
    WATER_AFFINITY("Water Affinity"),
    OINKER_RUSH("Oinker Rush"),
    BLOODHOUND("Bloodhound"),
    BONE_COLLECTOR("Bone Collector"),
    IRON_DEFENSE("Iron Defense"),
    FELINE_AGILITY("Feline Agility"),
    MONKEY_BUSINESS("Monkey Business"),
    TALL_SIGHTING("Tall Sighting"),
    SPINE_DEFENSE("Spine Defense"),
    UNDERWATER_VISION("Underwater Vision"),
    SNOWFALL("Snowfall"),
    SCARECROW_AURA("Scarecrow Aura"),
    EXTRA_MILK("Extra Milk"),
    MITHRIL_INFUSION("Mithril Infusion"),
    SUMO_SLAM("Sumo Slam"),
    ENDERMITE_LOOT("Endermite Loot");

    /** Human-readable name shown in UI. */
    private final String displayName;

    PetAbility(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    private static final Map<PetType, PetAbility> ABILITY_MAP;

    static {
        ABILITY_MAP = new EnumMap<>(PetType.class);
        ABILITY_MAP.put(PetType.BEE,              HONEY_TRANSMISSION);
        ABILITY_MAP.put(PetType.RABBIT,           FARMING_FORTUNE);
        ABILITY_MAP.put(PetType.WOLF,             HUNTER);
        ABILITY_MAP.put(PetType.LION,             LEONINE);
        ABILITY_MAP.put(PetType.TIGER,            FEROCIOUS_STRIKES);
        ABILITY_MAP.put(PetType.DOLPHIN,          DOLPHIN_GRACE);
        ABILITY_MAP.put(PetType.DOG,              GOOD_BOY);
        ABILITY_MAP.put(PetType.ELEPHANT,         STRONG_TRUNK);
        ABILITY_MAP.put(PetType.HORSE,            GALLOP);
        ABILITY_MAP.put(PetType.CAT,              NINE_LIVES);
        ABILITY_MAP.put(PetType.PARROT,           PARROT_EXPERIENCE_BOOST);
        ABILITY_MAP.put(PetType.PENGUIN,          COLD_BLOODED);
        ABILITY_MAP.put(PetType.TURTLE,           TURTLE_SHELL);
        ABILITY_MAP.put(PetType.SHEEP,            OVINE_AGILITY);
        ABILITY_MAP.put(PetType.PIG,              PORK_POWER);
        ABILITY_MAP.put(PetType.CHICKEN,          CLUCK_CLUCK);
        ABILITY_MAP.put(PetType.BLAZE,            BLAZE_SHIELD);
        ABILITY_MAP.put(PetType.ENDERMAN,         ENDERPEARL_CAST);
        ABILITY_MAP.put(PetType.SKELETON,         ARROW_STORM);
        ABILITY_MAP.put(PetType.SPIDER,           SPIDER_SENSE);
        ABILITY_MAP.put(PetType.ZOMBIE,           ZOMBIE_HORDE);
        ABILITY_MAP.put(PetType.GOLDEN_DRAGON,    LEGENDARY_STRIKE);
        ABILITY_MAP.put(PetType.ROCK,             GRANITE_SOUL);
        ABILITY_MAP.put(PetType.SILVERFISH,       HASTE);
        ABILITY_MAP.put(PetType.FLYING_FISH,      FISH_BAIT);
        ABILITY_MAP.put(PetType.BAT,              ECHOLOCATION);
        ABILITY_MAP.put(PetType.SQUID,            INK_SPRAY);
        ABILITY_MAP.put(PetType.JELLYFISH,        ELECTRIC_STING);
        ABILITY_MAP.put(PetType.ENDER_DRAGON,     DRACONIC_BLOOD);
        ABILITY_MAP.put(PetType.BLACK_CAT,        DARK_SHADOW);
        ABILITY_MAP.put(PetType.BABY_YETI,        ICE_BREATH);
        ABILITY_MAP.put(PetType.PHOENIX,          REBIRTH);
        ABILITY_MAP.put(PetType.GHOUL,            GHOULISH_HUNGER);
        ABILITY_MAP.put(PetType.JERRY,            SANTA_HELP);
        ABILITY_MAP.put(PetType.SLUG,             STICKY_TRAIL);
        ABILITY_MAP.put(PetType.ARMADILLO,        HARDY_SHELL);
        ABILITY_MAP.put(PetType.DROPLET_WISP,     WATER_AFFINITY);
        ABILITY_MAP.put(PetType.PIGMAN,           OINKER_RUSH);
        ABILITY_MAP.put(PetType.HOUND,            BLOODHOUND);
        ABILITY_MAP.put(PetType.WITHER_SKELETON,  BONE_COLLECTOR);
        ABILITY_MAP.put(PetType.GOLEM,            IRON_DEFENSE);
        ABILITY_MAP.put(PetType.OCELOT,           FELINE_AGILITY);
        ABILITY_MAP.put(PetType.MONKEY,           MONKEY_BUSINESS);
        ABILITY_MAP.put(PetType.GIRAFFE,          TALL_SIGHTING);
        ABILITY_MAP.put(PetType.HEDGEHOG,         SPINE_DEFENSE);
        ABILITY_MAP.put(PetType.GUARDIAN,         UNDERWATER_VISION);
        ABILITY_MAP.put(PetType.SNOWMAN,          SNOWFALL);
        ABILITY_MAP.put(PetType.SCARECROW,        SCARECROW_AURA);
        ABILITY_MAP.put(PetType.MOOSHROOM_COW,    EXTRA_MILK);
        ABILITY_MAP.put(PetType.MITHRIL_GOLEM,    MITHRIL_INFUSION);
        ABILITY_MAP.put(PetType.SUMO,             SUMO_SLAM);
        ABILITY_MAP.put(PetType.ENDERMITE,        ENDERMITE_LOOT);
    }

    /**
     * Returns the ability for the given pet type.
     *
     * @param type the pet type to look up
     * @return the associated {@link PetAbility}
     * @throws IllegalArgumentException if no ability is mapped for the type
     */
    public static PetAbility forPet(PetType type) {
        PetAbility ability = ABILITY_MAP.get(type);
        if (ability == null) {
            throw new IllegalArgumentException("No ability mapped for PetType: " + type);
        }
        return ability;
    }
}
