package com.skyblock.core.menu;

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
