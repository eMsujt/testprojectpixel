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
 * The Island Storage menu.
 *
 * <p>A 54-slot (6-row) chest titled {@code §aIsland Storage}. The slots
 * {@value #FIRST_SLOT}–{@value #LAST_SLOT} are fully interactive: the player may
 * freely move items in and out. The remaining bottom-row slots form a
 * {@code GREEN_STAINED_GLASS_PANE} border whose clicks are cancelled.</p>
 *
 * <p>Contents are persisted on the player's {@link PlayerProfile} (via
 * {@link ProfileManager}) as a per-slot {@code ItemStack[]} snapshot: a fresh
 * inventory is populated from the snapshot each time the storage is opened, and
 * the snapshot is rewritten when the player closes it (see
 * {@link #onClose(InventoryCloseEvent)}).</p>
 */
public final class StorageMenu implements Listener {

    /** The inventory title (supports colour codes). */
    public static final String TITLE = "§aIsland Storage";

    /** The total number of slots (6 rows). */
    public static final int SIZE = 54;

    /** First interactive (storage) slot, inclusive. */
    public static final int FIRST_SLOT = 0;

    /** Last interactive (storage) slot, inclusive. */
    public static final int LAST_SLOT = 44;

    /**
     * Opens the player's Island Storage, drawing the border and populating the
     * interactive slots from the contents snapshot persisted on their profile.
     *
     * @param player the player to show the storage to
     */
    public static void open(Player player) {
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(player, SIZE, TITLE);

        ItemStack pane = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§r");
            pane.setItemMeta(meta);
        }
        for (int slot = 0; slot < SIZE; slot++) {
            if (slot < FIRST_SLOT || slot > LAST_SLOT) {
                inventory.setItem(slot, pane);
            }
        }

        ItemStack[] contents = profile.getIslandStorageContents();
        if (contents != null) {
            for (int i = 0; i < contents.length && FIRST_SLOT + i <= LAST_SLOT; i++) {
                inventory.setItem(FIRST_SLOT + i, contents[i]);
            }
        }

        player.openInventory(inventory);
    }

    /**
     * Cancels clicks on the decorative border so its panes cannot be removed,
     * while leaving the interactive storage slots untouched.
     *
     * @param event the inventory click event
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!TITLE.equals(event.getView().getTitle())) return;
        int raw = event.getRawSlot();
        if (raw >= 0 && raw < SIZE && (raw < FIRST_SLOT || raw > LAST_SLOT)) {
            event.setCancelled(true);
        }
    }

    /**
     * Persists the interactive storage slots back onto the player's profile when
     * the menu is closed, so edits survive the inventory being discarded.
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
        profile.setIslandStorageContents(contents);
    }
}
