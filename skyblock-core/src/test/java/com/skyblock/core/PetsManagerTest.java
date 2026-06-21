package com.skyblock.core;

import com.skyblock.core.manager.PetsManager;
import com.skyblock.core.manager.PetsManager.PetData;
import com.skyblock.core.manager.PetsManager.PetRarity;
import com.skyblock.core.manager.PetsManager.PetType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PetsManagerTest {

    private PetsManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = PetsManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        manager.reset(playerId);
    }

    // --- getOwner / getPets on fresh player ---

    @Test
    void getPets_freshPlayer_returnsEmptyList() {
        assertTrue(manager.getPets(playerId).isEmpty());
    }

    @Test
    void getActivePet_freshPlayer_returnsNull() {
        assertNull(manager.getActivePet(playerId));
    }

    // --- addPet ---

    @Test
    void addPet_returnsPetWithCorrectFields() {
        PetData pet = manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
        assertNotNull(pet);
        assertEquals(playerId, pet.owner);
        assertEquals(PetType.BEE, pet.type);
        assertEquals(PetRarity.COMMON, pet.rarity);
    }

    @Test
    void addPet_appearsInGetPets() {
        PetData pet = manager.addPet(playerId, PetType.CAT, PetRarity.RARE);
        List<PetData> pets = manager.getPets(playerId);
        assertEquals(1, pets.size());
        assertEquals(pet.id, pets.get(0).id);
    }

    @Test
    void addPet_multiplePets_allAppear() {
        manager.addPet(playerId, PetType.WOLF, PetRarity.COMMON);
        manager.addPet(playerId, PetType.TIGER, PetRarity.EPIC);
        manager.addPet(playerId, PetType.GRIFFIN, PetRarity.LEGENDARY);
        assertEquals(3, manager.getPets(playerId).size());
    }

    @Test
    void addPet_nullPlayerId_throws() {
        assertThrows(NullPointerException.class,
                () -> manager.addPet(null, PetType.BEE, PetRarity.COMMON));
    }

    @Test
    void addPet_nullType_throws() {
        assertThrows(NullPointerException.class,
                () -> manager.addPet(playerId, null, PetRarity.COMMON));
    }

    @Test
    void addPet_nullRarity_throws() {
        assertThrows(NullPointerException.class,
                () -> manager.addPet(playerId, PetType.BEE, null));
    }

    @Test
    void addPet_freshExperience_isZero() {
        PetData pet = manager.addPet(playerId, PetType.RABBIT, PetRarity.UNCOMMON);
        assertEquals(0L, pet.getExperience());
    }

    @Test
    void addPet_freshLevel_isOne() {
        PetData pet = manager.addPet(playerId, PetType.RABBIT, PetRarity.UNCOMMON);
        assertEquals(1, pet.getLevel());
    }

    // --- removePet ---

    @Test
    void removePet_existingPet_returnsTrue() {
        PetData pet = manager.addPet(playerId, PetType.ZOMBIE, PetRarity.COMMON);
        assertTrue(manager.removePet(playerId, pet.id));
    }

    @Test
    void removePet_existingPet_nolongerInList() {
        PetData pet = manager.addPet(playerId, PetType.ZOMBIE, PetRarity.COMMON);
        manager.removePet(playerId, pet.id);
        assertTrue(manager.getPets(playerId).isEmpty());
    }

    @Test
    void removePet_unknownPet_returnsFalse() {
        assertFalse(manager.removePet(playerId, UUID.randomUUID()));
    }

    @Test
    void removePet_activePet_clearsActivePet() {
        PetData pet = manager.addPet(playerId, PetType.LION, PetRarity.EPIC);
        manager.setActivePet(playerId, pet.id);
        manager.removePet(playerId, pet.id);
        assertNull(manager.getActivePet(playerId));
    }

    @Test
    void removePet_nonActivePet_doesNotClearActivePet() {
        PetData active = manager.addPet(playerId, PetType.LION, PetRarity.EPIC);
        PetData other  = manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
        manager.setActivePet(playerId, active.id);
        manager.removePet(playerId, other.id);
        assertEquals(active.id, manager.getActivePet(playerId).id);
    }

    // --- setActivePet / getActivePet ---

    @Test
    void setActivePet_validPet_getActivePetReturnsIt() {
        PetData pet = manager.addPet(playerId, PetType.PHOENIX, PetRarity.LEGENDARY);
        manager.setActivePet(playerId, pet.id);
        PetData active = manager.getActivePet(playerId);
        assertNotNull(active);
        assertEquals(pet.id, active.id);
    }

    @Test
    void setActivePet_null_clearsActivePet() {
        PetData pet = manager.addPet(playerId, PetType.PHOENIX, PetRarity.LEGENDARY);
        manager.setActivePet(playerId, pet.id);
        manager.setActivePet(playerId, null);
        assertNull(manager.getActivePet(playerId));
    }

    @Test
    void setActivePet_switchBetweenPets() {
        PetData a = manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
        PetData b = manager.addPet(playerId, PetType.CAT, PetRarity.RARE);
        manager.setActivePet(playerId, a.id);
        manager.setActivePet(playerId, b.id);
        assertEquals(b.id, manager.getActivePet(playerId).id);
    }

    @Test
    void setActivePet_unknownPetId_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.setActivePet(playerId, UUID.randomUUID()));
    }

    @Test
    void setActivePet_otherPlayersId_throws() {
        UUID other = UUID.randomUUID();
        PetData pet = manager.addPet(other, PetType.BEE, PetRarity.COMMON);
        try {
            assertThrows(IllegalArgumentException.class,
                    () -> manager.setActivePet(playerId, pet.id));
        } finally {
            manager.reset(other);
        }
    }

    // --- addExperience ---

    @Test
    void addExperience_accumulates() {
        PetData pet = manager.addPet(playerId, PetType.WOLF, PetRarity.RARE);
        manager.addExperience(playerId, pet.id, 500L);
        manager.addExperience(playerId, pet.id, 300L);
        assertEquals(800L, pet.getExperience());
    }

    @Test
    void addExperience_negativeAmount_throws() {
        PetData pet = manager.addPet(playerId, PetType.WOLF, PetRarity.RARE);
        assertThrows(IllegalArgumentException.class,
                () -> manager.addExperience(playerId, pet.id, -1L));
    }

    @Test
    void addExperience_unknownPet_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.addExperience(playerId, UUID.randomUUID(), 100L));
    }

    @Test
    void addExperience_zero_isNoop() {
        PetData pet = manager.addPet(playerId, PetType.WOLF, PetRarity.RARE);
        long result = manager.addExperience(playerId, pet.id, 0L);
        assertEquals(0L, result);
        assertEquals(0L, pet.getExperience());
    }

    @Test
    void addExperience_capsAtMaxXpTable() {
        PetData pet = manager.addPet(playerId, PetType.WOLF, PetRarity.RARE);
        manager.addExperience(playerId, pet.id, Long.MAX_VALUE / 2);
        manager.addExperience(playerId, pet.id, Long.MAX_VALUE / 2);
        assertEquals(PetsManager.MAX_LEVEL, pet.getLevel());
    }

    // --- PetData.getLevel ---

    @Test
    void getLevel_afterEnoughXp_raisesLevel() {
        PetData pet = manager.addPet(playerId, PetType.ENDERMAN, PetRarity.EPIC);
        // XP_TABLE[1] = 100 + 1*1*2.5 = 102 -> cumulative = 102; adding 103 should push to level 2
        manager.addExperience(playerId, pet.id, 103L);
        assertTrue(pet.getLevel() >= 2);
    }

    @Test
    void getLevel_maxLevel_isHundred() {
        PetData pet = manager.addPet(playerId, PetType.GOLDEN_DRAGON, PetRarity.LEGENDARY);
        manager.addExperience(playerId, pet.id, Long.MAX_VALUE / 2);
        assertEquals(PetsManager.MAX_LEVEL, pet.getLevel());
    }

    // --- PetData.getDisplayName ---

    @Test
    void getDisplayName_containsTypeDisplayName() {
        PetData pet = manager.addPet(playerId, PetType.ENDER_DRAGON, PetRarity.LEGENDARY);
        assertTrue(pet.getDisplayName().contains("Ender Dragon"));
    }

    @Test
    void getDisplayName_containsLevel() {
        PetData pet = manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
        assertTrue(pet.getDisplayName().contains("[Lvl 1]"));
    }

    @Test
    void getDisplayName_containsRarityColorCode() {
        PetData pet = manager.addPet(playerId, PetType.PHOENIX, PetRarity.LEGENDARY);
        assertTrue(pet.getDisplayName().contains("§6"));
    }

    // --- PetRarity.getDisplayName ---

    @Test
    void rarityDisplayName_capitalisedCorrectly() {
        assertEquals("§6Legendary", PetRarity.LEGENDARY.getDisplayName());
    }

    @Test
    void rarityColorCode_common_isWhite() {
        assertEquals("§f", PetRarity.COMMON.getColorCode());
    }

    // --- reset ---

    @Test
    void reset_clearsPets() {
        manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
        manager.reset(playerId);
        assertTrue(manager.getPets(playerId).isEmpty());
    }

    @Test
    void reset_clearsActivePet() {
        PetData pet = manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
        manager.setActivePet(playerId, pet.id);
        manager.reset(playerId);
        assertNull(manager.getActivePet(playerId));
    }

    @Test
    void reset_returnsTrue_whenDataExisted() {
        manager.addPet(playerId, PetType.BEE, PetRarity.COMMON);
        assertTrue(manager.reset(playerId));
    }

    @Test
    void reset_returnsFalse_whenNoData() {
        assertFalse(manager.reset(playerId));
    }

    @Test
    void reset_doesNotAffectOtherPlayers() {
        UUID other = UUID.randomUUID();
        try {
            manager.addPet(other, PetType.CAT, PetRarity.RARE);
            manager.reset(playerId);
            assertEquals(1, manager.getPets(other).size());
        } finally {
            manager.reset(other);
        }
    }
}
