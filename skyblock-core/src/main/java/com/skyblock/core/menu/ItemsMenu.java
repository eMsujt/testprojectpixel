package com.skyblock.core.menu;

import com.skyblock.core.items.CustomItemManager;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.util.SkyblockUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Browser of every registered custom SkyBlock item, each rendered with its real 1:1 Hypixel head
 * texture (or vanilla material fallback). Clicking an item gives a copy to the player — handy for
 * the operator to verify items in-game.
 */
public final class ItemsMenu extends Menu {

    private final Player player;

    // 7×4 content grid, skipping the bordered columns/rows.
    private static final int[] CONTENT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43,
    };

    public ItemsMenu(Player player) {
        super("§aSkyBlock Items", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) setItem(slot, pane);
        }

        CustomItemManager.SkyBlockItem[] items = CustomItemManager.SkyBlockItem.values();
        for (int i = 0; i < items.length && i < CONTENT_SLOTS.length; i++) {
            CustomItemManager.SkyBlockItem item = items[i];
            ChatColor color = SkyblockUtils.rarityColor(item.getRarity());
            ItemStack icon = new ItemBuilder(item.toItemStack())
                    .addLore("")
                    .addLore("§7Type: §f" + item.getItemType().name().charAt(0)
                            + item.getItemType().name().substring(1).toLowerCase())
                    .addLore("")
                    .addLore("§eClick to receive!")
                    .build();
            setItem(CONTENT_SLOTS[i], icon, e -> {
                e.setCancelled(true);
                player.getInventory().addItem(item.toItemStack());
                player.sendMessage("§aReceived " + color + item.getDisplayName() + "§a!");
            });
        }

        setItem(49, new ItemBuilder(Material.ARROW).displayName("§cBack")
                        .lore("§7Return to the SkyBlock Menu.").build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        super.handleClick(event);
    }
}
