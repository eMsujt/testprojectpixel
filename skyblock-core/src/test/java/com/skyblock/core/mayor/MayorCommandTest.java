package com.skyblock.core.mayor;

import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MayorManager.MayorCandidate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class MayorCommandTest {

    private MayorManager mayorManager;
    private MayorCommand command;
    private Command cmd;

    @BeforeEach
    void setUp() {
        mayorManager = MayorManager.getInstance();
        mayorManager.setCurrentMayor(null);
        command = new MayorCommand(mayorManager);
        cmd = mock(Command.class);
    }

    @Test
    void constructor_acceptsMayorManager() {
        assertDoesNotThrow(() -> new MayorCommand(MayorManager.getInstance()));
    }

    @Test
    void onCommand_nonPlayer_sendsMessage_andReturnsTrue() {
        CommandSender sender = mock(CommandSender.class);
        boolean result = command.onCommand(sender, cmd, "mayor", new String[0]);
        assertTrue(result);
        verify(sender).sendMessage("This command can only be used by players.");
    }

    @Test
    void onCommand_current_sendsCurrentMayor() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        boolean result = command.onCommand(player, cmd, "mayor", new String[]{"current"});

        assertTrue(result);
        verify(player, atLeastOnce()).sendMessage(anyString());
    }

    @Test
    void onCommand_perks_noMayor_sendsNoMayor() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        command.onCommand(player, cmd, "mayor", new String[]{"perks"});

        verify(player).sendMessage("There is no active mayor.");
    }

    @Test
    void onCommand_vote_missingArg_sendsUsage() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        command.onCommand(player, cmd, "mayor", new String[]{"vote"});

        verify(player).sendMessage("Usage: /mayor vote <mayor>");
    }

    @Test
    void onCommand_vote_invalidMayor_sendsError() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        command.onCommand(player, cmd, "mayor", new String[]{"vote", "NOTAMAYOR"});

        verify(player).sendMessage(contains("Unknown mayor"));
    }

    @Test
    void onCommand_vote_validMayor_recordsVote() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        command.onCommand(player, cmd, "mayor", new String[]{"vote", "paul"});

        assertEquals(MayorCandidate.PAUL, mayorManager.getVote(id));
    }

    @Test
    void onCommand_set_nonOp_deniesAccess() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.isOp()).thenReturn(false);

        command.onCommand(player, cmd, "mayor", new String[]{"set", "paul"});

        verify(player).sendMessage("You do not have permission to use this subcommand.");
    }

    @Test
    void onCommand_set_op_changesMayor() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.isOp()).thenReturn(true);

        command.onCommand(player, cmd, "mayor", new String[]{"set", "diana"});

        assertEquals(MayorCandidate.DIANA, mayorManager.getCurrentMayor());
    }

    @Test
    void onCommand_unknownSubcommand_sendsError() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        boolean result = command.onCommand(player, cmd, "mayor", new String[]{"bogus"});

        assertTrue(result);
        verify(player).sendMessage(contains("Unknown subcommand"));
    }

    @Test
    void onTabComplete_noArgs_returnsSubs() {
        Player player = mock(Player.class);
        List<String> completions = command.onTabComplete(player, cmd, "mayor", new String[]{""});
        assertFalse(completions.isEmpty());
    }

    @Test
    void onTabComplete_vote_returnsMayorNames() {
        Player player = mock(Player.class);
        List<String> completions = command.onTabComplete(player, cmd, "mayor", new String[]{"vote", ""});
        assertTrue(completions.contains("paul"));
        assertTrue(completions.contains("diana"));
    }

    @Test
    void onTabComplete_unknownSub_returnsEmpty() {
        Player player = mock(Player.class);
        List<String> completions = command.onTabComplete(player, cmd, "mayor", new String[]{"unknown", ""});
        assertTrue(completions.isEmpty());
    }
}
