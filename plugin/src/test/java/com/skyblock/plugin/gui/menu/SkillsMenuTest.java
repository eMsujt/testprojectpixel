package com.skyblock.plugin.gui.menu;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SkillsMenuTest {

    private static final UUID TEST_PLAYER_ID = UUID.randomUUID();

    @Test
    void testSkillsMenuTitle() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        assertEquals("§aSkills", skillsMenu.getTitle());
    }

    @Test
    void testSkillsMenuRowCount() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        assertEquals(6, skillsMenu.getRows());
    }

    @Test
    void testSkillsMenuHasCorrectSlotCount() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        assertEquals(54, skillsMenu.getInventory().getSize());
    }

    @Test
    void testBorderFilledTopRow() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        Inventory inv = skillsMenu.getInventory();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack item = inv.getItem(slot);
            assertNotNull(item);
            assertEquals(Material.GRAY_STAINED_GLASS_PANE, item.getType());
        }
    }

    @Test
    void testBorderFilledBottomRow() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        Inventory inv = skillsMenu.getInventory();
        for (int slot = 45; slot < 54; slot++) {
            ItemStack item = inv.getItem(slot);
            assertNotNull(item);
            assertEquals(Material.GRAY_STAINED_GLASS_PANE, item.getType());
        }
    }

    @Test
    void testBorderPaneHasEmptyName() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        Inventory inv = skillsMenu.getInventory();
        ItemStack borderPane = inv.getItem(0);
        assertTrue(borderPane.hasItemMeta());
        assertEquals("§r", borderPane.getItemMeta().getDisplayName());
    }

    @Test
    void testSkillSlotsFilled() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        Inventory inv = skillsMenu.getInventory();
        int[] skillSlots = {10, 12, 14, 16, 19, 21, 23, 25, 28, 30, 32};
        for (int slot : skillSlots) {
            ItemStack item = inv.getItem(slot);
            assertNotNull(item);
            assertNotEquals(Material.AIR, item.getType());
        }
    }

    @Test
    void testSkillIconsAreMaterialPlayerHead() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        Inventory inv = skillsMenu.getInventory();
        int[] skillSlots = {10, 12, 14, 16, 19, 21, 23, 25, 28, 30, 32};
        for (int slot : skillSlots) {
            ItemStack item = inv.getItem(slot);
            assertEquals(Material.PLAYER_HEAD, item.getType());
        }
    }

    @Test
    void testSkillItemsHaveDisplayNames() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        Inventory inv = skillsMenu.getInventory();
        int[] skillSlots = {10, 12, 14, 16, 19, 21, 23, 25, 28, 30, 32};
        for (int slot : skillSlots) {
            ItemStack item = inv.getItem(slot);
            assertTrue(item.hasItemMeta());
            String displayName = item.getItemMeta().getDisplayName();
            assertNotNull(displayName);
            assertNotEquals("", displayName);
        }
    }

    @Test
    void testSkillItemsHaveLore() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        Inventory inv = skillsMenu.getInventory();
        int[] skillSlots = {10, 12, 14, 16, 19, 21, 23, 25, 28, 30, 32};
        for (int slot : skillSlots) {
            ItemStack item = inv.getItem(slot);
            assertTrue(item.hasItemMeta());
            assertNotNull(item.getItemMeta().getLore());
            assertTrue(item.getItemMeta().getLore().size() >= 2);
        }
    }

    @Test
    void testInventoryCreatedOnGetInventory() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        assertNotNull(skillsMenu.getInventory());
        assertEquals("§aSkills", skillsMenu.getInventory().getTitle());
    }

    @Test
    void testInventoryItemStackSizeOne() {
        SkillsMenu skillsMenu = new SkillsMenu(TEST_PLAYER_ID);
        Inventory inv = skillsMenu.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                assertEquals(1, item.getAmount());
            }
        }
    }
}
