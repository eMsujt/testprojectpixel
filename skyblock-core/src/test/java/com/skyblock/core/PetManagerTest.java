package com.skyblock.core;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.manager.PetManager.PetType;
import com.skyblock.core.model.Rarity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PetManagerTest {

    private PetManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = PetManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @Test
    void addPet_storesThePet() {
        Pet pet = manager.addPet(playerId, PetType.ENDER_DRAGON, Rarity.LEGENDARY);
        assertNotNull(pet);
        assertEquals(PetType.ENDER_DRAGON, pet.type);
        assertEquals(Rarity.LEGENDARY, pet.rarity);
        List<Pet> pets = manager.getPets(playerId);
        assertEquals(1, pets.size());
        assertSame(pet, pets.get(0));
    }

    @Test
    void addPet_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.addPet(null, PetType.CHICKEN, Rarity.COMMON));
    }

    @Test
    void getPets_freshPlayer_returnsEmptyList() {
        assertTrue(manager.getPets(playerId).isEmpty());
    }

    @Test
    void removePet_existingPet_returnsTrueAndRemoves() {
        Pet pet = manager.addPet(playerId, PetType.CHICKEN, Rarity.COMMON);
        assertTrue(manager.removePet(playerId, pet.id));
        assertTrue(manager.getPets(playerId).isEmpty());
    }

    @Test
    void removePet_unknownPet_returnsFalse() {
        assertFalse(manager.removePet(playerId, UUID.randomUUID()));
    }

    @Test
    void equipPet_ownedPet_becomesActive() {
        Pet pet = manager.addPet(playerId, PetType.WOLF, Rarity.EPIC);
        assertTrue(manager.equipPet(playerId, pet.id));
        assertEquals(pet.id, manager.getActivePetId(playerId));
        assertSame(pet, manager.getActivePet(playerId));
    }

    @Test
    void equipPet_unownedPet_returnsFalse() {
        assertFalse(manager.equipPet(playerId, UUID.randomUUID()));
    }

    @Test
    void unequipPet_afterEquip_returnsTrueAndClears() {
        Pet pet = manager.addPet(playerId, PetType.WOLF, Rarity.EPIC);
        manager.equipPet(playerId, pet.id);
        assertTrue(manager.unequipPet(playerId));
        assertNull(manager.getActivePetId(playerId));
    }

    @Test
    void unequipPet_noActivePet_returnsFalse() {
        assertFalse(manager.unequipPet(playerId));
    }

    @Test
    void removePet_equippedPet_alsoUnequips() {
        Pet pet = manager.addPet(playerId, PetType.WOLF, Rarity.EPIC);
        manager.equipPet(playerId, pet.id);
        assertTrue(manager.removePet(playerId, pet.id));
        assertNull(manager.getActivePetId(playerId));
    }

    @Test
    void addExperience_accumulates_andReturnsTotal() {
        manager.addExperience(playerId, PetType.CHICKEN, 60L);
        long total = manager.addExperience(playerId, PetType.CHICKEN, 40L);
        assertEquals(100L, total);
        assertEquals(100L, manager.getExperience(playerId, PetType.CHICKEN));
    }

    @Test
    void addExperience_negative_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.addExperience(playerId, PetType.CHICKEN, -1L));
    }

    @Test
    void getLevel_freshPlayer_isLevelOne() {
        assertEquals(1, manager.getLevel(playerId, PetType.CHICKEN));
    }

    @Test
    void getLevel_atFirstThreshold_isLevelTwo() {
        // COMMON level-2 threshold = 100
        manager.addExperience(playerId, PetType.CHICKEN, 100L);
        assertEquals(2, manager.getLevel(playerId, PetType.CHICKEN));
    }

    @Test
    void addPetXp_noActivePet_returnsMinusOne() {
        assertEquals(-1L, manager.addPetXp(playerId, 50L));
    }

    @Test
    void addPetXp_activePet_accumulatesAndTracksLevel() {
        Pet pet = manager.addPet(playerId, PetType.CHICKEN, Rarity.COMMON);
        manager.equipPet(playerId, pet.id);
        assertEquals(100L, manager.addPetXp(playerId, 100L));
        assertEquals(100L, manager.getPetXp(playerId));
        assertEquals(2, manager.getPetLevel(playerId));
    }

    @Test
    void reset_removesPlayerData() {
        manager.addPet(playerId, PetType.CHICKEN, Rarity.COMMON);
        assertTrue(manager.reset(playerId));
        assertTrue(manager.getPets(playerId).isEmpty());
    }

    @Test
    void reset_unknownPlayer_returnsFalse() {
        assertFalse(manager.reset(UUID.randomUUID()));
    }
}
