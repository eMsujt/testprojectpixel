package com.skyblock.core.menu;

import com.skyblock.core.manager.BackpackManager.BackpackTier;
import com.skyblock.core.manager.StorageManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The "Storage" hub, opened from the SkyBlock Menu. Organised 1:1 with Hypixel's
 * Storage GUI (wiki Storage/UI): an Ender Chest banner (slot 4) with a row of
 * Ender Chest page panes (slots 9+, locked panes after), a Backpacks banner
 * (slot 22) and the player's Backpack slots (row starting slot 27). Page and
 * backpack tiles route to the real Ender Chest container / {@link BackpackMenu}.
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

        // Ender Chest banner.
        setItem(4, new ItemBuilder(Material.ENDER_CHEST)
                .displayName("§aEnder Chest")
                .lore("§7Store global items you can",
                      "§7access anywhere in your ender",
                      "§7chest.")
                .build(), e -> e.setCancelled(true));

        // Ender Chest pages: one purple pane per unlocked page (slots 9..), red locked panes after.
        int unlocked = storage.getUnlockedPages(id);
        for (int i = 0; i < 9; i++) {
            int slot = 9 + i;
            int pageNumber = i + 1;
            if (pageNumber <= unlocked) {
                setItem(slot, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE)
                        .displayName("§aEnder Chest Page " + pageNumber)
                        .lore("", "§eLeft-click to open!", "§eRight-click to change icon!")
                        .build(),
                        e -> { e.setCancelled(true); com.skyblock.core.manager.EnderChestManager.getInstance().open(player); });
            } else {
                setItem(slot, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .displayName("§cLocked Page")
                        .lore("§7Unlock more Ender Chest pages in",
                              "§7the community shop!")
                        .build(), e -> e.setCancelled(true));
            }
        }

        // Backpacks banner.
        setItem(22, new ItemBuilder(Material.CHEST)
                .displayName("§aBackpacks")
                .lore("§7Place backpack items in these",
                      "§7slots to use them as additional",
                      "§7storage that can be accessed",
                      "§7anywhere.")
                .build(), e -> e.setCancelled(true));

        // Backpack slot 1 = the player's current backpack; remaining row slots are empty.
        BackpackTier tier = storage.getBackpackTier(id);
        if (tier != null && tier.getSlots() > 0) {
            setItem(27, new ItemBuilder(Material.CHEST)
                    .displayName("§6Backpack Slot 1")
                    .lore("§a" + friendly(tier) + " Backpack",
                          "§7This backpack has §a" + tier.getSlots() + "§7",
                          "§7slots.",
                          " ",
                          "§eLeft-click to open!",
                          "§eRight-click to remove!")
                    .build(),
                    e -> { e.setCancelled(true); new BackpackMenu(id).open(player); });
        } else {
            setItem(27, new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE)
                    .displayName("§eEmpty Backpack Slot 1")
                    .lore(" ", "§eLeft-click a backpack item on", "§ethis slot to place it!")
                    .build(), e -> e.setCancelled(true));
        }
        for (int i = 1; i < 8; i++) {
            setItem(27 + i, new ItemBuilder(Material.BROWN_STAINED_GLASS_PANE)
                    .displayName("§eEmpty Backpack Slot " + (i + 1))
                    .lore(" ", "§eLeft-click a backpack item on", "§ethis slot to place it!")
                    .build(), e -> e.setCancelled(true));
        }

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

    /** Title-cases the backpack tier name for the "<Tier> Backpack" line. */
    private static String friendly(BackpackTier tier) {
        String n = tier.name().toLowerCase();
        return Character.toUpperCase(n.charAt(0)) + n.substring(1);
    }
}
