package com.skyblock.core.menu;

import com.skyblock.core.manager.ReforgeManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 3-row GUI where a player drops the item to reforge in slot 11,
 * clicks the anvil at slot 13 to apply a random reforge, and collects
 * the result from slot 15.
 */
public final class ReforgeMenu extends Menu {

    static final int ITEM_SLOT   = 11;
    static final int REFORGE_SLOT = 13;
    static final int RESULT_SLOT  = 15;

    private static final ReforgeManager.ReforgeType[] REFORGE_TYPES;

    static {
        ReforgeManager.ReforgeType[] all = ReforgeManager.ReforgeType.values();
        // skip NONE (index 0)
        REFORGE_TYPES = new ReforgeManager.ReforgeType[all.length - 1];
        System.arraycopy(all, 1, REFORGE_TYPES, 0, REFORGE_TYPES.length);
    }

    public ReforgeMenu() {
        super("§6Reforge Item", 3);
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 27; slot++) {
            if (slot != ITEM_SLOT && slot != REFORGE_SLOT && slot != RESULT_SLOT) {
                setItem(slot, pane);
            }
        }

        setItem(ITEM_SLOT, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§7Item to Reforge")
                .lore("§7Drag your item here.")
                .build());

        setItem(REFORGE_SLOT, new ItemBuilder(Material.ANVIL)
                .displayName("§6Reforge")
                .lore("§7Click to apply a random reforge", "§7to the item in the left slot.")
                .build());

        setItem(RESULT_SLOT, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§7Result")
                .lore("§7The reforged item appears here.")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot == ITEM_SLOT || slot == RESULT_SLOT) {
            // allow the player to freely place / take the item being reforged or collected
            return;
        }
        event.setCancelled(true);
        if (slot == REFORGE_SLOT && event.getWhoClicked() instanceof Player player) {
            ItemStack item = getInventory().getItem(ITEM_SLOT);
            if (item == null || item.getType() == Material.AIR
                    || item.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                player.sendMessage("§cPlace an item in the left slot first.");
                return;
            }
            ReforgeManager mgr = ReforgeManager.getInstance();
            ReforgeManager.ReforgeType chosen =
                    REFORGE_TYPES[(int) (Math.random() * REFORGE_TYPES.length)];
            mgr.setReforge(player.getUniqueId(), chosen);
            getInventory().setItem(RESULT_SLOT, item.clone());
            getInventory().setItem(ITEM_SLOT, null);
            player.sendMessage("§aYour item was reforged to §6" + chosen.getDisplayName() + "§a!");
        }
    }
}
