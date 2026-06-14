package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.skyblock.plugin.SkyBlockPlugin;

public final class SkyBlockMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    public SkyBlockMenu() {
        this.inventory = Bukkit.createInventory(this, 54, "§aSkyBlock Menu");
        build();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build() {
        Inventory inv = inventory;

        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§7");
        for (int i = 0; i < 9; i++)  inv.setItem(i,      pane);
        for (int i = 45; i < 54; i++) inv.setItem(i,      pane);
        for (int row = 1; row <= 4; row++) {
            inv.setItem(row * 9,     pane);
            inv.setItem(row * 9 + 8, pane);
        }

        // Row 1: skill / gameplay sub-menus
        inv.setItem(10, makeItem(Material.BOOK,              "§aSkills"));
        inv.setItem(11, makeItem(Material.PAINTING,          "§aCollections"));
        inv.setItem(12, makeItem(Material.CRAFTING_TABLE,    "§aCrafting"));
        inv.setItem(13, makeItem(Material.LEATHER_CHESTPLATE,"§aWardrobe"));
        inv.setItem(14, makeItem(Material.CHEST,             "§aStorage"));
        inv.setItem(15, makeItem(Material.LEATHER_BOOTS,     "§aAccessories"));
        inv.setItem(16, makeItem(Material.BONE,              "§aPets"));

        // Row 2: economy / social / travel sub-menus
        inv.setItem(19, makeItem(Material.PLAYER_HEAD,       "§aProfile"));
        inv.setItem(20, makeItem(Material.GOLD_INGOT,        "§aAuction House"));
        inv.setItem(21, makeItem(Material.EMERALD,           "§aBazaar"));
        inv.setItem(22, makeItem(Material.CLOCK,             "§aCalendar"));
        inv.setItem(23, makeItem(Material.WRITTEN_BOOK,      "§aQuests"));
        inv.setItem(24, makeItem(Material.ENDER_PEARL,       "§aCo-op"));
        inv.setItem(25, makeItem(Material.COMPASS,           "§aFast Travel"));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SkyBlockMenu)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        switch (event.getRawSlot()) {
            case 10 -> new SkillsMenu(player).open(player);
            case 11 -> new CollectionsMenu(player).open(player);
            case 12 -> new RecipeBookMenu(player).open(player);
            case 13 -> new WardrobeMenu(player).open(player);
            case 14 -> new StorageMenu().open(player);
            case 15 -> new AccessoryBagMenu(player).open(player);
            case 16 -> new PetsMenu(player).open(player);
            case 19 -> new ProfileMenu(player).open(player);
            case 20 -> new AuctionHouseMenu().open(player);
            case 21 -> new BazaarMenu().open(player);
            case 22 -> new CalendarMenu(SkyBlockPlugin.getInstance()).open(player);
            case 23 -> new QuestsMenu().open(player);
            case 25 -> new FastTravelMenu().open(player);
            default -> { }
        }
    }

    private ItemStack makeItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
