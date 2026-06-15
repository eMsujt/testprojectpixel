package com.skyblock.core.crafting;

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
 * Singleton facade over {@link SkyBlockRecipeManager} that also tracks each
 * player's crafting history (which recipe IDs they have crafted and how many
 * times).
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CraftingManager {

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

    private static final CraftingManager INSTANCE = new CraftingManager();

    private final SkyBlockRecipeManager recipeManager = SkyBlockRecipeManager.getInstance();

    /** Per-player map of recipe-id → craft count. */
    private final Map<UUID, Map<String, Integer>> craftHistory = new HashMap<>();

    private CraftingManager() {
        loadDefaultRecipes();
    }

    /**
     * Returns the single shared {@code CraftingManager} instance.
     *
     * @return the singleton instance
     */
    public static CraftingManager getInstance() {
        return INSTANCE;
    }

    // ---------------------------------------------------------------------------
    // Recipe access
    // ---------------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of all registered recipes, keyed by id.
     *
     * @return all recipes
     */
    public Map<String, SkyBlockRecipeManager.SkyBlockRecipe> getAllRecipes() {
        return recipeManager.getAllRecipes();
    }

    /**
     * Returns the recipe registered under the given id, if any.
     *
     * @param id the recipe's unique id
     * @return the registered recipe, or empty
     */
    public Optional<SkyBlockRecipeManager.SkyBlockRecipe> getRecipe(String id) {
        Objects.requireNonNull(id, "id");
        return recipeManager.getRecipe(id);
    }

    // ---------------------------------------------------------------------------
    // Crafting history
    // ---------------------------------------------------------------------------

    /**
     * Records that the player crafted the given recipe once.
     *
     * @param playerId the player who crafted
     * @param recipeId the recipe that was crafted
     * @throws IllegalArgumentException if the recipe id is not registered
     */
    public void recordCraft(UUID playerId, String recipeId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(recipeId, "recipeId");
        if (recipeManager.getRecipe(recipeId).isEmpty()) {
            throw new IllegalArgumentException("Unknown recipe: " + recipeId);
        }
        craftHistory.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(recipeId, 1, Integer::sum);
    }

    /**
     * Returns how many times the player has crafted the given recipe.
     *
     * @param playerId the player to look up
     * @param recipeId the recipe id to check
     * @return craft count, 0 if never crafted
     */
    public int getCraftCount(UUID playerId, String recipeId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(recipeId, "recipeId");
        Map<String, Integer> history = craftHistory.get(playerId);
        return history == null ? 0 : history.getOrDefault(recipeId, 0);
    }

    /**
     * Returns an unmodifiable view of the player's crafting history
     * (recipe-id → craft count).
     *
     * @param playerId the player to look up
     * @return crafting history, empty map if none
     */
    public Map<String, Integer> getCraftHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, Integer> history = craftHistory.get(playerId);
        return history == null ? Collections.emptyMap() : Collections.unmodifiableMap(history);
    }

    /**
     * Removes all crafting history for the given player.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any history, {@code false} otherwise
     */
    public boolean resetHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return craftHistory.remove(playerId) != null;
    }

    // ---------------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------------

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

    // ---------------------------------------------------------------------------
    // Bukkit recipe registration
    // ---------------------------------------------------------------------------

    /**
     * Registers SkyBlock-specific enchanted crafting recipes into the Bukkit server.
     *
     * @param plugin the owning plugin used to create {@link NamespacedKey}s
     */
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

    // ---------------------------------------------------------------------------
    // Default recipe registration
    // ---------------------------------------------------------------------------

    private void loadDefaultRecipes() {
        // Shaped recipes
        recipeManager.registerShaped("enchanted_iron_sword", Material.IRON_SWORD, 1,
                new String[]{"I", "I", "S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        recipeManager.registerShaped("iron_helmet", Material.IRON_HELMET, 1,
                new String[]{"III", "I I"},
                Map.of('I', Material.IRON_INGOT));

        recipeManager.registerShaped("iron_chestplate", Material.IRON_CHESTPLATE, 1,
                new String[]{"I I", "III", "III"},
                Map.of('I', Material.IRON_INGOT));

        recipeManager.registerShaped("iron_leggings", Material.IRON_LEGGINGS, 1,
                new String[]{"III", "I I", "I I"},
                Map.of('I', Material.IRON_INGOT));

        recipeManager.registerShaped("iron_boots", Material.IRON_BOOTS, 1,
                new String[]{"I I", "I I"},
                Map.of('I', Material.IRON_INGOT));

        recipeManager.registerShaped("iron_pickaxe", Material.IRON_PICKAXE, 1,
                new String[]{"III", " S ", " S "},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        recipeManager.registerShaped("iron_axe", Material.IRON_AXE, 1,
                new String[]{"II", "IS", " S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        recipeManager.registerShaped("iron_shovel", Material.IRON_SHOVEL, 1,
                new String[]{"I", "S", "S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        recipeManager.registerShaped("iron_hoe", Material.IRON_HOE, 1,
                new String[]{"II", " S", " S"},
                Map.of('I', Material.IRON_INGOT, 'S', Material.STICK));

        recipeManager.registerShaped("chest", Material.CHEST, 1,
                new String[]{"WWW", "W W", "WWW"},
                Map.of('W', Material.OAK_PLANKS));

        // SkyBlock-specific shaped recipes — diamond gear
        recipeManager.registerShaped("diamond_sword", Material.DIAMOND_SWORD, 1,
                new String[]{"D", "D", "S"},
                Map.of('D', Material.DIAMOND, 'S', Material.STICK));

        recipeManager.registerShaped("diamond_helmet", Material.DIAMOND_HELMET, 1,
                new String[]{"DDD", "D D"},
                Map.of('D', Material.DIAMOND));

        recipeManager.registerShaped("diamond_chestplate", Material.DIAMOND_CHESTPLATE, 1,
                new String[]{"D D", "DDD", "DDD"},
                Map.of('D', Material.DIAMOND));

        recipeManager.registerShaped("diamond_leggings", Material.DIAMOND_LEGGINGS, 1,
                new String[]{"DDD", "D D", "D D"},
                Map.of('D', Material.DIAMOND));

        recipeManager.registerShaped("diamond_boots", Material.DIAMOND_BOOTS, 1,
                new String[]{"D D", "D D"},
                Map.of('D', Material.DIAMOND));

        // SkyBlock-specific shaped recipes — diamond tools
        recipeManager.registerShaped("diamond_pickaxe", Material.DIAMOND_PICKAXE, 1,
                new String[]{"DDD", " S ", " S "},
                Map.of('D', Material.DIAMOND, 'S', Material.STICK));

        recipeManager.registerShaped("diamond_axe", Material.DIAMOND_AXE, 1,
                new String[]{"DD", "DS", " S"},
                Map.of('D', Material.DIAMOND, 'S', Material.STICK));

        recipeManager.registerShaped("diamond_shovel", Material.DIAMOND_SHOVEL, 1,
                new String[]{"D", "S", "S"},
                Map.of('D', Material.DIAMOND, 'S', Material.STICK));

        recipeManager.registerShaped("diamond_hoe", Material.DIAMOND_HOE, 1,
                new String[]{"DD", " S", " S"},
                Map.of('D', Material.DIAMOND, 'S', Material.STICK));

        // SkyBlock-specific shaped recipes — utility blocks
        recipeManager.registerShaped("enchanting_table", Material.ENCHANTING_TABLE, 1,
                new String[]{" B ", "DOD", "OOO"},
                Map.of('B', Material.BOOK, 'D', Material.DIAMOND, 'O', Material.OBSIDIAN));

        recipeManager.registerShaped("ender_chest", Material.ENDER_CHEST, 1,
                new String[]{"OOO", "OEO", "OOO"},
                Map.of('O', Material.OBSIDIAN, 'E', Material.ENDER_EYE));

        recipeManager.registerShaped("anvil", Material.ANVIL, 1,
                new String[]{"III", " i ", "iii"},
                Map.of('I', Material.IRON_BLOCK, 'i', Material.IRON_INGOT));

        recipeManager.registerShaped("brewing_stand", Material.BREWING_STAND, 1,
                new String[]{" B ", "CCC"},
                Map.of('B', Material.BLAZE_ROD, 'C', Material.COBBLESTONE));

        recipeManager.registerShaped("beacon", Material.BEACON, 1,
                new String[]{"GGG", "GSG", "OOO"},
                Map.of('G', Material.GLASS, 'S', Material.NETHER_STAR, 'O', Material.OBSIDIAN));

        // SkyBlock-specific shaped recipes — compressed resource blocks
        recipeManager.registerShaped("enchanted_iron_block", Material.IRON_BLOCK, 1,
                new String[]{"III", "III", "III"},
                Map.of('I', Material.IRON_INGOT));

        recipeManager.registerShaped("enchanted_gold_block", Material.GOLD_BLOCK, 1,
                new String[]{"GGG", "GGG", "GGG"},
                Map.of('G', Material.GOLD_INGOT));

        recipeManager.registerShaped("enchanted_diamond_block", Material.DIAMOND_BLOCK, 1,
                new String[]{"DDD", "DDD", "DDD"},
                Map.of('D', Material.DIAMOND));

        recipeManager.registerShaped("enchanted_lapis_block", Material.LAPIS_BLOCK, 1,
                new String[]{"LLL", "LLL", "LLL"},
                Map.of('L', Material.LAPIS_LAZULI));

        recipeManager.registerShaped("enchanted_emerald_block", Material.EMERALD_BLOCK, 1,
                new String[]{"EEE", "EEE", "EEE"},
                Map.of('E', Material.EMERALD));

        // Shapeless recipes
        recipeManager.registerShapeless("torch_x4", Material.TORCH, 4,
                List.of(Material.COAL, Material.STICK));

        recipeManager.registerShapeless("wooden_planks", Material.OAK_PLANKS, 4,
                List.of(Material.OAK_LOG));

        recipeManager.registerShapeless("paper_x3", Material.PAPER, 3,
                new ArrayList<>(List.of(Material.SUGAR_CANE, Material.SUGAR_CANE, Material.SUGAR_CANE)));

        recipeManager.registerShapeless("book", Material.BOOK, 1,
                List.of(Material.PAPER, Material.PAPER, Material.PAPER, Material.LEATHER));

        recipeManager.registerShapeless("glass_bottle", Material.GLASS_BOTTLE, 3,
                new ArrayList<>(List.of(Material.GLASS, Material.GLASS, Material.GLASS)));
    }
}
