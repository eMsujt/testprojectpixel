package com.skyblock.plugin.menu;

import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class PotionBagMenu implements InventoryHolder, Listener {

    private static final int FIRST_SLOT = 0;
    private static final int LAST_SLOT = 44;

    private final Inventory inventory;

    public PotionBagMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§5Potion Bag");
        build(player);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build(Player player) {
        ItemStack pane = makeItem(Material.PURPLE_STAINED_GLASS_PANE, "§r");
        for (int slot = 45; slot < 54; slot++) {
            inventory.setItem(slot, pane);
        }

        // Slots 0–44 display the player's persisted Potion Bag contents.
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        ItemStack[] contents = profile.getPotionBagContents();
        if (contents != null) {
            for (int i = 0; i < contents.length && FIRST_SLOT + i <= LAST_SLOT; i++) {
                inventory.setItem(FIRST_SLOT + i, contents[i]);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof PotionBagMenu)) return;
        int raw = event.getRawSlot();
        if (raw >= 0 && raw < 54 && (raw < FIRST_SLOT || raw > LAST_SLOT)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof PotionBagMenu)) return;
        HumanEntity closer = event.getPlayer();
        if (!(closer instanceof Player player)) return;
        ItemStack[] contents = new ItemStack[LAST_SLOT - FIRST_SLOT + 1];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = event.getInventory().getItem(FIRST_SLOT + i);
        }
        ProfileManager.getInstance().getOrCreate(player.getUniqueId()).setPotionBagContents(contents);
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
