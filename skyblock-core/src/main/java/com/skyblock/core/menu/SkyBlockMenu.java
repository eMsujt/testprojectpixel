package com.skyblock.core.menu;

import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class SkyBlockMenu extends Menu {

    private final Player player;

    public SkyBlockMenu(Player player) {
        super("§aSkyBlock Menu", 6);
        this.player = player;
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) setItem(slot, pane);
        }

        setItem(10, new ItemBuilder(Material.DIAMOND_SWORD).displayName("§aSkills")
                .lore("§7View your skill levels and XP.").build(),
                e -> { e.setCancelled(true); new SkillsMenu(com.skyblock.core.SkyBlockCore.getInstance(), player).open(player); });

        setItem(11, new ItemBuilder(Material.WRITABLE_BOOK).displayName("§aCollections")
                .lore("§7Track your collection progress.").build(),
                e -> { e.setCancelled(true); new CollectionsMenu(player.getUniqueId()).open(player); });

        setItem(12, new ItemBuilder(Material.CAT_SPAWN_EGG).displayName("§aPets")
                .lore("§7Manage your pets.").build(),
                e -> { e.setCancelled(true); new PetMenu(player).open(player); });

        setItem(13, new ItemBuilder(Material.PISTON).displayName("§aMinions")
                .lore("§7Manage your minions.").build(),
                e -> e.setCancelled(true));

        setItem(14, new ItemBuilder(Material.BONE).displayName("§aBestiary")
                .lore("§7Track the mobs you've slain.").build(),
                e -> { e.setCancelled(true); new BestiaryMenu(player).open(player); });

        setItem(15, new ItemBuilder(Material.NETHER_STAR).displayName("§aSlayer")
                .lore("§7View your slayer quests.").build(),
                e -> { e.setCancelled(true); new SlayerMenu(com.skyblock.core.SkyBlockCore.getInstance(), player).open(player); });

        setItem(16, new ItemBuilder(Material.END_PORTAL_FRAME).displayName("§5Dungeons")
                .lore("§7Explore the Catacombs.").build(),
                e -> { e.setCancelled(true); new DungeonsMenu(player.getUniqueId()).open(player); });

        setItem(19, new ItemBuilder(Material.ENCHANTING_TABLE).displayName("§5Enchanting")
                .lore("§7Enchant your items.").build(),
                e -> { e.setCancelled(true); new EnchantingMenu(com.skyblock.core.SkyBlockCore.getInstance(), player).open(player); });

        setItem(20, new ItemBuilder(Material.FISHING_ROD).displayName("§9Fishing")
                .lore("§7Cast your line and fish.").build(),
                e -> { e.setCancelled(true); new FishingMenu(player).open(player); });

        setItem(21, new ItemBuilder(Material.GOLD_BLOCK).displayName("§6Bank")
                .lore("§7Deposit and withdraw coins.").build(),
                e -> { e.setCancelled(true); new BankMenu(player).open(player); });

        setItem(22, new ItemBuilder(Material.LEATHER_CHESTPLATE).displayName("§aWardrobe")
                .lore("§7Manage your armor sets.").build(),
                e -> { e.setCancelled(true); new WardrobeMenu(player).open(player); });

        setItem(23, new ItemBuilder(Material.ENDER_CHEST).displayName("§5Accessory Bag")
                .lore("§7Manage your accessories.").build(),
                e -> { e.setCancelled(true); new AccessoryBagMenu(player).open(player); });

        setItem(24, new ItemBuilder(Material.CHEST).displayName("§aStorage")
                .lore("§7Access your personal storage.").build(),
                e -> e.setCancelled(true));

        setItem(25, new ItemBuilder(Material.DIAMOND).displayName("§aStats")
                .lore("§7View your SkyBlock stats.").build(),
                e -> { e.setCancelled(true); new StatsMenu(player).open(player); });
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        super.handleClick(event);
    }
}
