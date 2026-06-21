package com.skyblock.core.menu;

import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.manager.ForgeManager.ForgeJob;
import com.skyblock.core.manager.ForgeManager.ForgeRecipe;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

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

    /** Row-1 slots 10-14 for active forge jobs (5 visible forge slots). */
    private static final int[] FORGE_SLOTS = {10, 11, 12, 13, 14};

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
    private final Map<Integer, Consumer<InventoryClickEvent>> clickHandlers = new HashMap<>();

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
    public void open(Player p) {
        clickHandlers.clear();
        inventory = Bukkit.createInventory(this, 54, getTitle());

        UUID id = playerId;
        SkyblockUtils.fillBorder(getRows(), inventory::setItem, Material.GRAY_STAINED_GLASS_PANE);

        buildForgeSlots(id);
        buildRecipeList();

        inventory.setItem(CLOSE_SLOT, SkyblockUtils.buildItem(Material.BARRIER,
                "§cClose", "§7Close the forge."));
        clickHandlers.put(CLOSE_SLOT, e -> p.closeInventory());

        p.openInventory(inventory);
    }

    private void buildForgeSlots(UUID id) {
        ForgeManager manager = ForgeManager.getInstance();
        long now = System.currentTimeMillis();
        int slotCount = manager.getSlotCount(id);

        for (int i = 0; i < FORGE_SLOTS.length; i++) {
            int displaySlot = FORGE_SLOTS[i];
            if (i >= slotCount) {
                inventory.setItem(displaySlot, SkyblockUtils.buildItem(Material.IRON_BARS,
                        "§7Locked Forge Slot",
                        "§7Unlock more slots via Heart", "§7of the Mountain."));
                continue;
            }

            ForgeJob job = manager.getJob(id, i);
            if (job == null) {
                inventory.setItem(displaySlot, SkyblockUtils.buildItem(Material.FURNACE,
                        "§aForge Slot " + (i + 1),
                        "§7Empty.", "§7Use §e/forge start <recipe> §7to begin."));
                continue;
            }

            if (job.isComplete(now)) {
                inventory.setItem(displaySlot, SkyblockUtils.buildItem(Material.ANVIL,
                        "§a" + job.getRecipe().getDisplayName(),
                        "§7Slot " + (i + 1), "§aReady to claim!", "", "§eClick to collect!"));
                final int slotIdx = i;
                clickHandlers.put(displaySlot, e -> {
                    try {
                        ForgeManager.getInstance().collectForge(id, slotIdx, System.currentTimeMillis());
                        Player clicker = (Player) e.getWhoClicked();
                        clicker.sendMessage("Collected " + job.getRecipe().getOutputAmount() + "x "
                                + job.getRecipe().getOutputItem() + " from forging "
                                + job.getRecipe().getDisplayName() + "!");
                        new ForgeMenu(clicker).open(clicker);
                    } catch (IllegalStateException ex) {
                        new ForgeMenu((Player) e.getWhoClicked()).open((Player) e.getWhoClicked());
                    }
                });
            } else {
                long elapsed = (now - job.getStartTimeMillis()) / 1000L;
                long remaining = Math.max(0, job.getDurationSeconds() - elapsed);
                inventory.setItem(displaySlot, SkyblockUtils.buildItem(Material.BLAST_FURNACE,
                        "§e" + job.getRecipe().getDisplayName(),
                        "§7Slot " + (i + 1), "§7Forging...",
                        "§7Time remaining: §e" + formatDuration((int) remaining)));
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
            inventory.setItem(RECIPE_SLOTS[i], SkyblockUtils.createNamedItem(Material.BOOK,
                    "§6" + recipe.getDisplayName(), lore));
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Consumer<InventoryClickEvent> handler = clickHandlers.get(event.getSlot());
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
