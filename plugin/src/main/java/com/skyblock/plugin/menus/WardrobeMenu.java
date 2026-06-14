package com.skyblock.plugin.menus;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The Wardrobe menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §eWardrobe}. Slots
 * {@value #FIRST_SLOT}–{@value #LAST_SLOT} are interactive storage for armour
 * sets. The bottom row (slots 45–53) is decorated with
 * {@code PURPLE_STAINED_GLASS_PANE} and its clicks are cancelled.</p>
 *
 * <p>Contents are persisted on the player's {@link PlayerProfile} via
 * {@link ProfileManager}: loaded on open, saved on close.</p>
 */
public final class WardrobeMenu implements Listener {

    /** The inventory title (supports colour codes). */
    public static final String TITLE = "§eWardrobe";

    /** The total number of slots (6 rows). */
    public static final int SIZE = 54;

    /** First interactive slot, inclusive. */
    public static final int FIRST_SLOT = 0;

    /** Last interactive slot, inclusive. */
    public static final int LAST_SLOT = 44;

    /**
     * Opens the player's Wardrobe, drawing the border and populating the
     * interactive slots from the contents snapshot on their profile.
     *
     * @param player the player to show the wardrobe to
     */
    public static void open(Player player) {
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(player, SIZE, TITLE);

        ItemStack pane = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§r");
            pane.setItemMeta(meta);
        }
        for (int slot = LAST_SLOT + 1; slot < SIZE; slot++) {
            inventory.setItem(slot, pane);
        }

        ItemStack[] contents = profile.getWardrobeContents();
        if (contents != null) {
            for (int i = 0; i < contents.length && FIRST_SLOT + i <= LAST_SLOT; i++) {
                inventory.setItem(FIRST_SLOT + i, contents[i]);
            }
        }

        player.openInventory(inventory);
    }

    /**
     * Cancels clicks on the decorative border panes.
     *
     * @param event the inventory click event
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!TITLE.equals(event.getView().getTitle())) return;
        int raw = event.getRawSlot();
        if (raw > LAST_SLOT && raw < SIZE) {
            event.setCancelled(true);
        }
    }

    /**
     * Persists the interactive wardrobe slots back onto the player's profile
     * when the menu is closed.
     *
     * @param event the inventory close event
     */
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!TITLE.equals(event.getView().getTitle())) return;
        HumanEntity closer = event.getPlayer();
        if (!(closer instanceof Player player)) return;
        Inventory inventory = event.getInventory();
        ItemStack[] contents = new ItemStack[LAST_SLOT - FIRST_SLOT + 1];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = inventory.getItem(FIRST_SLOT + i);
        }
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.setWardrobeContents(contents);
    }
}
