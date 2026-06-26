package com.skyblock.core.menu;

import com.skyblock.core.crafting.manager.CraftingManager;
import com.skyblock.core.crafting.manager.CraftingManager.SkyBlockRecipe;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * The "Craft Item" GUI opened by {@code /craft} and the SkyBlock Menu. A working
 * 3×3 crafting grid (slots 10-12 / 19-21 / 28-30) the player places items into;
 * the output slot (24) previews the matching {@link SkyBlockRecipe} and crafts it
 * on click (consuming one of each grid ingredient). The right column shows the
 * VIP-gated "Quick Crafting Slot" panes. Items left in the grid are returned to
 * the player on close.
 *
 * <p>The grid slots are the only interactive slots ({@link #isInteractiveSlot}),
 * and only plain place/pickup is allowed — {@link com.skyblock.core.listener.PlayerEventListener}
 * blocks shift-click / drag / number-key so items can't be duped or lost.</p>
 */
public final class CraftingMenu extends AbstractMenu {

    private static final int[] GRID = {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int OUTPUT_SLOT = 24;

    public CraftingMenu(JavaPlugin plugin, Player player) {
        super(plugin, player, "Craft Item", 54);
    }

    @Override
    protected void populate() {
        ItemStack quick = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .displayName("§cQuick Crafting Slot")
                .lore("§7Quick crafting allows you to", "§7craft items without assembling",
                      "§7the recipe.", "", "§cRequires §aVIP §cor above.").build();
        for (int slot : new int[]{7, 16, 25, 34, 43, 52}) setItem(slot, quick);

        setItem(49, new ItemBuilder(Material.ARROW).displayName("§aGo Back")
                .lore("§7To SkyBlock Menu").build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
        // Grid slots are emptied after open() so the player can place items;
        // the output preview is set by refreshOutput().
    }

    @Override
    public void open(Player player) {
        super.open(player);
        Inventory inv = getInventory();
        for (int slot : GRID) inv.setItem(slot, null);
        refreshOutput();
    }

    @Override
    public boolean isInteractiveSlot(int slot) {
        for (int g : GRID) if (g == slot) return true;
        return false;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (isInteractiveSlot(slot)) {
            // Place/pickup already applied by the un-cancelled event; refresh the
            // preview next tick, once the inventory reflects the change.
            plugin.getServer().getScheduler().runTask(plugin, this::refreshOutput);
            return;
        }
        if (slot == OUTPUT_SLOT) {
            craft();
            return;
        }
        super.handleClick(event);
    }

    /** Sets the output slot to a preview of the matching recipe, or a "Recipe Required" barrier. */
    private void refreshOutput() {
        Inventory inv = getInventory();
        Optional<SkyBlockRecipe> match = CraftingManager.getInstance().findMatchingRecipe(readGrid(inv));
        if (match.isPresent()) {
            SkyBlockRecipe recipe = match.get();
            inv.setItem(OUTPUT_SLOT, new ItemBuilder(new ItemStack(recipe.result(), recipe.resultAmount()))
                    .addLore("").addLore("§eClick to craft!").build());
        } else {
            inv.setItem(OUTPUT_SLOT, new ItemBuilder(Material.BARRIER)
                    .displayName("§cRecipe Required")
                    .lore("§7Add the items for a valid", "§7recipe in the crafting grid",
                          "§7to the left!").build());
        }
    }

    /** Crafts the matching recipe's output, consuming one item from each occupied grid cell. */
    private void craft() {
        Inventory inv = getInventory();
        Optional<SkyBlockRecipe> match = CraftingManager.getInstance().findMatchingRecipe(readGrid(inv));
        if (match.isEmpty()) {
            player.sendMessage("§cAdd the items for a valid recipe first.");
            return;
        }
        SkyBlockRecipe recipe = match.get();
        for (int slot : GRID) {
            ItemStack cell = inv.getItem(slot);
            if (cell != null && cell.getType() != Material.AIR) {
                int amount = cell.getAmount() - 1;
                if (amount <= 0) {
                    inv.setItem(slot, null);
                } else {
                    cell.setAmount(amount);
                    inv.setItem(slot, cell);
                }
            }
        }
        ItemStack result = new ItemStack(recipe.result(), recipe.resultAmount());
        for (ItemStack overflow : player.getInventory().addItem(result).values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), overflow);
        }
        player.sendMessage("§aCrafted §f" + recipe.resultAmount() + "x " + prettyName(recipe.result()) + "§a!");
        refreshOutput();
    }

    /** Reads the live 3×3 grid into a Material[][] (null = empty cell). */
    private static Material[][] readGrid(Inventory inv) {
        Material[][] grid = new Material[3][3];
        for (int i = 0; i < GRID.length; i++) {
            ItemStack item = inv.getItem(GRID[i]);
            grid[i / 3][i % 3] = (item == null || item.getType() == Material.AIR) ? null : item.getType();
        }
        return grid;
    }

    @Override
    public void onClose(Player player) {
        Inventory inv = getInventory();
        for (int slot : GRID) {
            ItemStack item = inv.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                for (ItemStack overflow : player.getInventory().addItem(item).values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), overflow);
                }
                inv.setItem(slot, null);
            }
        }
    }

    private static String prettyName(Material material) {
        String name = material.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
