package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Canonical singleton managing SkyBlock enchantments for the enchanting skill system.
 *
 * <p>Tracks which enchant types at which levels are active for each player.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EnchantingManager {

    /** Canonical 30-entry enchant catalogue used for enchanting-table interactions. */
    public enum EnchantType {
        // Combat
        SHARPNESS(        "Sharpness",          7),
        CRITICAL(         "Critical",           7),
        SMITE(            "Smite",              7),
        BANE_OF_ARTHROPODS("Bane of Arthropods", 7),
        FIRST_STRIKE(     "First Strike",       5),
        GIANT_KILLER(     "Giant Killer",       7),
        ENDER_SLAYER(     "Ender Slayer",       7),
        DRAGON_HUNTER(    "Dragon Hunter",      5),
        THUNDERLORD(      "Thunderlord",        7),
        EXECUTE(          "Execute",            7),
        // Utility / Special
        TELEKINESIS(      "Telekinesis",        1),
        LOOTING(          "Looting",            5),
        POWER(            "Power",              7),
        SMELTING_TOUCH(   "Smelting Touch",     1),
        MAGNET(           "Magnet",             1),
        LIFE_STEAL(       "Life Steal",         5),
        // Fishing
        LUCK_OF_THE_SEA(  "Luck of the Sea",    7),
        ANGLER(           "Angler",             6),
        FRAIL(            "Frail",              5),
        EXPERTISE(        "Expertise",         10),
        // Farming
        CULTIVATING(      "Cultivating",       10),
        GREEN_THUMB(      "Green Thumb",        5),
        HARVESTING(       "Harvesting",         6),
        // Mining / Tool
        EFFICIENCY(       "Efficiency",         5),
        FORTUNE(          "Fortune",            4),
        SILK_TOUCH(       "Silk Touch",         1),
        // Armor
        PROTECTION(       "Protection",         7),
        THORNS(           "Thorns",             3),
        GROWTH(           "Growth",             7),
        FEATHER_FALLING(  "Feather Falling",    10),
        REJUVENATE(       "Rejuvenate",         5);

        private final String displayName;
        private final int maxLevel;

        EnchantType(String displayName, int maxLevel) {
            this.displayName = displayName;
            this.maxLevel = maxLevel;
        }

        public String getDisplayName() { return displayName; }
        public int getMaxLevel() { return maxLevel; }
    }

    /** Simple enchant-name enum for category lookups and tab completion. */
    public enum SkyBlockEnchant {
        // Combat
        SHARPNESS, CRITICAL, SMITE, BANE_OF_ARTHROPODS, FIRST_STRIKE,
        GIANT_KILLER, ENDER_SLAYER, CUBISM, DRAGON_HUNTER, THUNDERLORD, VAMPIRISM,
        LIFE_STEAL, LETHALITY, EXECUTE, PROSECUTE, OVERLOAD,
        // Utility / Special
        TELEKINESIS, LOOTING, SMELTING_TOUCH, MAGNET, SILK_TOUCH,
        FIRE_ASPECT, KNOCKBACK, PUNCH, FLAME,
        // Bow
        POWER,
        // Fishing
        LUCK_OF_THE_SEA, ANGLER, FRAIL, EXPERTISE,
        // Farming
        CULTIVATING, GREEN_THUMB, DEDICATION, REPLENISH, HARVESTING,
        TURBO_WHEAT, TURBO_COCO, TURBO_CACTUS, TURBO_MELON, TURBO_PUMPKIN,
        TURBO_WARTS, TURBO_MUSHROOMS, TURBO_POTATO, TURBO_CARROT, TURBO_SUGAR_CANE,
        // Mining / Tool
        EFFICIENCY, FORTUNE,
        // Armor
        PROTECTION, THORNS, GROWTH, FEATHER_FALLING, SUGAR_RUSH, REJUVENATE,
        // Misc
        LUCK, CHANCE, ULTIMATE_WISE,
        // Dungeon / Extra
        SHREDDER, SCAVENGER, SOUL_EATER, VENOMOUS, VICIOUS
    }

    /** Every SkyBlock enchant type with display name and maximum level. */
    public enum SkyBlockEnchantment {
        // Combat
        SHARPNESS("Sharpness", 7),
        CRITICAL("Critical", 7),
        SMITE("Smite", 7),
        BANE_OF_ARTHROPODS("Bane of Arthropods", 7),
        FIRST_STRIKE("First Strike", 5),
        GIANT_KILLER("Giant Killer", 7),
        ENDER_SLAYER("Ender Slayer", 7),
        CUBISM("Cubism", 6),
        DRAGON_HUNTER("Dragon Hunter", 5),
        THUNDERLORD("Thunderlord", 7),
        VAMPIRISM("Vampirism", 6),
        LIFE_STEAL("Life Steal", 5),
        LETHALITY("Lethality", 6),
        EXECUTE("Execute", 7),
        PROSECUTE("Prosecute", 7),
        OVERLOAD("Overload", 5),
        // Utility / Special
        TELEKINESIS("Telekinesis", 1),
        LOOTING("Looting", 5),
        SMELTING_TOUCH("Smelting Touch", 1),
        MAGNET("Magnet", 1),
        SILK_TOUCH("Silk Touch", 1),
        FIRE_ASPECT("Fire Aspect", 2),
        KNOCKBACK("Knockback", 2),
        PUNCH("Punch", 2),
        FLAME("Flame", 1),
        // Bow
        POWER("Power", 7),
        // Fishing
        LUCK_OF_THE_SEA("Luck of the Sea", 7),
        ANGLER("Angler", 6),
        FRAIL("Frail", 5),
        EXPERTISE("Expertise", 10),
        // Farming
        CULTIVATING("Cultivating", 10),
        GREEN_THUMB("Green Thumb", 5),
        DEDICATION("Dedication", 4),
        REPLENISH("Replenish", 1),
        HARVESTING("Harvesting", 6),
        TURBO_WHEAT("Turbo-Wheat", 5),
        TURBO_COCO("Turbo-Coco", 5),
        TURBO_CACTUS("Turbo-Cactus", 5),
        TURBO_MELON("Turbo-Melon", 5),
        TURBO_PUMPKIN("Turbo-Pumpkin", 5),
        TURBO_WARTS("Turbo-Warts", 5),
        TURBO_MUSHROOMS("Turbo-Mushrooms", 5),
        TURBO_POTATO("Turbo-Potato", 5),
        TURBO_CARROT("Turbo-Carrot", 5),
        TURBO_SUGAR_CANE("Turbo-Sugar Cane", 5),
        // Mining / Tool
        EFFICIENCY("Efficiency", 5),
        FORTUNE("Fortune", 4),
        // Armor
        PROTECTION("Protection", 7),
        THORNS("Thorns", 3),
        GROWTH("Growth", 7),
        FEATHER_FALLING("Feather Falling", 10),
        SUGAR_RUSH("Sugar Rush", 3),
        REJUVENATE("Rejuvenate", 5),
        // Misc
        LUCK("Luck", 7),
        CHANCE("Chance", 5),
        ULTIMATE_WISE("Ultimate Wise", 5),
        // Dungeon / Extra
        SHREDDER("Shredder", 5),
        SCAVENGER("Scavenger", 6),
        SOUL_EATER("Soul Eater", 5),
        VENOMOUS("Venomous", 5),
        VICIOUS("Vicious", 5);

        private final String displayName;
        private final int maxLevel;

        SkyBlockEnchantment(String displayName, int maxLevel) {
            this.displayName = displayName;
            this.maxLevel = maxLevel;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMaxLevel() {
            return maxLevel;
        }
    }

    /**
     * Represents an enchantment book: a named item that holds one enchantment at a specific level.
     *
     * @param name        display name of the book (e.g. "Sharpness Book V")
     * @param enchantment the enchantment stored in this book
     * @param level       the enchantment level; must be between 1 and {@code enchantment.getMaxLevel()}
     */
    public record EnchantmentBook(String name, SkyBlockEnchantment enchantment, int level) {
        public EnchantmentBook {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(enchantment, "enchantment");
            if (level < 1 || level > enchantment.getMaxLevel()) {
                throw new IllegalArgumentException(
                        "Level " + level + " out of range [1, " + enchantment.getMaxLevel() + "] for " + enchantment);
            }
        }
    }

    /** Static catalogue: enchant name → {maxLevel, bookshelvesRequired}. */
    public static final Map<String, int[]> ENCHANT_DATA;
    static {
        Map<String, int[]> m = new HashMap<>();
        // Combat
        m.put("SHARPNESS",          new int[]{7, 15});
        m.put("CRITICAL",           new int[]{7, 12});
        m.put("SMITE",              new int[]{7, 10});
        m.put("BANE_OF_ARTHROPODS", new int[]{7, 10});
        m.put("FIRST_STRIKE",       new int[]{5,  8});
        m.put("GIANT_KILLER",       new int[]{7, 12});
        m.put("ENDER_SLAYER",       new int[]{7, 15});
        m.put("DRAGON_HUNTER",      new int[]{5, 15});
        m.put("THUNDERLORD",        new int[]{7, 12});
        m.put("EXECUTE",            new int[]{7, 10});
        // Utility / Special
        m.put("TELEKINESIS",        new int[]{1,  5});
        m.put("LOOTING",            new int[]{5,  8});
        m.put("POWER",              new int[]{7, 10});
        m.put("SMELTING_TOUCH",     new int[]{1,  8});
        m.put("MAGNET",             new int[]{1,  5});
        m.put("LIFE_STEAL",         new int[]{5, 10});
        // Fishing
        m.put("LUCK_OF_THE_SEA",    new int[]{7, 10});
        m.put("ANGLER",             new int[]{6, 12});
        m.put("FRAIL",              new int[]{5, 10});
        m.put("EXPERTISE",          new int[]{10, 15});
        // Farming
        m.put("CULTIVATING",        new int[]{10, 15});
        m.put("GREEN_THUMB",        new int[]{5, 10});
        m.put("HARVESTING",         new int[]{6, 10});
        // Mining / Tool
        m.put("EFFICIENCY",         new int[]{5, 10});
        m.put("FORTUNE",            new int[]{4, 12});
        m.put("SILK_TOUCH",         new int[]{1,  8});
        // Armor
        m.put("PROTECTION",         new int[]{7, 15});
        m.put("THORNS",             new int[]{3,  8});
        m.put("GROWTH",             new int[]{7, 12});
        m.put("FEATHER_FALLING",    new int[]{10, 10});
        m.put("REJUVENATE",         new int[]{5, 12});
        ENCHANT_DATA = Collections.unmodifiableMap(m);
    }

    /**
     * Ultimate enchants. At most one ultimate enchant may be active on a player at a
     * time; applying a second ultimate is rejected as a conflict.
     */
    public static final Set<SkyBlockEnchantment> ULTIMATE_ENCHANTS =
            Collections.unmodifiableSet(EnumSet.of(SkyBlockEnchantment.ULTIMATE_WISE));

    /** Mutually exclusive enchant pairs: a single item cannot hold both. */
    private static final Map<SkyBlockEnchantment, Set<SkyBlockEnchantment>> CONFLICTS;
    static {
        Map<SkyBlockEnchantment, Set<SkyBlockEnchantment>> c = new EnumMap<>(SkyBlockEnchantment.class);
        // Silk Touch and Fortune are mutually exclusive on the same tool.
        addConflict(c, SkyBlockEnchantment.SILK_TOUCH, SkyBlockEnchantment.FORTUNE);
        CONFLICTS = Collections.unmodifiableMap(c);
    }

    private static void addConflict(Map<SkyBlockEnchantment, Set<SkyBlockEnchantment>> c,
                                    SkyBlockEnchantment a, SkyBlockEnchantment b) {
        c.computeIfAbsent(a, k -> EnumSet.noneOf(SkyBlockEnchantment.class)).add(b);
        c.computeIfAbsent(b, k -> EnumSet.noneOf(SkyBlockEnchantment.class)).add(a);
    }

    private static final EnchantingManager INSTANCE = new EnchantingManager();

    /** Per-player enchanting skill levels (1–60); absent entries default to 1. */
    private final Map<UUID, Integer> enchantingLevels = new HashMap<>();

    /** Per-player enchantment levels; absent entries mean the enchantment is not applied. */
    private final Map<UUID, Map<SkyBlockEnchantment, Integer>> playerEnchantments = new HashMap<>();

    /** Per-player enchantment book inventories. */
    private final Map<UUID, List<EnchantmentBook>> playerBooks = new HashMap<>();

    private final Map<UUID, List<String>> enchantingHistory = new HashMap<>();

    private EnchantingManager() {
    }

    public static EnchantingManager getInstance() {
        return INSTANCE;
    }

    public void recordEnchantingEvent(UUID playerUuid, String summary) {
        enchantingHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getEnchantingHistory(UUID playerUuid) {
        return Collections.unmodifiableList(enchantingHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllEnchantingHistory() {
        return Collections.unmodifiableMap(enchantingHistory);
    }

    public String getEnchantingStats(UUID playerId) {
        int booksApplied = enchantingHistory.getOrDefault(playerId, Collections.emptyList()).size();
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.getOrDefault(playerId, Collections.emptyMap());
        int totalLevels = enchants.values().stream().mapToInt(Integer::intValue).sum();
        return "Total books applied: " + booksApplied + ", Cumulative enchant levels: " + totalLevels;
    }

    /**
     * Returns the enchanting skill level for the given player.
     *
     * @param playerId the player's UUID
     * @return skill level, or {@code 1} if none recorded
     */
    public int getEnchantingLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return enchantingLevels.getOrDefault(playerId, 1);
    }

    /**
     * Sets the enchanting skill level for the given player.
     *
     * @param playerId the player's UUID
     * @param level    the skill level (must be >= 1)
     */
    public void setEnchantingLevel(UUID playerId, int level) {
        Objects.requireNonNull(playerId, "playerId");
        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }
        enchantingLevels.put(playerId, level);
    }

    /**
     * Returns the level of the given enchant type for the given player, or
     * {@code 0} if the enchantment is not applied.
     */
    public int getLevel(UUID playerId, SkyBlockEnchantment type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
        return enchants == null ? 0 : enchants.getOrDefault(type, 0);
    }

    /**
     * Applies an enchant type at the given level to the player.
     *
     * @throws IllegalArgumentException if the level is out of range or the enchant
     *                                  conflicts with one already applied
     */
    public void setEnchantment(UUID playerId, SkyBlockEnchantment type, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        int max = type.getMaxLevel();
        if (level < 1 || level > max) {
            throw new IllegalArgumentException(
                    "Level " + level + " out of range [1, " + max + "] for " + type);
        }
        SkyBlockEnchantment conflict = firstConflict(playerId, type);
        if (conflict != null) {
            throw new IllegalArgumentException(
                    type.getDisplayName() + " conflicts with " + conflict.getDisplayName()
                            + "; remove it first.");
        }
        playerEnchantments.computeIfAbsent(playerId, id -> new EnumMap<>(SkyBlockEnchantment.class))
                .put(type, level);
        recordEnchantingEvent(playerId, "Enchanted " + type.name() + " level " + level);
    }

    /**
     * Returns {@code true} if the given enchant is an ultimate enchant.
     */
    public boolean isUltimate(SkyBlockEnchantment type) {
        Objects.requireNonNull(type, "type");
        return ULTIMATE_ENCHANTS.contains(type);
    }

    /**
     * Returns the set of enchants that directly conflict with the given type.
     */
    public Set<SkyBlockEnchantment> getConflicts(SkyBlockEnchantment type) {
        Objects.requireNonNull(type, "type");
        return CONFLICTS.getOrDefault(type, Collections.emptySet());
    }

    /**
     * Returns the experience-level cost to apply the given enchant at the given level.
     * Cost scales with the bookshelf power the enchant requires and the target level.
     */
    public int getEnchantCost(SkyBlockEnchantment type, int level) {
        Objects.requireNonNull(type, "type");
        if (level < 1 || level > type.getMaxLevel()) {
            throw new IllegalArgumentException(
                    "Level " + level + " out of range [1, " + type.getMaxLevel() + "] for " + type);
        }
        int[] data = ENCHANT_DATA.get(type.name());
        int base = data == null ? 5 : data[1];
        return base * level;
    }

    /**
     * Returns the first enchant already applied to the player that conflicts with
     * {@code type} (direct conflict, or a second ultimate), or {@code null} if none.
     * A re-application of {@code type} itself is never a conflict.
     */
    private SkyBlockEnchantment firstConflict(UUID playerId, SkyBlockEnchantment type) {
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
        if (enchants == null) {
            return null;
        }
        Set<SkyBlockEnchantment> direct = CONFLICTS.getOrDefault(type, Collections.emptySet());
        boolean ultimate = ULTIMATE_ENCHANTS.contains(type);
        for (SkyBlockEnchantment applied : enchants.keySet()) {
            if (applied == type) {
                continue;
            }
            if (direct.contains(applied) || (ultimate && ULTIMATE_ENCHANTS.contains(applied))) {
                return applied;
            }
        }
        return null;
    }

    /**
     * Removes an enchant type from the player.
     *
     * @return {@code true} if the enchantment was present, {@code false} otherwise
     */
    public boolean removeEnchantment(UUID playerId, SkyBlockEnchantment type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
        if (enchants == null) {
            return false;
        }
        boolean removed = enchants.remove(type) != null;
        if (enchants.isEmpty()) {
            playerEnchantments.remove(playerId);
        }
        if (removed) {
            recordEnchantingEvent(playerId, "Disenchanted " + type.name());
        }
        return removed;
    }

    /**
     * Returns an unmodifiable view of all enchantments currently applied to the player.
     */
    public Map<SkyBlockEnchantment, Integer> getEnchantments(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
        return enchants == null ? Collections.emptyMap() : Collections.unmodifiableMap(enchants);
    }

    /**
     * Returns the maximum allowed level for the given enchant type.
     */
    public int getMaxLevel(SkyBlockEnchantment type) {
        Objects.requireNonNull(type, "type");
        return type.getMaxLevel();
    }

    /**
     * Returns an immutable snapshot of the complete enchant table: every enchant
     * type mapped to its maximum level.
     */
    public Map<SkyBlockEnchantment, Integer> getEnchantTable() {
        Map<SkyBlockEnchantment, Integer> table = new EnumMap<>(SkyBlockEnchantment.class);
        for (SkyBlockEnchantment type : SkyBlockEnchantment.values()) {
            table.put(type, type.getMaxLevel());
        }
        return Collections.unmodifiableMap(table);
    }

    /**
     * Returns the bookshelf power required to access the given enchant at the
     * enchanting table, or {@code -1} if no requirement is configured for it.
     */
    public int getRequiredBookshelfPower(SkyBlockEnchantment type) {
        Objects.requireNonNull(type, "type");
        int[] data = ENCHANT_DATA.get(type.name());
        return data == null ? -1 : data[1];
    }

    /**
     * Removes all enchantment data for the given player.
     *
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerBooks.remove(playerId);
        enchantingLevels.remove(playerId);
        enchantingHistory.remove(playerId);
        return playerEnchantments.remove(playerId) != null;
    }

    /**
     * Adds an enchantment book to the player's book inventory.
     *
     * @param playerId the player to update
     * @param book     the book to add
     */
    public void addBook(UUID playerId, EnchantmentBook book) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(book, "book");
        playerBooks.computeIfAbsent(playerId, id -> new ArrayList<>()).add(book);
    }

    /**
     * Returns an unmodifiable view of all enchantment books held by the player.
     *
     * @param playerId the player to look up
     * @return list of books; empty if the player holds none
     */
    public List<EnchantmentBook> getBooks(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<EnchantmentBook> books = playerBooks.get(playerId);
        return books == null ? Collections.emptyList() : Collections.unmodifiableList(books);
    }

    /**
     * Removes the book at the given index from the player's book inventory and applies
     * its enchantment to the player, provided it is within level bounds and does not
     * conflict with an already-applied enchant.
     *
     * @param playerId  the player to update
     * @param bookIndex 0-based index into the player's book list
     * @return the book that was applied
     * @throws IndexOutOfBoundsException if {@code bookIndex} is out of range
     * @throws IllegalArgumentException  if the book's enchant conflicts with an applied one
     */
    public EnchantmentBook applyBook(UUID playerId, int bookIndex) {
        Objects.requireNonNull(playerId, "playerId");
        List<EnchantmentBook> books = playerBooks.get(playerId);
        if (books == null || bookIndex < 0 || bookIndex >= books.size()) {
            throw new IndexOutOfBoundsException("No book at index " + bookIndex);
        }
        EnchantmentBook book = books.get(bookIndex);
        // Apply first: if this conflicts or is out of range it throws and the book is kept.
        setEnchantment(playerId, book.enchantment(), book.level());
        books.remove(bookIndex);
        if (books.isEmpty()) {
            playerBooks.remove(playerId);
        }
        return book;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "enchanting.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        enchantingLevels.clear();
        playerEnchantments.clear();
        enchantingHistory.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int skillLevel = cfg.getInt(key + ".level", 0);
                if (skillLevel >= 1) {
                    enchantingLevels.put(uuid, skillLevel);
                }
                if (cfg.isConfigurationSection(key + ".enchantments")) {
                    Map<SkyBlockEnchantment, Integer> enchants = new EnumMap<>(SkyBlockEnchantment.class);
                    for (SkyBlockEnchantment type : SkyBlockEnchantment.values()) {
                        int level = cfg.getInt(key + ".enchantments." + type.name(), 0);
                        if (level > 0) {
                            enchants.put(type, level);
                        }
                    }
                    if (!enchants.isEmpty()) {
                        playerEnchantments.put(uuid, enchants);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
        if (cfg.isConfigurationSection("enchantingHistory")) {
            for (String key : cfg.getConfigurationSection("enchantingHistory").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    List<String> entries = cfg.getStringList("enchantingHistory." + key);
                    if (!entries.isEmpty()) {
                        enchantingHistory.put(uuid, new ArrayList<>(entries));
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "enchanting.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : enchantingLevels.entrySet()) {
            cfg.set(entry.getKey().toString() + ".level", entry.getValue());
        }
        for (Map.Entry<UUID, Map<SkyBlockEnchantment, Integer>> entry : playerEnchantments.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<SkyBlockEnchantment, Integer> e : entry.getValue().entrySet()) {
                cfg.set(key + ".enchantments." + e.getKey().name(), e.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : enchantingHistory.entrySet()) {
            cfg.set("enchantingHistory." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save enchanting.yml", e);
        }
    }
}
