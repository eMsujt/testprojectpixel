package com.skyblock.core.menu;

import com.skyblock.core.crafting.RecipeData;
import com.skyblock.core.item.SkyblockItems;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.ItemData;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Read-only crafting recipe viewer (Hypixel "Recipe Book" style): shows an item's real 3x3 recipe
 * with each ingredient's true icon and count, an arrow, and the finished result item. Clicks are
 * cancelled globally by the menu listener, so nothing here can be taken.
 */
public final class RecipeMenu extends Menu {

    /** Slots forming the 3x3 crafting grid inside a 6-row chest. */
    private static final int[] GRID = {10, 11, 12, 19, 20, 21, 28, 29, 30};

    private final String itemId;

    public RecipeMenu(String itemId) {
        super("§8Recipe: " + displayName(itemId), 6);
        this.itemId = itemId;
    }

    private static String displayName(String id) {
        String name = ItemData.name(id);
        return name != null ? name : id;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) setItem(slot, pane);
        }

        RecipeData.Ingredient[] grid = RecipeData.grid(itemId);
        if (grid == null) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo recipe")
                    .lore("§7This item has no crafting recipe.").build());
            return;
        }

        ItemStack empty = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int i = 0; i < GRID.length; i++) {
            RecipeData.Ingredient ing = grid[i];
            if (ing == null) {
                setItem(GRID[i], empty);
                continue;
            }
            setItem(GRID[i], ItemBuilder.forItem(ing.id())
                    .displayName(displayName(ing.id()))
                    .lore("§7Amount: §e" + ing.count())
                    .amount(Math.max(1, Math.min(64, ing.count())))
                    .build());
        }

        setItem(24, new ItemBuilder(Material.ARROW).displayName("§e->").build());

        ItemStack result = SkyblockItems.build(itemId, 1);
        if (result != null) {
            ItemStack craftButton = new ItemBuilder(result)
                    .addLore("")
                    .addLore("§eClick to craft!")
                    .build();
            setItem(25, craftButton, e -> attemptCraft((Player) e.getWhoClicked()));
        }

        setItem(49, new ItemBuilder(Material.BARRIER).displayName("§cClose").build(),
                e -> e.getWhoClicked().closeInventory());
    }

    private void attemptCraft(Player player) {
        RecipeData.Ingredient[] grid = RecipeData.grid(itemId);
        if (grid == null) {
            return;
        }
        Map<String, Integer> need = new HashMap<>();
        for (RecipeData.Ingredient ing : grid) {
            if (ing != null) {
                need.merge(ing.id(), ing.count(), Integer::sum);
            }
        }
        for (Map.Entry<String, Integer> e : need.entrySet()) {
            if (countOf(player, e.getKey()) < e.getValue()) {
                player.sendMessage("§cYou don't have the ingredients to craft this.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return;
            }
        }
        for (Map.Entry<String, Integer> e : need.entrySet()) {
            consume(player, e.getKey(), e.getValue());
        }
        ItemStack result = SkyblockItems.build(itemId, 1);
        for (ItemStack leftover : player.getInventory().addItem(result).values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
        player.sendMessage("§aCrafted §r" + displayName(itemId) + "§a!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.4f);
    }

    private static int countOf(Player player, String id) {
        int total = 0;
        for (ItemStack stack : player.getInventory().getStorageContents()) {
            if (stack != null && matches(stack, id)) {
                total += stack.getAmount();
            }
        }
        return total;
    }

    private static void consume(Player player, String id, int count) {
        ItemStack[] contents = player.getInventory().getStorageContents();
        int remaining = count;
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (stack == null || !matches(stack, id)) {
                continue;
            }
            int take = Math.min(remaining, stack.getAmount());
            stack.setAmount(stack.getAmount() - take);
            remaining -= take;
            if (stack.getAmount() <= 0) {
                contents[i] = null;
            }
        }
        player.getInventory().setStorageContents(contents);
    }

    /** Matches a custom item by its stamped id, or a plain vanilla item by its material name. */
    private static boolean matches(ItemStack stack, String ingredientId) {
        String stampedId = SkyblockItems.idOf(stack);
        if (stampedId != null) {
            return stampedId.equals(ingredientId);
        }
        return stack.getType().name().equals(ingredientId);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        super.handleClick(event);
    }
}
