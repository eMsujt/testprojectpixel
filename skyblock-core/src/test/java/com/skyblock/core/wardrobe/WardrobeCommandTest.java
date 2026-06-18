package com.skyblock.core.wardrobe;

import com.skyblock.core.manager.WardrobeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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

    @BeforeEach
    void setUp() {
        wardrobeManager = WardrobeManager.getInstance();
        command = new WardrobeCommand(wardrobeManager);
        cmd = mock(Command.class);
    }

    @Test
    void constructor_acceptsWardrobeManager() {
        assertDoesNotThrow(() -> new WardrobeCommand(WardrobeManager.getInstance()));
    }

    @Test
    void onCommand_nonPlayer_sendsMessage_andReturnsTrue() {
        CommandSender sender = mock(CommandSender.class);
        boolean result = command.onCommand(sender, cmd, "wardrobe", new String[0]);
        assertTrue(result);
        verify(sender).sendMessage("This command can only be used by players.");
    }

    @Test
    void onCommand_save_missingName_sendsUsage() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        command.onCommand(player, cmd, "wardrobe", new String[]{"save"});
        verify(player).sendMessage("Usage: /wardrobe save <name>");
    }

    @Test
    void onCommand_load_missingName_sendsUsage() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        command.onCommand(player, cmd, "wardrobe", new String[]{"load"});
        verify(player).sendMessage("Usage: /wardrobe load <name>");
    }

    @Test
    void onCommand_delete_missingName_sendsUsage() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        command.onCommand(player, cmd, "wardrobe", new String[]{"delete"});
        verify(player).sendMessage("Usage: /wardrobe delete <name>");
    }

    @Test
    void onCommand_list_noOutfits_sendsEmpty() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        command.onCommand(player, cmd, "wardrobe", new String[]{"list"});
        verify(player).sendMessage("You have no saved outfits.");
    }

    @Test
    void onCommand_save_withName_savesOutfit() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        PlayerInventory inv = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inv);
        when(inv.getArmorContents()).thenReturn(new ItemStack[4]);

        command.onCommand(player, cmd, "wardrobe", new String[]{"save", "set1"});
        verify(player).sendMessage("Outfit 'set1' saved.");
        wardrobeManager.reset(id);
    }

    @Test
    void onCommand_delete_unknownName_sendsNotFound() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        command.onCommand(player, cmd, "wardrobe", new String[]{"delete", "ghost"});
        verify(player).sendMessage("No outfit named 'ghost' found.");
    }

    @Test
    void onCommand_load_unknownName_sendsNotFound() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        command.onCommand(player, cmd, "wardrobe", new String[]{"load", "ghost"});
        verify(player).sendMessage("No outfit named 'ghost' found.");
    }

    @Test
    void onTabComplete_firstArg_returnsSubcommands() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        List<String> completions = command.onTabComplete(player, cmd, "wardrobe", new String[]{""});
        assertTrue(completions.contains("save"));
        assertTrue(completions.contains("load"));
        assertTrue(completions.contains("list"));
        assertTrue(completions.contains("delete"));
        assertTrue(completions.contains("slots"));
    }

    @Test
    void onCommand_unknownSubcommand_returnsTrue() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        boolean result = command.onCommand(player, cmd, "wardrobe", new String[]{"unknown"});
        assertTrue(result);
    }
}
