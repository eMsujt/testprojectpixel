package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.CollectionsManager;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The Recipe Book menu.
 *
 * <p>A 54-slot (6-row) menu listing the recipes the player has unlocked. Recipes are
 * gated behind collection tiers, so this iterates the player's collection milestones in
 * {@link CollectionsManager}: every collection in which the player has reached at least
 * tier 1 contributes one unlocked-recipe entry, shown with the tier reached.</p>
 */
public class RecipeBookMenu extends Menu {

    private final UUID playerId;

    /**
     * Creates a recipe book listing.
     *
     * @param playerId the viewing player, whose unlocked recipes are shown
     */
    public RecipeBookMenu(UUID playerId) {
        super("Recipe Book", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        CollectionsManager manager = CollectionsManager.getInstance();
        List<Map.Entry<String, Integer>> unlocked = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : manager.getCollectionMilestones(playerId).entrySet()) {
            if (entry.getValue() != null && entry.getValue() >= 1) {
                unlocked.add(entry);
            }
        }
        unlocked.sort((a, b) -> a.getKey().compareTo(b.getKey()));
        for (int i = 0; i < unlocked.size() && i < 54; i++) {
            Map.Entry<String, Integer> entry = unlocked.get(i);
            setItem(i, new ItemBuilder(materialFor(entry.getKey()))
                    .displayName("§a" + prettify(entry.getKey()))
                    .lore("§7Unlocked at tier §e" + entry.getValue())
                    .build());
        }
    }

    /** Resolves a collection id to a representative material, defaulting to paper. */
    private static Material materialFor(String collection) {
        Material material = Material.matchMaterial(collection);
        return material != null ? material : Material.PAPER;
    }

    /** Turns a collection id such as {@code "iron_ingot"} into {@code "Iron Ingot"}. */
    private static String prettify(String collection) {
        String[] words = collection.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return sb.toString();
    }
}
