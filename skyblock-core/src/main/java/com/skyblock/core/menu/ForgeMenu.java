package com.skyblock.core.menu;

import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.manager.ForgeManager.ForgeJob;
import com.skyblock.core.manager.ForgeManager.ForgeRecipe;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * GUI menu opened by {@code /forge}. Renders the player's forge state directly
 * from {@link ForgeManager}: the top row shows each owned forge slot with the
 * recipe being forged and its remaining craft time (or a claim action once a
 * job completes), and the body lists every available forge recipe with its
 * output, duration and ingredients.
 */
public final class ForgeMenu extends Menu {

    /** Left-3-column slots (rows 1–4) for active forge jobs (up to {@link ForgeManager#MAX_SLOT_COUNT}). */
    private static final int[] FORGE_SLOTS = {9, 10, 11, 18, 19, 20, 27};

    /** Body slots used to list the available recipes (cols 3-7, rows 1-4). */
    private static final int[] RECIPE_SLOTS = {
            12, 13, 14, 15, 16,
            21, 22, 23, 24, 25,
            30, 31, 32, 33, 34,
            39, 40, 41, 42, 43
    };

    private static final int CLOSE_SLOT = 49;

    private final UUID playerId;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public ForgeMenu(Player player) {
        this(player.getUniqueId());
    }

    public ForgeMenu(UUID playerId) {
        super("§7§lForge", 6);
        this.playerId = playerId;
    }

    /** Unused: this menu manages its own inventory via {@link #open(Player)}. */
    @Override
    protected void build() {
    }

    @Override
    public void open(Player player) {
        handlers.clear();
        inventory = Bukkit.createInventory(this, 54, getTitle());

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        buildForgeSlots(player);
        buildRecipeList();

        inventory.setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the forge.")
                .build());
        handlers.put(CLOSE_SLOT, e -> player.closeInventory());

        player.openInventory(inventory);
    }

    private void buildForgeSlots(Player player) {
        ForgeManager manager = ForgeManager.getInstance();
        long now = System.currentTimeMillis();
        int slotCount = manager.getSlotCount(playerId);

        for (int i = 0; i < FORGE_SLOTS.length; i++) {
            int displaySlot = FORGE_SLOTS[i];
            if (i >= slotCount) {
                inventory.setItem(displaySlot, new ItemBuilder(Material.IRON_BARS)
                        .displayName("§7Locked Forge Slot")
                        .lore("§7Unlock more slots via Heart", "§7of the Mountain.")
                        .build());
                continue;
            }

            ForgeJob job = manager.getJob(playerId, i);
            if (job == null) {
                inventory.setItem(displaySlot, new ItemBuilder(Material.FURNACE)
                        .displayName("§aForge Slot " + (i + 1))
                        .lore("§7Empty.", "§7Use §e/forge start <recipe> §7to begin.")
                        .build());
                continue;
            }

            if (job.isComplete(now)) {
                inventory.setItem(displaySlot, new ItemBuilder(Material.ANVIL)
                        .displayName("§a" + job.getRecipe().getDisplayName())
                        .lore("§7Slot " + (i + 1), "§aReady to claim!", "", "§eClick to collect!")
                        .build());
                final int slot = i;
                handlers.put(displaySlot, e -> {
                    try {
                        ForgeManager.getInstance().collectForge(playerId, slot, System.currentTimeMillis());
                        Player clicker = (Player) e.getWhoClicked();
                        ForgeJob collected = job;
                        clicker.sendMessage("Collected " + collected.getRecipe().getOutputAmount() + "x "
                                + collected.getRecipe().getOutputItem() + " from forging "
                                + collected.getRecipe().getDisplayName() + "!");
                        new ForgeMenu(playerId).open(clicker);
                    } catch (IllegalStateException ex) {
                        new ForgeMenu(playerId).open((Player) e.getWhoClicked());
                    }
                });
            } else {
                long elapsed = (now - job.getStartTimeMillis()) / 1000L;
                long remaining = Math.max(0, job.getDurationSeconds() - elapsed);
                inventory.setItem(displaySlot, new ItemBuilder(Material.BLAST_FURNACE)
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
            inventory.setItem(RECIPE_SLOTS[i], new ItemBuilder(Material.BOOK)
                    .displayName("§6" + recipe.getDisplayName())
                    .lore(lore)
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Consumer<InventoryClickEvent> handler = handlers.get(event.getSlot());
        if (handler != null) {
            handler.accept(event);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /** Formats a duration in seconds as a human-readable string (e.g. "2h 30m 15s"). */
    private static String formatDuration(int seconds) {
        if (seconds <= 0) {
            return "0s";
        }
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
