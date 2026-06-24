package com.skyblock.core.menu;

import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 5-row enchanting-table GUI. The item currently being enchanted (the player's
 * main-hand item) is shown in the centre slot ({@link #ITEM_SLOT}); an empty
 * hand shows a placeholder prompt instead.
 */
public final class EnchantingTableMenu extends AbstractSkyBlockMenu {

    public static final int ITEM_SLOT = 22;

    public EnchantingTableMenu(Player player) {
        super(player, "§5Enchanting Table", 5);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 36; slot < 45; slot++) setItem(slot, pane);

        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand == null || inHand.getType() == Material.AIR) {
            setItem(ITEM_SLOT, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo item to enchant")
                    .lore("§7Hold an item in your main hand", "§7to enchant it here.")
                    .build(),
                    e -> e.setCancelled(true));
        } else {
            setItem(ITEM_SLOT, inHand.clone(), e -> e.setCancelled(true));
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
