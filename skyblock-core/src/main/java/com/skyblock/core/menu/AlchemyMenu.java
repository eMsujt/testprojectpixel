package com.skyblock.core.menu;

import com.skyblock.core.manager.AlchemyManager;
import com.skyblock.core.manager.AlchemyManager.BrewJob;
import com.skyblock.core.manager.AlchemyManager.PotionType;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 6-row chest GUI titled '§dAlchemy'. Shows the player's alchemy level,
 * XP and active brew status at slot 4, and one tile per {@link PotionType}
 * across the interior rows.
 */
public final class AlchemyMenu extends AbstractSkyBlockMenu {

    private static final String TITLE       = "§dAlchemy";
    private static final int    SUMMARY_SLOT = 4;

    private static final int[] RECIPE_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    public AlchemyMenu(Player player) {
        super(player, TITLE, 6);
    }

    @Override
    protected void populate() {
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
            long elapsed    = (System.currentTimeMillis() - job.getStartTimeMillis()) / 1000L;
            long remaining  = Math.max(0, job.getRecipe().getDurationSeconds() - elapsed);
            summaryLore.add("§7Brewing: §f" + job.getRecipe().getDisplayName());
            summaryLore.add("§7Remaining: §e" + remaining + "s");
        }
        setItem(SUMMARY_SLOT, new ItemBuilder(Material.BREWING_STAND)
                .displayName("§a" + player.getName() + "'s Alchemy")
                .lore(summaryLore)
                .build(),
                e -> e.setCancelled(true));

        PotionType[] types = PotionType.values();
        for (int i = 0; i < types.length && i < RECIPE_SLOTS.length; i++) {
            PotionType type = types[i];
            List<String> lore = new ArrayList<>();
            lore.add("§7Type: §d" + type.getDisplayName());
            setItem(RECIPE_SLOTS[i], new ItemBuilder(Material.POTION)
                    .displayName("§d" + type.getDisplayName())
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
