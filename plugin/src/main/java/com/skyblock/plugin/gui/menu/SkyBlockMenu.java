package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The main SkyBlock hub menu.
 *
 * <p>A 54-slot (6-row) chest GUI titled '§aSkyBlock Menu'. A COMPASS at slot 13
 * serves as the header icon; shortcut icons for each major sub-menu fill the
 * inner content slots, matching Hypixel's layout.</p>
 */
public class SkyBlockMenu extends Menu {

    private final Player player;

    public SkyBlockMenu(Player player) {
        super("§aSkyBlock Menu", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        fillBorder();

        setItem(13, new ItemBuilder(Material.COMPASS)
                .displayName("§aSkyBlock Menu")
                .lore("§7Navigate SkyBlock features.")
                .build());

        setItem(19, new ItemBuilder(Material.DIAMOND_SWORD)
                .displayName("§aSkills")
                .lore("§7View your skill levels and XP.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new SkillsMenu(player.getUniqueId()).open(player);
                });

        setItem(20, new ItemBuilder(Material.WRITABLE_BOOK)
                .displayName("§aCollections")
                .lore("§7Track your collection progress.")
                .build());

        setItem(21, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§aBank")
                .lore("§7Deposit and withdraw coins.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new BankMenu(player).open(player);
                });

        setItem(22, new ItemBuilder(Material.CHEST)
                .displayName("§aStorage")
                .lore("§7Access your personal storage.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new StorageMenu().open(player);
                });

        setItem(23, new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .displayName("§aWardrobe")
                .lore("§7Manage your armor sets.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new WardrobeMenu().open(player);
                });

        setItem(24, new ItemBuilder(Material.ENDER_CHEST)
                .displayName("§aAccessory Bag")
                .lore("§7Manage your accessories.")
                .build());

        setItem(25, new ItemBuilder(Material.FISHING_ROD)
                .displayName("§aFishing Bag")
                .lore("§7Store your fishing gear.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new FishingBagMenu(player).open(player);
                });

        setItem(29, new ItemBuilder(Material.CAT_SPAWN_EGG)
                .displayName("§aPets")
                .lore("§7Manage your pets.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new PetsMenu(player.getUniqueId()).open(player);
                });

        setItem(31, new ItemBuilder(Material.PAPER)
                .displayName("§aObjectives")
                .lore("§7View your active objectives.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new ObjectivesMenu().open(player);
                });

        setItem(33, new ItemBuilder(Material.COMPARATOR)
                .displayName("§aSettings")
                .lore("§7Configure your SkyBlock settings.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    new SettingsMenu().open(player);
                });
    }

    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
