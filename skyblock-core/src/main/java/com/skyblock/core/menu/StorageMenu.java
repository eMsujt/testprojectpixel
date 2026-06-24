package com.skyblock.core.menu;

import com.skyblock.core.manager.BackpackManager.BackpackTier;
import com.skyblock.core.manager.StorageManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The "Storage" hub, opened from the SkyBlock Menu. Routes to the player's two
 * storage areas — the Ender Chest ({@link EnderChestMenu}) and the Backpack
 * ({@link BackpackMenu}) — instead of showing one page's contents directly.
 */
public final class StorageMenu extends AbstractSkyBlockMenu {

    public StorageMenu(Player player) {
        super(player, "Storage", 6);
    }

    @Override
    protected void populate() {
        UUID id = player.getUniqueId();
        ItemStack bg = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) setItem(slot, bg);

        StorageManager storage = StorageManager.getInstance();

        setItem(20, new ItemBuilder(Material.ENDER_CHEST)
                .displayName("§aEnder Chest")
                .lore(
                        "§7Pages unlocked: §e" + storage.getUnlockedPages(id)
                                + "§7/§e" + StorageManager.PAGE_COUNT,
                        "",
                        "§eClick to open!")
                .build(),
                e -> { e.setCancelled(true); new EnderChestMenu(id).open(player); });

        BackpackTier tier = storage.getBackpackTier(id);
        setItem(24, new ItemBuilder(Material.CHEST)
                .displayName("§aBackpack")
                .lore(
                        "§7Tier: §a" + tier.name(),
                        "§7Slots: §e" + tier.getSlots(),
                        "",
                        "§eClick to open!")
                .build(),
                e -> { e.setCancelled(true); new BackpackMenu(id).open(player); });

        setItem(48, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });

        setItem(49, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(),
                e -> { e.setCancelled(true); player.closeInventory(); });
    }
}
