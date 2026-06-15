package com.skyblock.plugin.gui.menus;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.CollectionsManager;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A per-category collections listing.
 *
 * <p>A 54-slot (6-row) menu showing each collection in a single category, laid out
 * left-to-right from the top-left slot. Every entry displays the player's current
 * collection count, read from {@link CollectionsManager}.</p>
 */
public class CollectionCategoryMenu extends Menu {

    private final List<String> collections;
    private final UUID playerId;

    /**
     * Creates a category listing.
     *
     * @param categoryName the category's display name (shown in the title)
     * @param collections  the collection ids to list
     * @param playerId     the viewing player, whose counts are shown
     */
    public CollectionCategoryMenu(String categoryName, List<String> collections, UUID playerId) {
        super("Collections ➜ " + categoryName, 6);
        this.collections = collections != null ? new ArrayList<>(collections) : new ArrayList<>();
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        CollectionsManager manager = CollectionsManager.getInstance();
        for (int i = 0; i < collections.size() && i < 54; i++) {
            String collection = collections.get(i);
            setItem(i, new ItemBuilder(materialFor(collection))
                    .displayName("§a" + prettify(collection))
                    .lore("§7Collected: §e" + manager.getCollectionCount(playerId, collection))
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
