package com.skyblock.plugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Abstract base class for chest-based menus.
 *
 * <p>A {@code Menu} is its own {@link InventoryHolder}, so a click listener can
 * recover it from {@link Inventory#getHolder()} and dispatch the click back to
 * the menu. Subclasses build their layout in {@link #build()} by calling
 * {@link #setItem(int, ItemStack, Consumer)} for each slot.</p>
 *
 * <p>Usage:
 * <pre>{@code
 * public class MyMenu extends Menu {
 *     public MyMenu() { super("My Menu", 3); }
 *     protected void build() {
 *         setItem(13, myItem, e -> e.getWhoClicked().sendMessage("clicked!"));
 *     }
 * }
 * new MyMenu().open(player);
 * }</pre>
 * </p>
 */
public abstract class Menu implements InventoryHolder {

    private final String title;
    private final int rows;
    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();
    private Inventory inventory;

    /**
     * Creates a menu with the given title and row count.
     *
     * @param title the inventory title (supports colour codes)
     * @param rows  the number of rows (1–6)
     * @throws IllegalArgumentException if rows is not between 1 and 6
     */
    protected Menu(String title, int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("rows must be between 1 and 6");
        }
        this.title = title != null ? title : "";
        this.rows = rows;
    }

    /**
     * Populates the menu's slots. Called once per {@link #open(Player)}.
     */
    protected abstract void build();

    /**
     * Places {@code item} in the given slot with an optional click handler.
     *
     * @param slot    the slot index (0-based)
     * @param item    the item to place
     * @param handler the click handler, or {@code null} for no action
     * @throws IllegalArgumentException if slot is out of range
     */
    protected void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        if (slot < 0 || slot >= rows * 9) {
            throw new IllegalArgumentException("slot out of range: " + slot);
        }
        items.put(slot, item);
        if (handler != null) {
            handlers.put(slot, handler);
        } else {
            handlers.remove(slot);
        }
    }

    /**
     * Places {@code item} in the given slot without a click handler.
     *
     * @param slot the slot index (0-based)
     * @param item the item to place
     */
    protected void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Builds the inventory (if needed) and opens it for {@code player}.
     *
     * @param player the player to show the menu to
     */
    public void open(Player player) {
        items.clear();
        handlers.clear();
        build();
        inventory = Bukkit.createInventory(this, rows * 9, title);
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }
        player.openInventory(inventory);
    }

    /**
     * Dispatches a click event to the registered handler for the clicked slot,
     * if any.
     *
     * @param event the click event
     */
    public void handleClick(InventoryClickEvent event) {
        Consumer<InventoryClickEvent> handler = handlers.get(event.getSlot());
        if (handler != null) {
            handler.accept(event);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
