package com.skyblock.core.menu;

import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.core.alchemy.AlchemyManager.BrewJob;
import com.skyblock.core.alchemy.AlchemyManager.PotionRecipe;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Canonical "Alchemy" menu. A 54-slot (6-row) chest GUI framed by a
 * {@code GRAY_STAINED_GLASS_PANE} border with a brewing stand at slot 4
 * summarising the viewing player's alchemy level, XP and active brew status
 * (from {@link AlchemyManager}), and one potion tile per {@link PotionRecipe}
 * showing its ingredients, output, brew time and XP reward.
 */
public final class AlchemyMenu extends Menu {

    private static final String TITLE = "§aAlchemy";
    private static final int SUMMARY_SLOT = 4;

    /** Recipe tiles laid out across the third and fourth interior rows. */
    private static final int[] RECIPE_SLOTS = {
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private final Player player;

    public AlchemyMenu(Player player) {
        super(TITLE, 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        UUID id = player.getUniqueId();
        AlchemyManager alchemy = AlchemyManager.getInstance();

        List<String> summaryLore = new ArrayList<>();
        summaryLore.add("§7Level: §e" + alchemy.getLevel(id));
        summaryLore.add("§7Total XP: §e" + String.format("%,.0f", alchemy.getXp(id)));
        summaryLore.add("");
        BrewJob job = alchemy.getActiveJob(id);
        if (job == null) {
            summaryLore.add("§7No active brew.");
        } else if (job.isComplete(System.currentTimeMillis())) {
            summaryLore.add("§aReady: §f" + job.getRecipe().getDisplayName());
        } else {
            long elapsed = (System.currentTimeMillis() - job.getStartTimeMillis()) / 1000L;
            long remaining = Math.max(0, job.getRecipe().getDurationSeconds() - elapsed);
            summaryLore.add("§7Brewing: §f" + job.getRecipe().getDisplayName());
            summaryLore.add("§7Remaining: §e" + remaining + "s");
        }
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.BREWING_STAND)
                .displayName("§a" + player.getName() + "'s Alchemy")
                .lore(summaryLore)
                .build(),
                e -> e.setCancelled(true));

        List<PotionRecipe> recipes = alchemy.getRecipes().values().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .collect(Collectors.toList());
        for (int i = 0; i < recipes.size() && i < RECIPE_SLOTS.length; i++) {
            PotionRecipe recipe = recipes.get(i);
            List<String> lore = new ArrayList<>();
            lore.add("§7Output: §a" + recipe.getOutputAmount() + "x " + recipe.getOutputPotion());
            lore.add("§7Brew time: §e" + recipe.getDurationSeconds() + "s");
            lore.add("§7XP reward: §e" + String.format("%,.0f", recipe.getXpReward()));
            lore.add("");
            lore.add("§7Ingredients:");
            for (Map.Entry<String, Integer> ingredient : recipe.getIngredients().entrySet()) {
                lore.add("§8 • §f" + ingredient.getValue() + "x " + ingredient.getKey());
            }
            setItem(RECIPE_SLOTS[i], new ItemBuilder(Material.POTION)
                    .displayName("§a" + recipe.getDisplayName())
                    .lore(lore)
                    .build(),
                    e -> e.setCancelled(true));
        }
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }
    }
}
