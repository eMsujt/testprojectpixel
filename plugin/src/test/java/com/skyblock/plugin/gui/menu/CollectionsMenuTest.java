package com.skyblock.plugin.gui.menu;

import com.skyblock.core.menu.CollectionsMenu;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CollectionsMenuTest {

    private static final UUID TEST_PLAYER_ID = UUID.randomUUID();

    @Test
    void testCollectionsMenuTitle() {
        CollectionsMenu menu = new CollectionsMenu(TEST_PLAYER_ID);
        assertEquals("§6Collections", menu.getTitle());
    }

    @Test
    void testCollectionsMenuRowCount() {
        CollectionsMenu menu = new CollectionsMenu(TEST_PLAYER_ID);
        assertEquals(6, menu.getRows());
    }

    @Test
    void testCollectionsMenuHasCorrectSlotCount() {
        CollectionsMenu menu = new CollectionsMenu(TEST_PLAYER_ID);
        assertEquals(54, menu.getInventory().getSize());
    }

    @Test
    void testBorderFilledTopRow() {
        CollectionsMenu menu = new CollectionsMenu(TEST_PLAYER_ID);
        Inventory inv = menu.getInventory();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack item = inv.getItem(slot);
            assertNotNull(item);
            assertEquals(Material.GRAY_STAINED_GLASS_PANE, item.getType());
        }
    }

    @Test
    void testBorderFilledBottomRow() {
        CollectionsMenu menu = new CollectionsMenu(TEST_PLAYER_ID);
        Inventory inv = menu.getInventory();
        for (int slot = 45; slot < 54; slot++) {
            ItemStack item = inv.getItem(slot);
            assertNotNull(item);
            assertEquals(Material.GRAY_STAINED_GLASS_PANE, item.getType());
        }
    }

    @Test
    void testBorderPaneHasEmptyName() {
        CollectionsMenu menu = new CollectionsMenu(TEST_PLAYER_ID);
        Inventory inv = menu.getInventory();
        ItemStack borderPane = inv.getItem(0);
        assertTrue(borderPane.hasItemMeta());
        assertEquals("§r", borderPane.getItemMeta().getDisplayName());
    }

    @Test
    void testCategorySlotsFilled() {
        CollectionsMenu menu = new CollectionsMenu(TEST_PLAYER_ID);
        Inventory inv = menu.getInventory();
        int[] categorySlots = {20, 21, 22, 23, 24};
        for (int slot : categorySlots) {
            ItemStack item = inv.getItem(slot);
            assertNotNull(item);
            assertNotEquals(Material.AIR, item.getType());
        }
    }

    @Test
    void testCategoryItemsHaveDisplayNames() {
        CollectionsMenu menu = new CollectionsMenu(TEST_PLAYER_ID);
        Inventory inv = menu.getInventory();
        int[] categorySlots = {20, 21, 22, 23, 24};
        for (int slot : categorySlots) {
            ItemStack item = inv.getItem(slot);
            assertTrue(item.hasItemMeta());
            String displayName = item.getItemMeta().getDisplayName();
            assertNotNull(displayName);
            assertNotEquals("", displayName);
        }
    }

    @Test
    void testCategoryItemsHaveLore() {
        CollectionsMenu menu = new CollectionsMenu(TEST_PLAYER_ID);
        Inventory inv = menu.getInventory();
        int[] categorySlots = {20, 21, 22, 23, 24};
        for (int slot : categorySlots) {
            ItemStack item = inv.getItem(slot);
            assertTrue(item.hasItemMeta());
            assertNotNull(item.getItemMeta().getLore());
            assertTrue(item.getItemMeta().getLore().size() >= 2);
        }
    }

    @Test
    void testInventoryCreatedOnGetInventory() {
        CollectionsMenu menu = new CollectionsMenu(TEST_PLAYER_ID);
        assertNotNull(menu.getInventory());
        assertEquals("§6Collections", menu.getInventory().getTitle());
    }

    @Test
    void testInventoryItemStackSizeOne() {
        CollectionsMenu menu = new CollectionsMenu(TEST_PLAYER_ID);
        Inventory inv = menu.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                assertEquals(1, item.getAmount());
            }
        }
    }
}
