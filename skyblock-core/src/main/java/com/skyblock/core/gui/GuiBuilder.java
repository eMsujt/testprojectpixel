package com.skyblock.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Fluent builder for constructing chest {@link Inventory} GUIs backed by a
 * simple {@link InventoryHolder}.
 *
 * <p>Usage:
 * <pre>{@code
 * Inventory inv = GuiBuilder.create("My Menu", 3)
 *         .setItem(13, myItem, e -> e.getWhoClicked().sendMessage("clicked!"))
 *         .fill(fillerItem)
 *         .build();
 * player.openInventory(inv);
 * }</pre>
 * </p>
 */
public final class GuiBuilder {

    /** Simple holder so the built inventory can identify itself. */
    public static final class GuiHolder implements InventoryHolder {

        private Inventory inventory;
        private final Map<Integer, Consumer<InventoryClickEvent>> handlers;

        private GuiHolder(Map<Integer, Consumer<InventoryClickEvent>> handlers) {
            this.handlers = new HashMap<>(handlers);
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        void setInventory(Inventory inventory) {
            this.inventory = inventory;
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
    }

    private final String title;
    private final int rows;
    private final ItemStack[] contents;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    private GuiBuilder(String title, int rows) {
        this.title = title;
        this.rows = rows;
        this.contents = new ItemStack[rows * 9];
    }

    /**
     * Creates a new {@code GuiBuilder} for a chest inventory with the given title
     * and row count.
     *
     * @param title the inventory title (supports {@link ChatColor} codes)
     * @param rows  the number of rows (1–6)
     * @return a new builder instance
     * @throws IllegalArgumentException if rows is not between 1 and 6
     */
    public static GuiBuilder create(String title, int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("rows must be between 1 and 6");
        }
        return new GuiBuilder(title != null ? title : "", rows);
    }

    /**
     * Places {@code item} in the given slot with an optional click handler.
     *
     * @param slot    the slot index (0-based)
     * @param item    the item to place
     * @param handler the click handler, or {@code null} for no action
     * @return this builder
     * @throws IllegalArgumentException if slot is out of range
     */
    public GuiBuilder setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        if (slot < 0 || slot >= contents.length) {
            throw new IllegalArgumentException("slot out of range: " + slot);
        }
        contents[slot] = item;
        if (handler != null) {
            handlers.put(slot, handler);
        }
        return this;
    }

    /**
     * Places {@code item} in the given slot without a click handler.
     *
     * @param slot the slot index (0-based)
     * @param item the item to place
     * @return this builder
     */
    public GuiBuilder setItem(int slot, ItemStack item) {
        return setItem(slot, item, null);
    }

    /**
     * Fills all currently empty slots with {@code filler}.
     *
     * @param filler the item to use as a filler
     * @return this builder
     */
    public GuiBuilder fill(ItemStack filler) {
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null) {
                contents[i] = filler;
            }
        }
        return this;
    }

    /**
     * Builds and returns the {@link Inventory}.
     *
     * <p>The returned inventory is backed by a {@link GuiHolder}; cast the
     * holder to {@code GuiHolder} and call {@link GuiHolder#handleClick} from
     * an {@link org.bukkit.event.inventory.InventoryClickEvent} listener to
     * dispatch slot handlers.</p>
     *
     * @return the constructed inventory
     */
    public Inventory build() {
        GuiHolder holder = new GuiHolder(handlers);
        Inventory inv = Bukkit.createInventory(holder, rows * 9, title);
        inv.setContents(contents);
        holder.setInventory(inv);
        return inv;
    }

    /**
     * Convenience method: builds the inventory and returns its {@link GuiHolder}.
     *
     * @return the {@link GuiHolder} whose {@link GuiHolder#getInventory()} is
     *         ready to open
     */
    public GuiHolder buildHolder() {
        Inventory inv = build();
        return (GuiHolder) Objects.requireNonNull(inv.getHolder());
    }
}
