package com.skyblock.core.crafting;

import com.skyblock.core.SkyBlockPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Singleton that loads custom SkyBlock recipes from {@code custom-recipes.yml}
 * and registers them into {@link SkyBlockRecipeManager}.
 *
 * <p>Call {@link #init(SkyBlockPlugin)} once during {@code onEnable} after
 * {@link SkyBlockRecipeManager} has been initialised.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class SkyBlockCraftingManager {

    private static final SkyBlockCraftingManager INSTANCE = new SkyBlockCraftingManager();

    private SkyBlockCraftingManager() {
    }

    public static SkyBlockCraftingManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code custom-recipes.yml} from the plugin jar and registers every
     * recipe it contains into {@link SkyBlockRecipeManager}.
     *
     * @param plugin the active plugin instance used to read the resource
     * @throws IllegalStateException if {@code custom-recipes.yml} is missing
     */
    public void init(SkyBlockPlugin plugin) {
        InputStream stream = plugin.getResource("custom-recipes.yml");
        if (stream == null) {
            throw new IllegalStateException("custom-recipes.yml not found in plugin resources");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8));
        ConfigurationSection section = config.getConfigurationSection("recipes");
        if (section == null) {
            plugin.getLogger().warning("custom-recipes.yml contains no 'recipes' section");
            return;
        }

        SkyBlockRecipeManager recipeManager = SkyBlockRecipeManager.getInstance();
        int count = 0;
        for (String id : section.getKeys(false)) {
            ConfigurationSection recipe = section.getConfigurationSection(id);
            Objects.requireNonNull(recipe, "null section for recipe: " + id);
            String type = recipe.getString("type", "shaped").toLowerCase();
            Material result = Material.valueOf(
                    recipe.getString("result", "AIR").toUpperCase());
            int resultAmount = recipe.getInt("resultAmount", 1);

            if ("shapeless".equals(type)) {
                List<String> rawIngredients = recipe.getStringList("ingredients");
                List<Material> ingredients = new ArrayList<>(rawIngredients.size());
                for (String mat : rawIngredients) {
                    ingredients.add(Material.valueOf(mat.toUpperCase()));
                }
                recipeManager.registerShapeless(id, result, resultAmount, ingredients);
            } else {
                List<String> shapeList = recipe.getStringList("shape");
                String[] shape = shapeList.toArray(new String[0]);
                ConfigurationSection ingredientSection =
                        recipe.getConfigurationSection("ingredientMap");
                Map<Character, Material> ingredientMap = new HashMap<>();
                if (ingredientSection != null) {
                    for (String key : ingredientSection.getKeys(false)) {
                        ingredientMap.put(key.charAt(0),
                                Material.valueOf(ingredientSection.getString(key).toUpperCase()));
                    }
                }
                recipeManager.registerShaped(id, result, resultAmount, shape, ingredientMap);
            }
            count++;
        }
        plugin.getLogger().info("Loaded " + count + " custom recipe(s) from custom-recipes.yml");
    }
}
