package com.skyblock.core.pets;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing the pet collection (inventory) for each player.
 *
 * <p>Tracks which {@link Pet} instances a player owns and which one is
 * currently equipped. XP and leveling are delegated to {@link PetManager}.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class PetsManager {

    /** Every pet type available in SkyBlock. */
    public enum PetType {
        BEE, WOLF, ENDERMAN, BLAZE, TIGER, DOLPHIN,
        RABBIT, LION, ELEPHANT, HORSE, CAT, DOG, PARROT,
        PENGUIN, TURTLE, SHEEP, PIG, CHICKEN,
        SKELETON, SPIDER, ZOMBIE, JELLYFISH,
        BLUE_WHALE, ARMADILLO, ROCK
    }

    /** Rarity tiers for pets. */
    public enum PetRarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    /** The highest level a pet can reach. */
    public static final int MAX_LEVEL = 100;

    /**
     * Cumulative XP required to reach each level (index 0 = level 1 → 2) per rarity.
     * Values sourced from the SkyBlock wiki pet XP tables.
     */
    public static final Map<String, long[]> PET_XP_TABLE;

    static {
        Map<String, long[]> m = new HashMap<>();
        m.put("COMMON",    new long[]{
            100, 210, 330, 460, 600, 750, 910, 1080, 1260, 1450,
            1650, 1860, 2080, 2310, 2550, 2800, 3060, 3330, 3610, 3900,
            4200, 4510, 4830, 5160, 5500, 5850, 6210, 6580, 6960, 7350,
            7750, 8160, 8580, 9010, 9450, 9900, 10360, 10830, 11310, 11800,
            12300, 12810, 13330, 13860, 14400, 14950, 15510, 16080, 16660, 17250,
            17850, 18460, 19080, 19710, 20350, 21000, 21660, 22330, 23010, 23700,
            24400, 25110, 25830, 26560, 27300, 28050, 28810, 29580, 30360, 31150,
            31950, 32760, 33580, 34410, 35250, 36100, 36960, 37830, 38710, 39600,
            40500, 41410, 42330, 43260, 44200, 45150, 46110, 47080, 48060, 49050,
            50050, 51060, 52080, 53110, 54150, 55200, 56260, 57330, 58410, 59500
        });
        m.put("UNCOMMON", new long[]{
            175, 368, 578, 808, 1058, 1330, 1624, 1940, 2278, 2640,
            3025, 3433, 3864, 4318, 4796, 5298, 5824, 6375, 6950, 7550,
            8175, 8825, 9500, 10200, 10925, 11675, 12450, 13250, 14075, 14925,
            15800, 16700, 17625, 18575, 19550, 20550, 21575, 22625, 23700, 24800,
            25925, 27075, 28250, 29450, 30675, 31925, 33200, 34500, 35825, 37175,
            38550, 39950, 41375, 42825, 44300, 45800, 47325, 48875, 50450, 52050,
            53675, 55325, 57000, 58700, 60425, 62175, 63950, 65750, 67575, 69425,
            71300, 73200, 75125, 77075, 79050, 81050, 83075, 85125, 87200, 89300,
            91425, 93575, 95750, 97950, 100175, 102425, 104700, 107000, 109325, 111675,
            114050, 116450, 118875, 121325, 123800, 126300, 128825, 131375, 133950, 136550
        });
        m.put("RARE", new long[]{
            275, 578, 908, 1268, 1658, 2083, 2543, 3040, 3575, 4150,
            4765, 5421, 6119, 6860, 7645, 8475, 9350, 10271, 11239, 12255,
            13320, 14435, 15601, 16819, 18090, 19415, 20795, 22231, 23724, 25275,
            26885, 28555, 30286, 32079, 33935, 35855, 37840, 39891, 42009, 44195,
            46450, 48775, 51171, 53639, 56180, 58795, 61485, 64251, 67094, 70015,
            73015, 76096, 79259, 82505, 85835, 89251, 92754, 96346, 100029, 103803,
            107670, 111632, 115691, 119849, 124108, 128470, 132936, 137509, 142191, 146984,
            151891, 156914, 162056, 167319, 172705, 178216, 183856, 189627, 195531, 201571,
            207750, 214069, 220531, 227139, 233896, 240804, 247866, 255085, 262464, 270006,
            277714, 285591, 293641, 301867, 310273, 318862, 327637, 336601, 345758, 355112
        });
        m.put("EPIC", new long[]{
            440, 925, 1455, 2035, 2665, 3350, 4095, 4903, 5775, 6715,
            7726, 8811, 9973, 11215, 12540, 13951, 15451, 17043, 18731, 20519,
            22410, 24408, 26517, 28741, 31085, 33553, 36150, 38880, 41748, 44759,
            47917, 51228, 54697, 58330, 62132, 66109, 70267, 74612, 79150, 83888,
            88831, 93986, 99360, 104960, 110793, 116866, 123186, 129761, 136599, 143708,
            151096, 158770, 166739, 175011, 183594, 192496, 201726, 211292, 221203, 231468,
            242096, 253096, 264478, 276250, 288422, 301003, 314003, 327430, 341295, 355607,
            370375, 385610, 401322, 417521, 434218, 451423, 469146, 487398, 506190, 525533,
            545438, 565916, 586979, 608639, 630907, 653795, 677315, 701479, 726299, 751788,
            777958, 804821, 832391, 860681, 889705, 919477, 950011, 981321, 1013421, 1046325
        });
        m.put("LEGENDARY", new long[]{
            660, 1388, 2183, 3053, 4003, 5038, 6163, 7383, 8703, 10128,
            11663, 13313, 15083, 16978, 19003, 21163, 23463, 25908, 28503, 31253,
            34163, 37238, 40483, 43903, 47503, 51288, 55263, 59433, 63803, 68378,
            73163, 78163, 83383, 88828, 94503, 100413, 106563, 112958, 119603, 126503,
            133663, 141088, 148783, 156753, 165003, 173538, 182363, 191483, 200903, 210628,
            220663, 231013, 241683, 252678, 264003, 275663, 287663, 300008, 312703, 325753,
            339163, 352938, 367083, 381603, 396503, 411788, 427463, 443533, 460003, 476878,
            494163, 511863, 529983, 548528, 567503, 586913, 606763, 627058, 647803, 669003,
            690663, 712788, 735383, 758453, 782003, 806038, 830563, 855583, 881103, 907128,
            933663, 960713, 988283, 1016378, 1045003, 1074163, 1103863, 1134108, 1164903, 1196253
        });
        PET_XP_TABLE = Collections.unmodifiableMap(m);
    }

    /**
     * Static metadata for each SkyBlock pet.
     * int[] layout: {rarity_ordinal, speed_bonus, strength_bonus, health_bonus}
     * rarity_ordinal: 0=COMMON, 1=UNCOMMON, 2=RARE, 3=EPIC, 4=LEGENDARY
     */
    public static final Map<String, int[]> PET_DATA;

    static {
        Map<String, int[]> m = new HashMap<>();
        // COMMON
        m.put("CHICKEN",    new int[]{0,  0,  0,  50});
        m.put("PIG",        new int[]{0,  5,  0,  30});
        m.put("RABBIT",     new int[]{0,  0,  0,  40});
        // UNCOMMON
        m.put("CAT",        new int[]{1,  0, 10,  60});
        m.put("DOG",        new int[]{1, 10, 15,  70});
        m.put("SHEEP",      new int[]{1,  0,  0,  80});
        m.put("SKELETON",   new int[]{1,  0, 15,  50});
        m.put("SPIDER",     new int[]{1,  0, 20,  50});
        m.put("ZOMBIE",     new int[]{1,  0, 25,  70});
        // RARE
        m.put("ARMADILLO",  new int[]{2,  0,  5, 120});
        m.put("ELEPHANT",   new int[]{2,  0,  0, 100});
        m.put("HORSE",      new int[]{2, 35,  0,  80});
        m.put("PARROT",     new int[]{2,  0,  0,  60});
        // EPIC
        m.put("BEE",        new int[]{3,  0,  0,  80});
        m.put("DOLPHIN",    new int[]{3, 10, 30,  90});
        m.put("JELLYFISH",  new int[]{3,  0,  0, 150});
        m.put("PENGUIN",    new int[]{3,  0,  0, 120});
        m.put("TURTLE",     new int[]{3,  0,  0, 200});
        // LEGENDARY
        m.put("BLAZE",      new int[]{4,  0, 40, 100});
        m.put("BLUE_WHALE", new int[]{4,  0,  0, 300});
        m.put("ENDER_DRAGON", new int[]{4,  0, 50, 200});
        m.put("ENDERMAN",   new int[]{4,  0, 30, 150});
        m.put("GRIFFIN",    new int[]{4,  0, 35, 120});
        m.put("LION",       new int[]{4, 25, 50, 120});
        m.put("ROCK",       new int[]{4,  0,  0, 500});
        m.put("TIGER",      new int[]{4,  0, 60, 100});
        m.put("WOLF",       new int[]{4,  0, 45, 150});
        PET_DATA = Collections.unmodifiableMap(m);
    }

    /** Cumulative XP required to reach each level, indexed by level - 1. */
    private static final double[] XP_PER_LEVEL;

    static {
        XP_PER_LEVEL = new double[MAX_LEVEL];
        double cumulative = 0;
        for (int i = 0; i < MAX_LEVEL; i++) {
            cumulative += 100.0 * (i + 1);
            XP_PER_LEVEL[i] = cumulative;
        }
    }

    /** Immutable snapshot of a pet's XP and level. */
    public static final class PetData {
        public final PetType type;
        public final double xp;
        public final int level;

        public PetData(PetType type, double xp, int level) {
            this.type = Objects.requireNonNull(type, "type");
            this.xp = xp;
            this.level = level;
        }
    }

    /** A single owned pet instance. */
    public static final class Pet {
        public final UUID id;
        public final PetType type;
        public final PetRarity rarity;

        public Pet(UUID id, PetType type, PetRarity rarity) {
            this.id = Objects.requireNonNull(id, "id");
            this.type = Objects.requireNonNull(type, "type");
            this.rarity = Objects.requireNonNull(rarity, "rarity");
        }
    }

    private static final PetsManager INSTANCE = new PetsManager();

    /** All pets owned by each player, keyed by pet UUID. */
    private final Map<UUID, Map<UUID, Pet>> playerPets = new HashMap<>();

    /** Currently equipped pet per player (pet UUID). */
    private final Map<UUID, UUID> equippedPets = new HashMap<>();

    /** Per-player XP data keyed by pet type. */
    private final Map<UUID, Map<PetType, PetData>> petXpData = new HashMap<>();

    /** Currently active (selected) pet type per player; absent means no active pet. */
    private final Map<UUID, PetType> activePetType = new HashMap<>();

    private PetsManager() {
    }

    /**
     * Returns the single shared {@code PetsManager} instance.
     *
     * @return the singleton instance
     */
    public static PetsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a new pet to the player's collection.
     *
     * @param playerId the player receiving the pet
     * @param type     the type of pet
     * @param rarity   the pet's rarity
     * @return the newly created {@link Pet}
     */
    public Pet addPet(UUID playerId, PetType type, PetRarity rarity) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rarity, "rarity");
        Pet pet = new Pet(UUID.randomUUID(), type, rarity);
        playerPets.computeIfAbsent(playerId, k -> new HashMap<>()).put(pet.id, pet);
        return pet;
    }

    /**
     * Removes a pet from the player's collection, unequipping it first if active.
     *
     * @param playerId the player losing the pet
     * @param petId    the UUID of the pet to remove
     * @return {@code true} if the pet existed and was removed
     */
    public boolean removePet(UUID playerId, UUID petId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || collection.remove(petId) == null) {
            return false;
        }
        UUID equipped = equippedPets.get(playerId);
        if (petId.equals(equipped)) {
            equippedPets.remove(playerId);
        }
        return true;
    }

    /**
     * Equips the given pet for the player, replacing any previously equipped pet.
     *
     * @param playerId the player equipping the pet
     * @param petId    the UUID of the pet to equip
     * @return {@code true} if the pet was found in the player's collection and equipped
     */
    public boolean equipPet(UUID playerId, UUID petId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(petId, "petId");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || !collection.containsKey(petId)) {
            return false;
        }
        equippedPets.put(playerId, petId);
        return true;
    }

    /**
     * Unequips the player's active pet without removing it from the collection.
     *
     * @param playerId the player to unequip
     * @return {@code true} if the player had an active pet, {@code false} otherwise
     */
    public boolean unequipPet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return equippedPets.remove(playerId) != null;
    }

    /**
     * Returns the player's currently equipped {@link Pet}, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the active pet, or {@code null}
     */
    public UUID getActivePetId(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return equippedPets.get(playerId);
    }

    public Pet getActivePet(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        UUID petId = equippedPets.get(playerId);
        if (petId == null) {
            return null;
        }
        Map<UUID, Pet> collection = playerPets.get(playerId);
        return collection == null ? null : collection.get(petId);
    }

    /**
     * Returns an unmodifiable list of all pets owned by the player.
     *
     * @param playerId the player to look up
     * @return an unmodifiable list of pets, empty if the player has none
     */
    public List<Pet> getPets(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<UUID, Pet> collection = playerPets.get(playerId);
        if (collection == null || collection.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(collection.values()));
    }

    /**
     * Returns the currently active pet type for the player, or {@code null} if none.
     *
     * @param player the player to look up
     * @return the active {@link PetType}, or {@code null}
     */
    public PetType getActivePetType(UUID player) {
        Objects.requireNonNull(player, "player");
        return activePetType.get(player);
    }

    /**
     * Sets the active pet type for the player. Pass {@code null} to clear.
     *
     * @param player the player to update
     * @param type   the pet type to activate, or {@code null} to deactivate
     */
    public void setActivePetType(UUID player, PetType type) {
        Objects.requireNonNull(player, "player");
        if (type == null) {
            activePetType.remove(player);
        } else {
            activePetType.put(player, type);
        }
    }

    /**
     * Adds XP to the given pet type for a player, leveling up when thresholds are met.
     *
     * @param player the player gaining XP
     * @param pet    the pet type receiving XP
     * @param amount the amount of XP to add, must not be negative
     * @return the updated {@link PetData} after the addition
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public PetData gainXP(UUID player, PetType pet, double amount) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(pet, "pet");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<PetType, PetData> xpMap = petXpData.computeIfAbsent(
                player, id -> new EnumMap<>(PetType.class));
        PetData current = xpMap.getOrDefault(pet, new PetData(pet, 0.0, 1));
        double newXp = current.xp + amount;
        int level = 1;
        while (level < MAX_LEVEL && newXp >= XP_PER_LEVEL[level - 1]) {
            level++;
        }
        PetData updated = new PetData(pet, newXp, level);
        xpMap.put(pet, updated);
        return updated;
    }

    /**
     * Returns the {@link PetData} for the given pet type, or a default level-1 entry if none.
     *
     * @param player the player to look up
     * @param pet    the pet type to look up
     * @return the current {@link PetData}
     */
    public PetData getPetData(UUID player, PetType pet) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(pet, "pet");
        Map<PetType, PetData> xpMap = petXpData.get(player);
        if (xpMap == null) {
            return new PetData(pet, 0.0, 1);
        }
        return xpMap.getOrDefault(pet, new PetData(pet, 0.0, 1));
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        petXpData.clear();
        activePetType.clear();
        playerPets.clear();
        equippedPets.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (!cfg.isConfigurationSection(key)) {
                    continue;
                }
                String activeStr = cfg.getString(key + ".active");
                if (activeStr != null) {
                    try {
                        activePetType.put(uuid, PetType.valueOf(activeStr));
                    } catch (IllegalArgumentException ignored) {
                        // skip unknown active type
                    }
                }
                String equippedStr = cfg.getString(key + ".equipped");
                if (equippedStr != null) {
                    try {
                        equippedPets.put(uuid, UUID.fromString(equippedStr));
                    } catch (IllegalArgumentException ignored) {
                        // skip malformed equipped UUID
                    }
                }
                if (cfg.isConfigurationSection(key + ".pets")) {
                    Map<UUID, Pet> collection = new HashMap<>();
                    for (String petIdStr : cfg.getConfigurationSection(key + ".pets").getKeys(false)) {
                        try {
                            UUID petId = UUID.fromString(petIdStr);
                            String typeName = cfg.getString(key + ".pets." + petIdStr + ".type");
                            String rarityName = cfg.getString(key + ".pets." + petIdStr + ".rarity");
                            if (typeName == null || rarityName == null) {
                                continue;
                            }
                            PetType type = PetType.valueOf(typeName);
                            PetRarity rarity = PetRarity.valueOf(rarityName);
                            collection.put(petId, new Pet(petId, type, rarity));
                        } catch (IllegalArgumentException ignored) {
                            // skip malformed or unknown pet entries
                        }
                    }
                    if (!collection.isEmpty()) {
                        playerPets.put(uuid, collection);
                    }
                }
                Map<PetType, PetData> xpMap = new EnumMap<>(PetType.class);
                for (String typeName : cfg.getConfigurationSection(key).getKeys(false)) {
                    if ("active".equals(typeName) || "equipped".equals(typeName) || "pets".equals(typeName)) {
                        continue;
                    }
                    try {
                        PetType type = PetType.valueOf(typeName);
                        double xp = cfg.getDouble(key + "." + typeName + ".xp", 0.0);
                        int level = cfg.getInt(key + "." + typeName + ".level", 1);
                        xpMap.put(type, new PetData(type, xp, level));
                    } catch (IllegalArgumentException ignored) {
                        // skip unknown pet types
                    }
                }
                if (!xpMap.isEmpty()) {
                    petXpData.put(uuid, xpMap);
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "pets.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<PetType, PetData>> entry : petXpData.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<PetType, PetData> pd : entry.getValue().entrySet()) {
                String typeName = pd.getKey().name();
                cfg.set(key + "." + typeName + ".xp", pd.getValue().xp);
                cfg.set(key + "." + typeName + ".level", pd.getValue().level);
            }
        }
        for (Map.Entry<UUID, PetType> entry : activePetType.entrySet()) {
            cfg.set(entry.getKey().toString() + ".active", entry.getValue().name());
        }
        for (Map.Entry<UUID, UUID> entry : equippedPets.entrySet()) {
            cfg.set(entry.getKey().toString() + ".equipped", entry.getValue().toString());
        }
        for (Map.Entry<UUID, Map<UUID, Pet>> entry : playerPets.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<UUID, Pet> petEntry : entry.getValue().entrySet()) {
                String petPath = key + ".pets." + petEntry.getKey().toString();
                cfg.set(petPath + ".type", petEntry.getValue().type.name());
                cfg.set(petPath + ".rarity", petEntry.getValue().rarity.name());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save pets.yml", e);
        }
    }

    /**
     * Removes all pet data for the given player, including their collection and equipped pet.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any data
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = playerPets.remove(playerId) != null;
        hadData |= equippedPets.remove(playerId) != null;
        hadData |= petXpData.remove(playerId) != null;
        activePetType.remove(playerId);
        return hadData;
    }
}
