package com.skyblock.core.pet;

import com.skyblock.core.command.PetCommand;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.manager.PetManager.PetType;
import com.skyblock.core.model.Rarity;
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

class PetCommandTest {

    private PetManager petManager;
    private PetCommand command;
    private Command cmd;

    @BeforeEach
    void setUp() {
        petManager = PetManager.getInstance();
        command = new PetCommand(petManager);
        cmd = mock(Command.class);
    }

    @Test
    void constructor_acceptsPetManager() {
        assertDoesNotThrow(() -> new PetCommand(PetManager.getInstance()));
    }

    @Test
    void onCommand_nonPlayer_sendsMessage_andReturnsTrue() {
        CommandSender sender = mock(CommandSender.class);
        boolean result = command.onCommand(sender, cmd, "pet", new String[0]);
        assertTrue(result);
        verify(sender).sendMessage("This command can only be used by players.");
    }

    @Test
    void onCommand_list_sendsHeaderAndAllPetTypes() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"list"});

        assertTrue(result);
        // header line + one line per PetType
        verify(player, times(1 + PetType.values().length)).sendMessage(anyString());
    }

    @Test
    void onCommand_equip_missingType_sendsUsage() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"equip"});

        assertTrue(result);
        verify(player).sendMessage("Usage: /pet equip <type> [rarity]");
    }

    @Test
    void onCommand_equip_invalidType_sendsError() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"equip", "NOTAPET"});

        assertTrue(result);
        verify(player).sendMessage(contains("Unknown pet type"));
    }

    @Test
    void onCommand_equip_validType_storesPetAndSendsConfirmation() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"equip", "chicken"});

        assertTrue(result);
        verify(player).sendMessage(contains("Equipped"));
        assertNotNull(petManager.getActivePet(id));
        petManager.reset(id);
    }

    @Test
    void onCommand_equip_validTypeAndRarity_usesSuppliedRarity() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        command.onCommand(player, cmd, "pet", new String[]{"equip", "tiger", "epic"});

        Pet active = petManager.getActivePet(id);
        assertNotNull(active);
        assertEquals(Rarity.EPIC, active.rarity);
        petManager.reset(id);
    }

    @Test
    void onCommand_unequip_noActivePet_sendsNoPetMessage() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        petManager.reset(id);

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"unequip"});

        assertTrue(result);
        verify(player).sendMessage("You have no active pet.");
    }

    @Test
    void onCommand_unequip_withActivePet_clearsPetAndSendsConfirmation() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        Pet pet = petManager.addPet(id, PetType.WOLF, Rarity.LEGENDARY);
        petManager.equipPet(id, pet.id);

        command.onCommand(player, cmd, "pet", new String[]{"unequip"});

        assertNull(petManager.getActivePet(id));
        verify(player).sendMessage("Pet unequipped.");
        petManager.reset(id);
    }

    @Test
    void onCommand_info_noActiveAndNoType_sendsNoPetMessage() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        petManager.reset(id);

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"info"});

        assertTrue(result);
        verify(player).sendMessage(contains("no active pet"));
    }

    @Test
    void onCommand_info_namedType_sendsLevelAndXp() {
        UUID id = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"info", "chicken"});

        assertTrue(result);
        verify(player, atLeast(2)).sendMessage(anyString());
    }

    @Test
    void onCommand_unknownSubcommand_sendsHelp() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        boolean result = command.onCommand(player, cmd, "pet", new String[]{"bogus"});

        assertTrue(result);
        verify(player, atLeast(1)).sendMessage(anyString());
    }

    // --- tab completion ---

    @Test
    void onTabComplete_noInput_returnsAllSubcommands() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{""});
        assertTrue(result.contains("list"));
        assertTrue(result.contains("equip"));
        assertTrue(result.contains("unequip"));
        assertTrue(result.contains("info"));
    }

    @Test
    void onTabComplete_prefixFiltering_narrowsToMatchingSubcommands() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{"li"});
        assertEquals(List.of("list"), result);
    }

    @Test
    void onTabComplete_equip_secondArg_returnsPetTypeNames() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{"equip", ""});
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(s -> s.equals(s.toLowerCase())));
    }

    @Test
    void onTabComplete_equip_thirdArg_returnsRarityNames() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{"equip", "chicken", ""});
        assertFalse(result.isEmpty());
        assertTrue(result.contains("common"));
        assertTrue(result.contains("legendary"));
    }

    @Test
    void onTabComplete_info_secondArg_returnsPetTypeNames() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{"info", ""});
        assertFalse(result.isEmpty());
    }

    @Test
    void onTabComplete_unknownSubcommand_returnsEmpty() {
        Player player = mock(Player.class);
        List<String> result = command.onTabComplete(player, cmd, "pet", new String[]{"equip", "chicken", "common", "extra"});
        assertTrue(result.isEmpty());
    }
}
