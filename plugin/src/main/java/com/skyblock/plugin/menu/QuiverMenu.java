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

public final class QuiverMenu implements InventoryHolder, Listener {

    /** First interactive (arrow) slot, inclusive. */
    private static final int FIRST_SLOT = 10;

    /** Last interactive (arrow) slot, inclusive. */
    private static final int LAST_SLOT = 16;

    private final Inventory inventory;

    public QuiverMenu() {
        this.inventory = Bukkit.createInventory(this, 27, "§eQuiver");
    }

    public void open(Player player) {
        build(player);
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build(Player player) {
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 27; slot++) {
            if (slot < FIRST_SLOT || slot > LAST_SLOT) {
                inventory.setItem(slot, pane);
            }
        }

        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        ItemStack[] contents = profile.getQuiverContents();
        if (contents != null) {
            for (int i = 0; i < contents.length && FIRST_SLOT + i <= LAST_SLOT; i++) {
                inventory.setItem(FIRST_SLOT + i, contents[i]);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof QuiverMenu)) {
            return;
        }
        int raw = event.getRawSlot();
        if (raw >= 0 && raw < 27 && (raw < FIRST_SLOT || raw > LAST_SLOT)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof QuiverMenu)) {
            return;
        }
        HumanEntity closer = event.getPlayer();
        if (!(closer instanceof Player)) {
            return;
        }
        Player player = (Player) closer;
        ItemStack[] contents = new ItemStack[LAST_SLOT - FIRST_SLOT + 1];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = event.getInventory().getItem(FIRST_SLOT + i);
        }
        ProfileManager.getInstance().getOrCreate(player.getUniqueId()).setQuiverContents(contents);
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
