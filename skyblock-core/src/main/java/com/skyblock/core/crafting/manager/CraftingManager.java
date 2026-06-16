package com.skyblock.core.crafting.manager;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Singleton registry of custom SkyBlock crafting recipes that also tracks
 * each player's crafting history (which recipe IDs they have crafted and how
 * many times).
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CraftingManager {

    // -----------------------------------------------------------------------
    // Recipe model types
    // -----------------------------------------------------------------------

    /** Common contract for all SkyBlock crafting recipes. */
    public sealed interface SkyBlockRecipe permits ShapedRecipe, ShapelessRecipe {
        /** Unique identifier for this recipe. */
        String id();
        /** The {@link Material} produced when the recipe is crafted. */
        Material result();
        /** Number of result items produced; always &ge; 1. */
        int resultAmount();
    }

    /**
     * A grid-positioned recipe requiring ingredients to be placed in a specific
     * shape inside the crafting table.
     */
    public record ShapedRecipe(
            String id,
            Material result,
            int resultAmount,
            String[] shape,
            Map<Character, Material> ingredientMap) implements SkyBlockRecipe {

        public ShapedRecipe {
            Objects.requireNonNull(id, "id");
            if (id.isBlank()) {
                throw new IllegalArgumentException("id must not be blank");
            }
            Objects.requireNonNull(result, "result");
            if (resultAmount < 1) {
                throw new IllegalArgumentException(
                        "resultAmount must be >= 1, got " + resultAmount);
            }
            Objects.requireNonNull(shape, "shape");
            if (shape.length == 0 || shape.length > 3) {
                throw new IllegalArgumentException(
                        "shape must have 1–3 rows, got " + shape.length);
            }
            for (String row : shape) {
                Objects.requireNonNull(row, "shape row must not be null");
                if (row.isEmpty() || row.length() > 3) {
                    throw new IllegalArgumentException(
                            "each shape row must be 1–3 characters, got \"" + row + "\"");
                }
            }
            shape = shape.clone();
            Objects.requireNonNull(ingredientMap, "ingredientMap");
            ingredientMap = Map.copyOf(ingredientMap);
        }
    }

    /**
     * An order-independent recipe requiring a multiset of ingredients anywhere
     * in the crafting grid.
     */
    public record ShapelessRecipe(
            String id,
            Material result,
            int resultAmount,
            List<Material> ingredients) implements SkyBlockRecipe {

        public ShapelessRecipe {
            Objects.requireNonNull(id, "id");
            if (id.isBlank()) {
                throw new IllegalArgumentException("id must not be blank");
            }
            Objects.requireNonNull(result, "result");
            if (resultAmount < 1) {
                throw new IllegalArgumentException(
                        "resultAmount must be >= 1, got " + resultAmount);
            }
            Objects.requireNonNull(ingredients, "ingredients");
            if (ingredients.isEmpty()) {
                throw new IllegalArgumentException("ingredients must not be empty");
            }
            ingredients = List.copyOf(ingredients);
        }
    }

    /** Typed SkyBlock-specific enchanted crafting recipes. */
    public enum SkyblockRecipe {
        // Farming
        ENCHANTED_WHEAT("Enchanted Bread", 160),
        ENCHANTED_SUGAR_CANE("Enchanted Sugar Cane", 160),
        ENCHANTED_CARROT("Enchanted Carrot", 160),
        ENCHANTED_POTATO("Enchanted Potato", 160),
        ENCHANTED_PUMPKIN("Enchanted Pumpkin", 160),
        ENCHANTED_MELON("Enchanted Melon", 160),
        ENCHANTED_MUSHROOM("Enchanted Red Mushroom", 160),
        ENCHANTED_CACTUS("Enchanted Cactus Green", 160),
        ENCHANTED_NETHER_WART("Enchanted Nether Wart", 160),
        ENCHANTED_COCOA_BEANS("Enchanted Cookie", 160),
        // Mining
        ENCHANTED_COBBLESTONE("Enchanted Cobblestone", 160),
        ENCHANTED_COAL("Enchanted Coal", 160),
        ENCHANTED_IRON("Enchanted Iron", 160),
        ENCHANTED_GOLD("Enchanted Gold", 160),
        ENCHANTED_DIAMOND("Enchanted Diamond", 160),
        ENCHANTED_EMERALD("Enchanted Emerald", 160),
        ENCHANTED_REDSTONE("Enchanted Redstone", 160),
        ENCHANTED_LAPIS("Enchanted Lapis Lazuli", 160),
        ENCHANTED_QUARTZ("Enchanted Quartz", 160),
        ENCHANTED_OBSIDIAN("Enchanted Obsidian", 160),
        // Foraging
        ENCHANTED_OAK_LOG("Enchanted Oak Wood", 160),
        ENCHANTED_SPRUCE_LOG("Enchanted Spruce Wood", 160),
        ENCHANTED_BIRCH_LOG("Enchanted Birch Wood", 160),
        ENCHANTED_JUNGLE_LOG("Enchanted Jungle Wood", 160),
        ENCHANTED_ACACIA_LOG("Enchanted Acacia Wood", 160),
        ENCHANTED_DARK_OAK_LOG("Enchanted Dark Oak Wood", 160),
        // Combat
        ENCHANTED_ROTTEN_FLESH("Enchanted Rotten Flesh", 160),
        ENCHANTED_BONE("Enchanted Bone", 160),
        ENCHANTED_SPIDER_EYE("Enchanted Spider Eye", 160),
        ENCHANTED_STRING("Enchanted String", 160),
        ENCHANTED_GUNPOWDER("Enchanted Gunpowder", 160),
        ENCHANTED_ENDER_PEARL("Enchanted Ender Pearl", 160),
        ENCHANTED_GHAST_TEAR("Enchanted Ghast Tear", 160),
        ENCHANTED_SLIME_BALL("Enchanted Slime Ball", 160),
        ENCHANTED_BLAZE_ROD("Enchanted Blaze Rod", 160),
        ENCHANTED_MAGMA_CREAM("Enchanted Magma Cream", 160),
        // Fishing
        ENCHANTED_RAW_FISH("Enchanted Raw Fish", 160),
        ENCHANTED_RAW_SALMON("Enchanted Raw Salmon", 160),
        ENCHANTED_INK_SAC("Enchanted Ink Sac", 160);

        private final String displayName;
        private final int requiredAmount;

        SkyblockRecipe(String displayName, int requiredAmount) {
            this.displayName = displayName;
            this.requiredAmount = requiredAmount;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getRequiredAmount() {
            return requiredAmount;
        }
    }

    // -----------------------------------------------------------------------
    // Singleton
    // -----------------------------------------------------------------------

    private static final CraftingManager INSTANCE = new CraftingManager();

    public static CraftingManager getInstance() {
        return INSTANCE;
    }

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    /** Recipe catalogue keyed by recipe id. */
    private final Map<String, SkyBlockRecipe> recipes = new HashMap<>();

    /** Per-player map of recipe-id → craft count. */
    private final Map<UUID, Map<String, Integer>> craftHistory = new HashMap<>();

    private CraftingManager() {
        loadDefaultRecipes();
    }

    // -----------------------------------------------------------------------
    // Recipe registration
    // -----------------------------------------------------------------------

    public void registerShaped(String id, Material result, int resultAmount,
                                String[] shape, Map<Character, Material> ingredientMap) {
        register(new ShapedRecipe(id, result, resultAmount, shape, ingredientMap));
    }

    public void registerShapeless(String id, Material result, int resultAmount,
                                   List<Material> ingredients) {
        register(new ShapelessRecipe(id, result, resultAmount, new ArrayList<>(ingredients)));
    }

    public void registerRecipe(SkyBlockRecipe recipe) {
        Objects.requireNonNull(recipe, "recipe");
        register(recipe);
    }

    private void register(SkyBlockRecipe recipe) {
        if (recipes.containsKey(recipe.id())) {
            throw new IllegalStateException(
                    "Recipe already registered for id: " + recipe.id());
        }
        recipes.put(recipe.id(), recipe);
    }

    public boolean removeRecipe(String id) {
        Objects.requireNonNull(id, "id");
        return recipes.remove(id) != null;
    }

    // -----------------------------------------------------------------------
    // Recipe access
    // -----------------------------------------------------------------------

    public Map<String, SkyBlockRecipe> getAllRecipes() {
        return Collections.unmodifiableMap(recipes);
    }

    public Optional<SkyBlockRecipe> getRecipe(String id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(recipes.get(id));
    }

    // -----------------------------------------------------------------------
    // Slot-aware recipe matching
    // -----------------------------------------------------------------------

    /**
     * Finds the first registered recipe that matches the supplied crafting grid.
     *
     * @param grid a (up to 3&times;3) grid of materials; {@code null} cells are
     *             treated as empty
     * @return the matching recipe, or empty if none match
     */
    public Optional<SkyBlockRecipe> findMatchingRecipe(Material[][] grid) {
        Objects.requireNonNull(grid, "grid");
        for (SkyBlockRecipe recipe : recipes.values()) {
            if (matches(recipe, grid)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    /** Tests whether the supplied crafting grid satisfies the given recipe. */
    public boolean matches(SkyBlockRecipe recipe, Material[][] grid) {
        Objects.requireNonNull(recipe, "recipe");
        Objects.requireNonNull(grid, "grid");
        return switch (recipe) {
            case ShapedRecipe shaped -> matchesShaped(shaped, grid);
            case ShapelessRecipe shapeless -> matchesShapeless(shapeless, grid);
        };
    }

    private static boolean matchesShaped(ShapedRecipe recipe, Material[][] grid) {
        String[] shape = recipe.shape();
        int rCols = 0;
        for (String row : shape) {
            rCols = Math.max(rCols, row.length());
        }
        Material[][] recipeGrid = new Material[shape.length][rCols];
        for (int r = 0; r < shape.length; r++) {
            String row = shape[r];
            for (int c = 0; c < rCols; c++) {
                char ch = c < row.length() ? row.charAt(c) : ' ';
                recipeGrid[r][c] = ch == ' ' ? null : recipe.ingredientMap().get(ch);
            }
        }
        int[] rb = boundingBox(recipeGrid);
        int[] gb = boundingBox(grid);
        if (rb == null || gb == null) {
            // A recipe with at least one ingredient never matches an empty grid.
            return rb == null && gb == null;
        }
        int rh = rb[2] - rb[0] + 1, rw = rb[3] - rb[1] + 1;
        int gh = gb[2] - gb[0] + 1, gw = gb[3] - gb[1] + 1;
        if (rh != gh || rw != gw) {
            return false;
        }
        for (int r = 0; r < rh; r++) {
            for (int c = 0; c < rw; c++) {
                if (recipeGrid[rb[0] + r][rb[1] + c] != grid[gb[0] + r][gb[1] + c]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean matchesShapeless(ShapelessRecipe recipe, Material[][] grid) {
        Map<Material, Integer> need = new HashMap<>();
        for (Material m : recipe.ingredients()) {
            need.merge(m, 1, Integer::sum);
        }
        for (Material[] row : grid) {
            if (row == null) {
                continue;
            }
            for (Material m : row) {
                if (m == null) {
                    continue;
                }
                Integer remaining = need.get(m);
                if (remaining == null) {
                    return false; // extraneous ingredient
                }
                if (remaining == 1) {
                    need.remove(m);
                } else {
                    need.put(m, remaining - 1);
                }
            }
        }
        return need.isEmpty();
    }

    /**
     * Returns the inclusive bounding box {@code {minRow, minCol, maxRow, maxCol}}
     * of the non-empty cells of {@code grid}, or {@code null} if every cell is empty.
     */
    private static int[] boundingBox(Material[][] grid) {
        int minRow = Integer.MAX_VALUE, minCol = Integer.MAX_VALUE;
        int maxRow = Integer.MIN_VALUE, maxCol = Integer.MIN_VALUE;
        for (int r = 0; r < grid.length; r++) {
            Material[] row = grid[r];
            if (row == null) {
                continue;
            }
            for (int c = 0; c < row.length; c++) {
                if (row[c] != null) {
                    minRow = Math.min(minRow, r);
                    minCol = Math.min(minCol, c);
                    maxRow = Math.max(maxRow, r);
                    maxCol = Math.max(maxCol, c);
                }
            }
        }
        return maxRow == Integer.MIN_VALUE ? null : new int[]{minRow, minCol, maxRow, maxCol};
    }

    // -----------------------------------------------------------------------
    // Crafting history
    // -----------------------------------------------------------------------

    public void recordCraft(UUID playerId, String recipeId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(recipeId, "recipeId");
        if (getRecipe(recipeId).isEmpty()) {
            throw new IllegalArgumentException("Unknown recipe: " + recipeId);
        }
        craftHistory.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(recipeId, 1, Integer::sum);
    }

    public int getCraftCount(UUID playerId, String recipeId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(recipeId, "recipeId");
        Map<String, Integer> history = craftHistory.get(playerId);
        return history == null ? 0 : history.getOrDefault(recipeId, 0);
    }

    public Map<String, Integer> getCraftHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, Integer> history = craftHistory.get(playerId);
        return history == null ? Collections.emptyMap() : Collections.unmodifiableMap(history);
    }

    public boolean resetHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return craftHistory.remove(playerId) != null;
    }

    // -----------------------------------------------------------------------
    // Persistence
    // -----------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "crafting.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        craftHistory.clear();
        for (String uuidStr : cfg.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(uuidStr);
                Map<String, Integer> history = new HashMap<>();
                for (String recipeId : cfg.getConfigurationSection(uuidStr).getKeys(false)) {
                    history.put(recipeId, cfg.getInt(uuidStr + "." + recipeId, 0));
                }
                if (!history.isEmpty()) {
                    craftHistory.put(playerId, history);
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "crafting.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Integer>> playerEntry : craftHistory.entrySet()) {
            String uuidStr = playerEntry.getKey().toString();
            for (Map.Entry<String, Integer> recipeEntry : playerEntry.getValue().entrySet()) {
                if (recipeEntry.getValue() > 0) {
                    cfg.set(uuidStr + "." + recipeEntry.getKey(), recipeEntry.getValue());
                }
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save crafting.yml", e);
        }
    }

    // -----------------------------------------------------------------------
    // Bukkit recipe registration
    // -----------------------------------------------------------------------

    public void registerRecipes(JavaPlugin plugin) {
        registerBukkitShaped(plugin, "enchanted_cobblestone", Material.COBBLESTONE);
        registerBukkitShaped(plugin, "enchanted_oak_log", Material.OAK_LOG);
        registerBukkitShaped(plugin, "enchanted_iron_ingot", Material.IRON_INGOT);
        registerBukkitShaped(plugin, "enchanted_gold_ingot", Material.GOLD_INGOT);
        registerBukkitShaped(plugin, "enchanted_diamond", Material.DIAMOND);
    }

    private void registerBukkitShaped(JavaPlugin plugin, String id, Material material) {
        NamespacedKey key = new NamespacedKey(plugin, id);
        org.bukkit.inventory.ShapedRecipe recipe =
                new org.bukkit.inventory.ShapedRecipe(key, new ItemStack(material, 1));
        recipe.shape("AAA", "AAA", "AAA");
        recipe.setIngredient('A', material);
        plugin.getServer().addRecipe(recipe);
    }

    // -----------------------------------------------------------------------
    // Default recipe catalogue
    // -----------------------------------------------------------------------

    private void loadDefaultRecipes() {
        // Shaped recipes
        registerShaped("enchanted_iron_sword", Material.IRON_SWORD, 1,
                new String[]{"I", "I", "S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        registerShaped("iron_helmet", Material.IRON_HELMET, 1,
                new String[]{"III", "I I"},
                Map.of('I', Material.IRON_INGOT));

        registerShaped("iron_chestplate", Material.IRON_CHESTPLATE, 1,
                new String[]{"I I", "III", "III"},
                Map.of('I', Material.IRON_INGOT));

        registerShaped("iron_leggings", Material.IRON_LEGGINGS, 1,
                new String[]{"III", "I I", "I I"},
                Map.of('I', Material.IRON_INGOT));

        registerShaped("iron_boots", Material.IRON_BOOTS, 1,
                new String[]{"I I", "I I"},
                Map.of('I', Material.IRON_INGOT));

        registerShaped("iron_pickaxe", Material.IRON_PICKAXE, 1,
                new String[]{"III", " S ", " S "},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        registerShaped("iron_axe", Material.IRON_AXE, 1,
                new String[]{"II", "IS", " S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        registerShaped("iron_shovel", Material.IRON_SHOVEL, 1,
                new String[]{"I", "S", "S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        registerShaped("iron_hoe", Material.IRON_HOE, 1,
                new String[]{"II", " S", " S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        registerShaped("chest", Material.CHEST, 1,
                new String[]{"WWW", "W W", "WWW"},
                Map.of('W', Material.OAK_PLANKS));

        // SkyBlock-specific shaped recipes — diamond gear
        registerShaped("diamond_sword", Material.DIAMOND_SWORD, 1,
                new String[]{"D", "D", "S"},
                Map.of('D', Material.DIAMOND, 'S', Material.STICK));

        registerShaped("diamond_helmet", Material.DIAMOND_HELMET, 1,
                new String[]{"DDD", "D D"},
                Map.of('D', Material.DIAMOND));

        registerShaped("diamond_chestplate", Material.DIAMOND_CHESTPLATE, 1,
                new String[]{"D D", "DDD", "DDD"},
                Map.of('D', Material.DIAMOND));

        registerShaped("diamond_leggings", Material.DIAMOND_LEGGINGS, 1,
                new String[]{"DDD", "D D", "D D"},
                Map.of('D', Material.DIAMOND));

        registerShaped("diamond_boots", Material.DIAMOND_BOOTS, 1,
                new String[]{"D D", "D D"},
                Map.of('D', Material.DIAMOND));

        // SkyBlock-specific shaped recipes — diamond tools
        registerShaped("diamond_pickaxe", Material.DIAMOND_PICKAXE, 1,
                new String[]{"DDD", " S ", " S "},
                Map.of('D', Material.DIAMOND, 'S', Material.STICK));

        registerShaped("diamond_axe", Material.DIAMOND_AXE, 1,
                new String[]{"DD", "DS", " S"},
                Map.of('D', Material.DIAMOND, 'S', Material.STICK));

        registerShaped("diamond_shovel", Material.DIAMOND_SHOVEL, 1,
                new String[]{"D", "S", "S"},
                Map.of('D', Material.DIAMOND, 'S', Material.STICK));

        registerShaped("diamond_hoe", Material.DIAMOND_HOE, 1,
                new String[]{"DD", " S", " S"},
                Map.of('D', Material.DIAMOND, 'S', Material.STICK));

        // SkyBlock-specific shaped recipes — utility blocks
        registerShaped("enchanting_table", Material.ENCHANTING_TABLE, 1,
                new String[]{" B ", "DOD", "OOO"},
                Map.of('B', Material.BOOK, 'D', Material.DIAMOND, 'O', Material.OBSIDIAN));

        registerShaped("ender_chest", Material.ENDER_CHEST, 1,
                new String[]{"OOO", "OEO", "OOO"},
                Map.of('O', Material.OBSIDIAN, 'E', Material.ENDER_EYE));

        registerShaped("anvil", Material.ANVIL, 1,
                new String[]{"III", " i ", "iii"},
                Map.of('I', Material.IRON_BLOCK, 'i', Material.IRON_INGOT));

        registerShaped("brewing_stand", Material.BREWING_STAND, 1,
                new String[]{" B ", "CCC"},
                Map.of('B', Material.BLAZE_ROD, 'C', Material.COBBLESTONE));

        registerShaped("beacon", Material.BEACON, 1,
                new String[]{"GGG", "GSG", "OOO"},
                Map.of('G', Material.GLASS, 'S', Material.NETHER_STAR, 'O', Material.OBSIDIAN));

        // SkyBlock-specific shaped recipes — compressed resource blocks
        registerShaped("enchanted_iron_block", Material.IRON_BLOCK, 1,
                new String[]{"III", "III", "III"},
                Map.of('I', Material.IRON_INGOT));

        registerShaped("enchanted_gold_block", Material.GOLD_BLOCK, 1,
                new String[]{"GGG", "GGG", "GGG"},
                Map.of('G', Material.GOLD_INGOT));

        registerShaped("enchanted_diamond_block", Material.DIAMOND_BLOCK, 1,
                new String[]{"DDD", "DDD", "DDD"},
                Map.of('D', Material.DIAMOND));

        registerShaped("enchanted_lapis_block", Material.LAPIS_BLOCK, 1,
                new String[]{"LLL", "LLL", "LLL"},
                Map.of('L', Material.LAPIS_LAZULI));

        registerShaped("enchanted_emerald_block", Material.EMERALD_BLOCK, 1,
                new String[]{"EEE", "EEE", "EEE"},
                Map.of('E', Material.EMERALD));

        // Shapeless recipes
        registerShapeless("torch_x4", Material.TORCH, 4,
                List.of(Material.COAL, Material.STICK));

        registerShapeless("wooden_planks", Material.OAK_PLANKS, 4,
                List.of(Material.OAK_LOG));

        registerShapeless("paper_x3", Material.PAPER, 3,
                new ArrayList<>(List.of(Material.SUGAR_CANE, Material.SUGAR_CANE, Material.SUGAR_CANE)));

        registerShapeless("book", Material.BOOK, 1,
                List.of(Material.PAPER, Material.PAPER, Material.PAPER, Material.LEATHER));

        registerShapeless("glass_bottle", Material.GLASS_BOTTLE, 3,
                new ArrayList<>(List.of(Material.GLASS, Material.GLASS, Material.GLASS)));
    }
}
