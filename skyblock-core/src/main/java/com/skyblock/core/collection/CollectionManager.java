package com.skyblock.core.collection;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's collection totals keyed by {@link CollectionType}.
 *
 * <p>Progress is stored per player as an {@link EnumMap} created lazily on first
 * access. Not thread-safe; synchronize externally if needed.</p>
 */
public final class CollectionManager {

    /** Every collection type tracked in SkyBlock. */
    public enum CollectionType {
        // Farming
        WHEAT("wheat",                 "Wheat"),
        CARROT("carrot",               "Carrot"),
        POTATO("potato",               "Potato"),
        PUMPKIN("pumpkin",             "Pumpkin"),
        MELON("melon",                 "Melon"),
        MUSHROOM("mushroom",           "Mushroom"),
        CACTUS("cactus",               "Cactus"),
        SUGAR_CANE("sugar_cane",       "Sugar Cane"),
        NETHER_WART("nether_wart",     "Nether Wart"),
        COCOA_BEANS("cocoa_beans",     "Cocoa Beans"),
        // Mining
        COBBLESTONE("cobblestone",     "Cobblestone"),
        COAL("coal",                   "Coal"),
        IRON_INGOT("iron_ingot",       "Iron Ingot"),
        GOLD_INGOT("gold_ingot",       "Gold Ingot"),
        DIAMOND("diamond",             "Diamond"),
        EMERALD("emerald",             "Emerald"),
        REDSTONE("redstone",           "Redstone"),
        LAPIS_LAZULI("lapis_lazuli",   "Lapis Lazuli"),
        QUARTZ("quartz",               "Quartz"),
        OBSIDIAN("obsidian",           "Obsidian"),
        GLOWSTONE("glowstone",         "Glowstone"),
        GRAVEL("gravel",               "Gravel"),
        ICE("ice",                     "Ice"),
        NETHERRACK("netherrack",       "Netherrack"),
        SAND("sand",                   "Sand"),
        END_STONE("end_stone",         "End Stone"),
        // Foraging
        OAK_LOG("oak_log",             "Oak Log"),
        SPRUCE_LOG("spruce_log",       "Spruce Log"),
        BIRCH_LOG("birch_log",         "Birch Log"),
        JUNGLE_LOG("jungle_log",       "Jungle Log"),
        ACACIA_LOG("acacia_log",       "Acacia Log"),
        DARK_OAK_LOG("dark_oak_log",   "Dark Oak Log"),
        // Combat
        ROTTEN_FLESH("rotten_flesh",   "Rotten Flesh"),
        BONE("bone",                   "Bone"),
        SPIDER_EYE("spider_eye",       "Spider Eye"),
        STRING("string",               "String"),
        GUNPOWDER("gunpowder",         "Gunpowder"),
        ENDER_PEARL("ender_pearl",     "Ender Pearl"),
        GHAST_TEAR("ghast_tear",       "Ghast Tear"),
        SLIME_BALL("slime_ball",       "Slime Ball"),
        BLAZE_ROD("blaze_rod",         "Blaze Rod"),
        MAGMA_CREAM("magma_cream",     "Magma Cream"),
        // Fishing
        RAW_FISH("raw_fish",                       "Raw Fish"),
        RAW_SALMON("raw_salmon",                   "Raw Salmon"),
        CLOWNFISH("clownfish",                     "Clownfish"),
        PUFFERFISH("pufferfish",                   "Pufferfish"),
        PRISMARINE_SHARD("prismarine_shard",       "Prismarine Shard"),
        PRISMARINE_CRYSTALS("prismarine_crystals", "Prismarine Crystals"),
        CLAY("clay",                               "Clay"),
        LILY_PAD("lily_pad",                       "Lily Pad"),
        INK_SAC("ink_sac",                         "Ink Sac"),
        SPONGE("sponge",                           "Sponge");

        /** Lower-case item key used in storage and lookups. */
        public final String itemKey;
        public final String displayName;

        CollectionType(String itemKey, String displayName) {
            this.itemKey     = itemKey;
            this.displayName = displayName;
        }
    }

    /** Groups {@link CollectionType} values into SkyBlock skill categories. */
    public enum CollectionCategory {
        FARMING("Farming",
                CollectionType.WHEAT, CollectionType.CARROT, CollectionType.POTATO,
                CollectionType.PUMPKIN, CollectionType.MELON, CollectionType.MUSHROOM,
                CollectionType.CACTUS, CollectionType.SUGAR_CANE,
                CollectionType.NETHER_WART, CollectionType.COCOA_BEANS),
        MINING("Mining",
                CollectionType.COBBLESTONE, CollectionType.COAL, CollectionType.IRON_INGOT,
                CollectionType.GOLD_INGOT, CollectionType.DIAMOND, CollectionType.EMERALD,
                CollectionType.REDSTONE, CollectionType.LAPIS_LAZULI, CollectionType.QUARTZ,
                CollectionType.OBSIDIAN, CollectionType.GLOWSTONE, CollectionType.GRAVEL,
                CollectionType.ICE, CollectionType.NETHERRACK, CollectionType.SAND,
                CollectionType.END_STONE),
        FORAGING("Foraging",
                CollectionType.OAK_LOG, CollectionType.SPRUCE_LOG, CollectionType.BIRCH_LOG,
                CollectionType.JUNGLE_LOG, CollectionType.ACACIA_LOG, CollectionType.DARK_OAK_LOG),
        COMBAT("Combat",
                CollectionType.ROTTEN_FLESH, CollectionType.BONE, CollectionType.SPIDER_EYE,
                CollectionType.STRING, CollectionType.GUNPOWDER, CollectionType.ENDER_PEARL,
                CollectionType.GHAST_TEAR, CollectionType.SLIME_BALL, CollectionType.BLAZE_ROD,
                CollectionType.MAGMA_CREAM),
        FISHING("Fishing",
                CollectionType.RAW_FISH, CollectionType.RAW_SALMON, CollectionType.CLOWNFISH,
                CollectionType.PUFFERFISH, CollectionType.PRISMARINE_SHARD,
                CollectionType.PRISMARINE_CRYSTALS, CollectionType.CLAY, CollectionType.LILY_PAD,
                CollectionType.INK_SAC, CollectionType.SPONGE);

        private final String displayName;
        private final CollectionType[] types;

        CollectionCategory(String displayName, CollectionType... types) {
            this.displayName = displayName;
            this.types = types;
        }

        public String getDisplayName() {
            return displayName;
        }

        public CollectionType[] getTypes() {
            return types;
        }
    }

    private static final CollectionManager INSTANCE = new CollectionManager();

    /** per-player totals: player → (CollectionType → total gathered) */
    private final Map<UUID, Map<CollectionType, Long>> playerCollections = new HashMap<>();

    private CollectionManager() {
    }

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds {@code amount} items to the player's total for the given collection type.
     *
     * @param playerId   the player gaining progress, must not be null
     * @param type       the collection type, must not be null
     * @param amount     items to add, must not be negative
     * @return the player's updated total for that collection
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addItems(UUID playerId, CollectionType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<CollectionType, Long> totals = playerCollections.computeIfAbsent(
                playerId, id -> new EnumMap<>(CollectionType.class));
        long total = totals.getOrDefault(type, 0L) + amount;
        totals.put(type, total);
        return total;
    }

    /**
     * Adds {@code amount} items by collection name string; ignores unknown names.
     *
     * @param playerId   the player gaining progress, must not be null
     * @param collection the collection name (e.g. "WHEAT"), must not be null or blank
     * @param amount     items to add, must not be negative
     * @return the player's updated total, or {@code -1} if the name is unknown
     */
    public long addItems(UUID playerId, String collection, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (collection == null || collection.isBlank()) {
            throw new IllegalArgumentException("collection must not be null or blank");
        }
        CollectionType type = parseType(collection);
        if (type == null) {
            return -1L;
        }
        return addItems(playerId, type, amount);
    }

    /**
     * Returns how many items the player has gathered for the given collection type.
     *
     * @param playerId the player to look up, must not be null
     * @param type     the collection type, must not be null
     * @return the total items gathered, {@code 0} if the player has none
     */
    public long getItems(UUID playerId, CollectionType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<CollectionType, Long> totals = playerCollections.get(playerId);
        return totals == null ? 0L : totals.getOrDefault(type, 0L);
    }

    /**
     * Returns how many items the player has gathered by collection name; returns {@code 0} for unknown names.
     *
     * @param playerId   the player to look up, must not be null
     * @param collection the collection name, must not be null or blank
     * @return the total items gathered, {@code 0} if the player has none or the name is unknown
     */
    public long getItems(UUID playerId, String collection) {
        Objects.requireNonNull(playerId, "playerId");
        if (collection == null || collection.isBlank()) {
            throw new IllegalArgumentException("collection must not be null or blank");
        }
        CollectionType type = parseType(collection);
        return type == null ? 0L : getItems(playerId, type);
    }

    /**
     * Returns an unmodifiable view of all collection totals for the player.
     *
     * @param playerId the player to look up, must not be null
     * @return an unmodifiable map of CollectionType to total, empty if none recorded
     */
    public Map<CollectionType, Long> getAll(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<CollectionType, Long> totals = playerCollections.get(playerId);
        return totals == null ? Collections.emptyMap() : Collections.unmodifiableMap(totals);
    }

    /**
     * Resets all collection progress for the player.
     *
     * @param playerId the player to reset, must not be null
     * @return {@code true} if the player had progress to reset, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerCollections.remove(playerId) != null;
    }

    /**
     * Returns the sum of all item totals for the given category.
     *
     * @param playerId the player to look up, must not be null
     * @param category the collection category, must not be null
     * @return the combined total for all types in the category
     */
    public long getTotalForCategory(UUID playerId, CollectionCategory category) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(category, "category");
        long total = 0L;
        for (CollectionType type : category.getTypes()) {
            total += getItems(playerId, type);
        }
        return total;
    }

    private static CollectionType parseType(String name) {
        for (CollectionType t : CollectionType.values()) {
            if (t.name().equalsIgnoreCase(name) || t.itemKey.equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
}
