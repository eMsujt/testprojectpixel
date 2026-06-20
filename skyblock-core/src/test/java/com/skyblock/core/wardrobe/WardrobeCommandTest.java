package com.skyblock.core.wardrobe;

import com.skyblock.core.command.WardrobeCommand;
import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.menu.WardrobeMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WardrobeCommandTest {

    private WardrobeManager wardrobeManager;
    private WardrobeCommand command;
    private Command cmd;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        wardrobeManager = WardrobeManager.getInstance();
        command = new WardrobeCommand(wardrobeManager);
        cmd = mock(Command.class);
        playerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        wardrobeManager.reset(playerId);
    }

    // ── constructor ─────────────────────────────────────────────────────────

    @Test
    void constructor_acceptsWardrobeManager() {
        assertDoesNotThrow(() -> new WardrobeCommand(WardrobeManager.getInstance()));
    }

    // ── non-player ──────────────────────────────────────────────────────────

    @Test
    void onCommand_nonPlayer_sendsMessage_andReturnsTrue() {
        CommandSender sender = mock(CommandSender.class);
        boolean result = command.onCommand(sender, cmd, "wardrobe", new String[0]);
        assertTrue(result);
        verify(sender).sendMessage("This command can only be used by players.");
    }

    // ── save ────────────────────────────────────────────────────────────────

    @Test
    void onCommand_save_missingName_sendsUsage() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"save"});
        verify(player).sendMessage("Usage: /wardrobe save <name>");
    }

    @Test
    void onCommand_save_withName_savesOutfit_andConfirms() {
        Player player = mockPlayer();
        PlayerInventory inv = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inv);
        when(inv.getArmorContents()).thenReturn(new ItemStack[4]);

        command.onCommand(player, cmd, "wardrobe", new String[]{"save", "set1"});
        verify(player).sendMessage("Outfit 'set1' saved.");
        assertTrue(wardrobeManager.getOutfitNames(playerId).contains("set1"));
    }

    // ── load ────────────────────────────────────────────────────────────────

    @Test
    void onCommand_load_missingName_sendsUsage() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"load"});
        verify(player).sendMessage("Usage: /wardrobe load <name>");
    }

    @Test
    void onCommand_load_unknownName_sendsNotFound() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"load", "ghost"});
        verify(player).sendMessage("No outfit named 'ghost' found.");
    }

    @Test
    void onCommand_load_knownName_equipsAndConfirms() {
        wardrobeManager.saveOutfit(playerId, "set1", new ItemStack[4]);
        Player player = mockPlayer();
        PlayerInventory inv = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inv);

        command.onCommand(player, cmd, "wardrobe", new String[]{"load", "set1"});
        verify(player).sendMessage("Outfit 'set1' loaded.");
        verify(inv).setArmorContents(any(ItemStack[].class));
    }

    // ── delete ──────────────────────────────────────────────────────────────

    @Test
    void onCommand_delete_missingName_sendsUsage() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"delete"});
        verify(player).sendMessage("Usage: /wardrobe delete <name>");
    }

    @Test
    void onCommand_delete_unknownName_sendsNotFound() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"delete", "ghost"});
        verify(player).sendMessage("No outfit named 'ghost' found.");
    }

    @Test
    void onCommand_delete_knownName_deletesAndConfirms() {
        wardrobeManager.saveOutfit(playerId, "set1", new ItemStack[4]);
        Player player = mockPlayer();

        command.onCommand(player, cmd, "wardrobe", new String[]{"delete", "set1"});
        verify(player).sendMessage("Outfit 'set1' deleted.");
        assertFalse(wardrobeManager.getOutfitNames(playerId).contains("set1"));
    }

    // ── list ────────────────────────────────────────────────────────────────

    @Test
    void onCommand_list_noOutfits_sendsEmpty() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"list"});
        verify(player).sendMessage("You have no saved outfits.");
    }

    // ── slots ───────────────────────────────────────────────────────────────

    @Test
    void onCommand_slots_reportsDefaultUnlockedCount() {
        Player player = mockPlayer();
        command.onCommand(player, cmd, "wardrobe", new String[]{"slots"});
        verify(player).sendMessage("You have " + WardrobeManager.DEFAULT_UNLOCKED_SLOTS + " unlocked wardrobe slots.");
    }

    // ── WardrobeMenu slot layout constants ──────────────────────────────────

    @Test
    void wardrobeMenu_slotCount_isNine() {
        assertEquals(9, WardrobeMenu.SLOT_COUNT);
    }

    @Test
    void wardrobeManager_defaultUnlockedSlots_isTwo() {
        // SLOT_1 and SLOT_2 are always unlocked; SLOT_3+ require explicit unlock
        assertEquals(2, WardrobeManager.DEFAULT_UNLOCKED_SLOTS);
        assertTrue(wardrobeManager.isSlotUnlocked(playerId, WardrobeManager.WardrobeSlot.SLOT_1));
        assertTrue(wardrobeManager.isSlotUnlocked(playerId, WardrobeManager.WardrobeSlot.SLOT_2));
        assertFalse(wardrobeManager.isSlotUnlocked(playerId, WardrobeManager.WardrobeSlot.SLOT_3));
    }

    // ── tab completion ──────────────────────────────────────────────────────

    @Test
    void onTabComplete_firstArg_returnsSubcommands() {
        Player player = mockPlayer();
        List<String> completions = command.onTabComplete(player, cmd, "wardrobe", new String[]{""});
        assertTrue(completions.contains("save"));
        assertTrue(completions.contains("load"));
        assertTrue(completions.contains("list"));
        assertTrue(completions.contains("delete"));
        assertTrue(completions.contains("slots"));
    }

    // ── unknown subcommand ──────────────────────────────────────────────────

    @Test
    void onCommand_unknownSubcommand_returnsTrue() {
        Player player = mockPlayer();
        boolean result = command.onCommand(player, cmd, "wardrobe", new String[]{"unknown"});
        assertTrue(result);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Player mockPlayer() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);
        return player;
    }
}
