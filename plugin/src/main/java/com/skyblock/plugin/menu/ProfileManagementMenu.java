package com.skyblock.plugin.menu;

import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public final class ProfileManagementMenu implements InventoryHolder {

    private static final String TITLE = "§6Profile Management";
    private static final int SIZE = 54;

    /** The eight profile slots displayed across the first inner row. */
    private static final int[] PROFILE_SLOTS = {10, 11, 12, 13, 14, 15, 16, 17};

    /** The eight Hypixel fruit profile names, in order. */
    private static final String[] PROFILE_NAMES = {
            "Apple", "Banana", "Blueberry", "Coconut",
            "Cucumber", "Grapes", "Kiwi", "Lemon"
    };

    /** Slot for the info item. */
    private static final int INFO_SLOT = 49;
    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 53;

    private final Inventory inventory;

    public ProfileManagementMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, SIZE, TITLE);
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
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r", null);
        for (int slot = 0; slot < SIZE; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= SIZE - 9 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        PlayerProfile profile = ProfileManager.getInstance().getProfile(player.getUniqueId());

        // The first slot is the player's active profile; the rest are empty.
        for (int i = 0; i < PROFILE_NAMES.length; i++) {
            boolean active = i == 0 && profile != null;
            if (active) {
                inventory.setItem(PROFILE_SLOTS[i], makeItem(Material.GRASS_BLOCK,
                        "§a" + PROFILE_NAMES[i],
                        Arrays.asList("§7Active profile", "§8" + player.getName())));
            } else {
                inventory.setItem(PROFILE_SLOTS[i], makeItem(Material.GRAY_STAINED_GLASS_PANE,
                        "§7" + PROFILE_NAMES[i],
                        List.of("§7Empty profile slot")));
            }
        }

        inventory.setItem(INFO_SLOT, makeItem(Material.BOOK, "§6Profile Management",
                List.of("§7Profiles: §f" + PROFILE_NAMES.length)));

        inventory.setItem(CLOSE_SLOT, makeItem(Material.BARRIER, "§cClose", null));
    }

    private static ItemStack makeItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
