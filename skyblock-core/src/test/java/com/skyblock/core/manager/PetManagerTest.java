package com.skyblock.core.manager;

import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.manager.PetManager.PetItem;
import com.skyblock.core.manager.PetManager.PetType;
import com.skyblock.core.model.Rarity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PetManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        PetManager a = PetManager.getInstance();
        PetManager b = PetManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(PetManager.getInstance());
    }

    @Test
    void petRarity_LegendaryDisplayName() {
        assertEquals("Legendary", Rarity.LEGENDARY.getDisplayName());
    }

    // --- pet registry with rarities ---

    @Test
    void registry_PetTypeCarriesDefaultRarityAndDisplayName() {
        assertEquals(Rarity.LEGENDARY, PetType.ENDER_DRAGON.defaultRarity);
        assertEquals("Ender Dragon", PetType.ENDER_DRAGON.getDisplayName());
        assertEquals(Rarity.COMMON, PetType.CHICKEN.defaultRarity);
    }

    @Test
    void addPet_StoresPetInPlayerCollection() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetType.TIGER, Rarity.EPIC);
        assertNotNull(pet.id);
        assertEquals(PetType.TIGER, pet.type);
        assertEquals(Rarity.EPIC, pet.rarity);
        assertTrue(mgr.getPets(player).stream().anyMatch(p -> p.id.equals(pet.id)));
        mgr.reset(player);
    }

    // --- pet XP / leveling curve ---

    @Test
    void xp_NewPetTypeIsLevelOne() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        assertEquals(0L, mgr.getExperience(player, PetType.BEE));
        assertEquals(1, mgr.getLevel(player, PetType.BEE));
        mgr.reset(player);
    }

    @Test
    void xp_AddingExperienceRaisesLevelAndAccumulates() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        // COMMON: 100 cumulative XP reaches level 2.
        long total = mgr.addExperience(player, PetType.CHICKEN, 100L);
        assertEquals(100L, total);
        assertEquals(2, mgr.getLevel(player, PetType.CHICKEN));
        assertEquals(150L, mgr.addExperience(player, PetType.CHICKEN, 50L));
        mgr.reset(player);
    }

    @Test
    void xp_NegativeExperienceRejected() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.addExperience(player, PetType.CHICKEN, -1L));
        mgr.reset(player);
    }

    @Test
    void xp_LevelCapsAtMaxLevel() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addExperience(player, PetType.CHICKEN, Long.MAX_VALUE);
        assertEquals(PetManager.MAX_LEVEL, mgr.getLevel(player, PetType.CHICKEN));
        mgr.reset(player);
    }

    // --- pet items ---

    @Test
    void heldItem_SetGetAndClear() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetType.TIGER, Rarity.EPIC);
        assertEquals(PetItem.NONE, mgr.getHeldItem(player, pet.id));
        assertTrue(mgr.setHeldItem(player, pet.id, PetItem.SHARPENED_CLAWS));
        assertEquals(PetItem.SHARPENED_CLAWS, mgr.getHeldItem(player, pet.id));
        int[] bonus = mgr.getHeldItemBonus(player, pet.id);
        assertEquals(PetItem.SHARPENED_CLAWS.strengthBonus, bonus[1]);
        assertTrue(mgr.setHeldItem(player, pet.id, PetItem.NONE));
        assertEquals(PetItem.NONE, mgr.getHeldItem(player, pet.id));
        mgr.reset(player);
    }

    @Test
    void heldItem_RejectsUnknownPet() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        assertFalse(mgr.setHeldItem(player, UUID.randomUUID(), PetItem.IRON_CLAWS));
        mgr.reset(player);
    }

    // --- active-pet selection ---

    @Test
    void activePet_EquipUnequipAndRemoval() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetType.WOLF, Rarity.LEGENDARY);
        assertNull(mgr.getActivePet(player));
        assertTrue(mgr.equipPet(player, pet.id));
        assertEquals(pet.id, mgr.getActivePetId(player));
        assertSame(pet.type, mgr.getActivePet(player).type);
        // Removing the active pet clears the equip slot.
        assertTrue(mgr.removePet(player, pet.id));
        assertNull(mgr.getActivePet(player));
        mgr.reset(player);
    }

    @Test
    void activePet_EquipRejectsUnownedPet() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        assertFalse(mgr.equipPet(player, UUID.randomUUID()));
        assertFalse(mgr.unequipPet(player));
        mgr.reset(player);
    }
}
