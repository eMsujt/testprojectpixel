package com.skyblock.core.menu;

import com.skyblock.core.manager.BackpackManager;
import com.skyblock.core.manager.BackpackManager.BackpackTier;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Read-only GUI showing a player's backpack: their {@link BackpackTier} and the
 * item names stored in it, one per content slot.
 */
public final class BackpackMenu extends Menu {

    private static final int[] CONTENT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final UUID playerId;

    public BackpackMenu(UUID playerId) {
        super("Backpack", 6);
        this.playerId = playerId;
    }

    public BackpackMenu(Player player) {
        this(player.getUniqueId());
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        BackpackManager manager = BackpackManager.getInstance();
        BackpackTier tier = manager.getTier(playerId);
        List<String> items = manager.getItems(playerId);

        setItem(4, new ItemBuilder(Material.CHEST)
                .displayName("§eBackpack")
                .lore(
                        "§7Tier: §a" + tier.name(),
                        "§7Slots: §e" + items.size() + " §8/ §e" + tier.getSlots())
                .build());

        if (items.isEmpty()) {
            setItem(31, new ItemBuilder(Material.BARRIER)
                    .displayName("§cEmpty Backpack")
                    .lore("§7This backpack contains no items.")
                    .build());
            return;
        }

        for (int i = 0; i < CONTENT_SLOTS.length && i < items.size(); i++) {
            setItem(CONTENT_SLOTS[i], new ItemBuilder(Material.PAPER)
                    .displayName("§f" + items.get(i))
                    .build());
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
