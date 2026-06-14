package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.CollectionsManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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

    /** All border slots (top row, bottom row, left/right edges of middle rows). */
    private static final int[] BORDER_SLOTS = {
        0,  1,  2,  3,  4,  5,  6,  7,  8,
        9,                                 17,
        18,                                26,
        27,                                35,
        36,                                44,
        45, 46, 47, 48, 49, 50, 51, 52, 53
    };

    /** Interior slots (rows 2-5, columns 1-7), one per unlocked recipe. */
    private static final int[] SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    private final UUID playerId;

    /**
     * Creates a recipe book listing.
     *
     * @param playerId the viewing player, whose unlocked recipes are shown
     */
    public RecipeBookMenu(UUID playerId) {
        super("§6Recipe Book", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot : BORDER_SLOTS) {
            setItem(slot, pane);
        }

        CollectionsManager manager = CollectionsManager.getInstance();
        List<Map.Entry<String, Integer>> unlocked = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : manager.getCollectionMilestones(playerId).entrySet()) {
            if (entry.getValue() != null && entry.getValue() >= 1) {
                unlocked.add(entry);
            }
        }
        unlocked.sort((a, b) -> a.getKey().compareTo(b.getKey()));
        for (int i = 0; i < unlocked.size() && i < SLOTS.length; i++) {
            Map.Entry<String, Integer> entry = unlocked.get(i);
            setItem(SLOTS[i], new ItemBuilder(materialFor(entry.getKey()))
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
