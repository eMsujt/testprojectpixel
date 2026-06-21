package com.skyblock.core.crafting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Real Hypixel crafting recipes, loaded from {@code /item_recipes.properties} and keyed by NEU
 * internal name. Each value is the 3x3 grid (A1,A2,A3,B1,B2,B3,C1,C2,C3) joined by '|', where each
 * cell is {@code "ID:count"} or empty. Sourced from the NotEnoughUpdates community repo.
 */
public final class RecipeData {

    private static final Properties RECIPES = load("/item_recipes.properties");
    private static final List<String> IDS =
            Collections.unmodifiableList(new ArrayList<>(new TreeSet<>(RECIPES.stringPropertyNames())));

    private RecipeData() {}

    private static Properties load(String path) {
        Properties p = new Properties();
        try (InputStream in = RecipeData.class.getResourceAsStream(path)) {
            if (in != null) p.load(in);
        } catch (Exception ignored) {
        }
        return p;
    }

    /** A single recipe ingredient. */
    public record Ingredient(String id, int count) {}

    /** {@code true} if a crafting recipe is registered for this item id. */
    public static boolean has(String internalName) {
        return internalName != null && RECIPES.containsKey(internalName);
    }

    /** Every item id that has a recipe, sorted. */
    public static List<String> ids() {
        return IDS;
    }

    /**
     * The 9 grid cells (A1..C3) for an item, or {@code null} if it has no recipe. Empty cells are
     * {@code null} entries; filled cells are {@link Ingredient}s.
     */
    public static Ingredient[] grid(String internalName) {
        if (internalName == null) return null;
        String value = RECIPES.getProperty(internalName);
        if (value == null) return null;
        String[] cells = value.split("\\|", -1);
        Ingredient[] grid = new Ingredient[9];
        for (int i = 0; i < 9; i++) {
            grid[i] = i < cells.length ? parse(cells[i]) : null;
        }
        return grid;
    }

    private static Ingredient parse(String cell) {
        if (cell == null || cell.isEmpty()) return null;
        int sep = cell.lastIndexOf(':');
        if (sep < 0) return new Ingredient(cell, 1);
        String id = cell.substring(0, sep);
        int count;
        try {
            count = Integer.parseInt(cell.substring(sep + 1));
        } catch (NumberFormatException e) {
            count = 1;
        }
        return new Ingredient(id, Math.max(1, count));
    }
}
