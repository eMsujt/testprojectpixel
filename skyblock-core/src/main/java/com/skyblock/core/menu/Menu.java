package com.skyblock.core.menu;

import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Abstract base for all SkyBlock GUI menus managed by {@link MenuManager}.
 *
 * <p>Subclasses implement {@link #build()} to populate slots via
 * {@link #setItem(int, ItemStack, Consumer)}. Call {@link #open(Player)} to
 * build and display the inventory; {@link #getInventory()} builds lazily so
 * the menu can be inspected in tests without a live player.</p>
 */
public abstract class Menu implements InventoryHolder {

    private final String title;
    private final int rows;
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();
    private Inventory inventory;

    protected Menu(String title, int rows) {
        if (rows < 1 || rows > 6) throw new IllegalArgumentException("rows must be between 1 and 6");
        this.title = title != null ? title : "";
        this.rows = rows;
    }

    protected abstract void build();

    protected void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        if (slot < 0 || slot >= rows * 9) throw new IllegalArgumentException("slot out of range: " + slot);
        items.put(slot, item);
        if (handler != null) handlers.put(slot, handler);
        else handlers.remove(slot);
    }

    protected void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /** Fills the full border (top &amp; bottom rows, left &amp; right columns) with a blank gray pane. */
    protected void drawBorder() {
        drawBorder(Material.BLACK_STAINED_GLASS_PANE);
    }

    /** Fills the full border with a blank pane of the given material (for themed menus). */
    protected void drawBorder(Material paneMaterial) {
        ItemStack pane = new ItemBuilder(paneMaterial).displayName("§r").build();
        int size = rows * 9;
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = size - 9; slot < size; slot++) setItem(slot, pane);
        for (int row = 1; row < rows - 1; row++) {
            setItem(row * 9, pane);
            setItem(row * 9 + 8, pane);
        }
    }

    /**
     * Maps a 0-based content index to a slot in the inner 7-wide grid (skipping the 1-wide border),
     * filling left-to-right, top-to-bottom — so items form a clean centered grid instead of running
     * across the border columns.
     */
    protected int contentSlot(int index) {
        int cols = 7;
        return (index / cols + 1) * 9 + (index % cols + 1);
    }

    /** Number of inner content slots available for {@link #contentSlot(int)} in this menu. */
    protected int contentCapacity() {
        return Math.max(0, (rows - 2) * 7);
    }

    /**
     * Fills every still-empty slot with a blank black pane so each menu has a solid
     * Hypixel-style background (not just a border with empty gaps). Runs after
     * {@link #build()}, so it never overwrites content or buttons a menu placed.
     */
    private void fillEmptyBackground() {
        int size = rows * 9;
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < size; slot++) {
            items.putIfAbsent(slot, pane);
        }
    }

    public void open(Player player) {
        items.clear();
        handlers.clear();
        build();
        fillEmptyBackground();
        inventory = Bukkit.createInventory(this, rows * 9, title);
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }
        player.openInventory(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        Consumer<InventoryClickEvent> handler = handlers.get(event.getSlot());
        if (handler != null) handler.accept(event);
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null) {
            items.clear();
            handlers.clear();
            build();
            fillEmptyBackground();
            inventory = Bukkit.createInventory(this, rows * 9, title);
            for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                inventory.setItem(entry.getKey(), entry.getValue());
            }
        }
        return inventory;
    }

    public String getTitle() { return title; }
    public int getRows() { return rows; }
}
