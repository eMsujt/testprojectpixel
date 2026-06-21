package com.skyblock.core.menu;

import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.manager.ForgeManager.ForgeJob;
import com.skyblock.core.manager.ForgeManager.ForgeRecipe;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 6-row GUI titled {@code §6Forge}. Shows the player's active forge slots
 * (top section) and the full recipe catalogue (body).
 */
public final class ForgeMenu extends AbstractSkyBlockMenu {

    /** Row-1 display slots for active forge jobs (5 visible forge slots). */
    private static final int[] FORGE_SLOTS = {10, 11, 12, 13, 14};

    /** Body slots used to list the available recipes. */
    private static final int[] RECIPE_SLOTS = {
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public ForgeMenu(Player player) {
        super(player, "§6Forge", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);

        buildForgeSlots();
        buildRecipeList();
    }

    private void buildForgeSlots() {
        ForgeManager manager = ForgeManager.getInstance();
        long now = System.currentTimeMillis();
        int slotCount = manager.getSlotCount(player.getUniqueId());

        for (int i = 0; i < FORGE_SLOTS.length; i++) {
            int displaySlot = FORGE_SLOTS[i];
            if (i >= slotCount) {
                setItem(displaySlot, new ItemBuilder(Material.IRON_BARS)
                        .displayName("§7Locked Forge Slot")
                        .lore("§7Unlock more slots via Heart", "§7of the Mountain.")
                        .build());
                continue;
            }

            ForgeJob job = manager.getJob(player.getUniqueId(), i);
            if (job == null) {
                setItem(displaySlot, new ItemBuilder(Material.FURNACE)
                        .displayName("§aForge Slot " + (i + 1))
                        .lore("§7Empty.", "§7Use §e/forge start <recipe> §7to begin.")
                        .build());
                continue;
            }

            if (job.isComplete(now)) {
                final int slotIdx = i;
                setItem(displaySlot, new ItemBuilder(Material.ANVIL)
                        .displayName("§a" + job.getRecipe().getDisplayName())
                        .lore("§7Slot " + (i + 1), "§aReady to claim!", "", "§eClick to collect!")
                        .build(),
                        e -> {
                            try {
                                ForgeManager.getInstance().collectForge(
                                        player.getUniqueId(), slotIdx, System.currentTimeMillis());
                                player.sendMessage("Collected " + job.getRecipe().getOutputAmount()
                                        + "x " + job.getRecipe().getOutputItem()
                                        + " from forging " + job.getRecipe().getDisplayName() + "!");
                                new ForgeMenu(player).open(player);
                            } catch (IllegalStateException ex) {
                                new ForgeMenu(player).open(player);
                            }
                        });
            } else {
                long elapsed = (now - job.getStartTimeMillis()) / 1000L;
                long remaining = Math.max(0, job.getDurationSeconds() - elapsed);
                setItem(displaySlot, new ItemBuilder(Material.BLAST_FURNACE)
                        .displayName("§e" + job.getRecipe().getDisplayName())
                        .lore("§7Slot " + (i + 1), "§7Forging...",
                              "§7Time remaining: §e" + formatDuration((int) remaining))
                        .build());
            }
        }
    }

    private void buildRecipeList() {
        ForgeRecipe[] recipes = ForgeRecipe.values();
        for (int i = 0; i < RECIPE_SLOTS.length && i < recipes.length; i++) {
            ForgeRecipe recipe = recipes[i];
            List<String> lore = new ArrayList<>();
            lore.add("§7Output: §a" + recipe.getOutputAmount() + "x " + recipe.getOutputItem());
            lore.add("§7Duration: §e" + formatDuration(recipe.getDurationSeconds()));
            lore.add("");
            lore.add("§7Ingredients:");
            for (Map.Entry<String, Integer> e : recipe.getIngredients().entrySet()) {
                lore.add("§8 • §f" + e.getValue() + "x " + e.getKey());
            }
            setItem(RECIPE_SLOTS[i], new ItemBuilder(Material.BOOK)
                    .displayName("§6" + recipe.getDisplayName())
                    .lore(lore.toArray(new String[0]))
                    .build());
        }
    }

    private static String formatDuration(int seconds) {
        if (seconds <= 0) return "0s";
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        StringBuilder sb = new StringBuilder();
        if (h > 0) sb.append(h).append("h ");
        if (m > 0) sb.append(m).append("m ");
        if (s > 0 || sb.length() == 0) sb.append(s).append("s");
        return sb.toString().trim();
    }
}
