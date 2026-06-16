package com.skyblock.core.crafting;

import com.skyblock.core.crafting.manager.CraftingManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.Map;

/**
 * Bukkit listener that records each SkyBlock recipe a player crafts into
 * {@link CraftingManager}'s per-player craft history.
 *
 * <p>Matches the crafted item's result material against registered
 * {@link CraftingManager} recipe entries and calls
 * {@link CraftingManager#recordCraft} on the first match.</p>
 */
public final class CraftingListener implements Listener {

    private final CraftingManager craftingManager;

    /**
     * Creates a listener backed by the given {@link CraftingManager}.
     *
     * @param craftingManager the manager; must not be null
     */
    public CraftingListener(CraftingManager craftingManager) {
        if (craftingManager == null) {
            throw new IllegalArgumentException("craftingManager must not be null");
        }
        this.craftingManager = craftingManager;
    }

    /**
     * Records a craft into the player's history when they craft an item that
     * matches a registered SkyBlock recipe by result material.
     *
     * @param event the craft event
     */
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getRecipe() == null) {
            return;
        }

        Material resultMaterial = event.getRecipe().getResult().getType();
        Map<String, CraftingManager.SkyBlockRecipe> allRecipes = craftingManager.getAllRecipes();
        for (CraftingManager.SkyBlockRecipe recipe : allRecipes.values()) {
            if (recipe.result() == resultMaterial) {
                craftingManager.recordCraft(player.getUniqueId(), recipe.id());
                break;
            }
        }
    }
}
