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

    // --- pet-XP leveling thresholds ---

    @Test
    void thresholds_CommonLevelBoundariesAreExact() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        // CHICKEN is COMMON: table is {100, 210, 330, ...}.
        mgr.addExperience(player, PetType.CHICKEN, 99L);
        assertEquals(1, mgr.getLevel(player, PetType.CHICKEN), "just below first threshold stays level 1");
        mgr.addExperience(player, PetType.CHICKEN, 1L); // 100 cumulative
        assertEquals(2, mgr.getLevel(player, PetType.CHICKEN), "reaching 100 hits level 2");
        mgr.addExperience(player, PetType.CHICKEN, 109L); // 209 cumulative
        assertEquals(2, mgr.getLevel(player, PetType.CHICKEN), "just below second threshold stays level 2");
        mgr.addExperience(player, PetType.CHICKEN, 1L); // 210 cumulative
        assertEquals(3, mgr.getLevel(player, PetType.CHICKEN), "reaching 210 hits level 3");
        mgr.reset(player);
    }

    @Test
    void thresholds_TableMatchesComputedLevel() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        long firstThreshold = PetManager.PET_XP_TABLE.get("COMMON")[0];
        mgr.addExperience(player, PetType.CHICKEN, firstThreshold - 1);
        assertEquals(1, mgr.getLevel(player, PetType.CHICKEN));
        mgr.addExperience(player, PetType.CHICKEN, 1L);
        assertEquals(2, mgr.getLevel(player, PetType.CHICKEN));
        mgr.reset(player);
    }

    // --- rarity progression ---

    @Test
    void rarityProgression_HigherRarityRequiresMoreXpPerLevel() {
        long[] common = PetManager.PET_XP_TABLE.get("COMMON");
        long[] uncommon = PetManager.PET_XP_TABLE.get("UNCOMMON");
        long[] rare = PetManager.PET_XP_TABLE.get("RARE");
        long[] epic = PetManager.PET_XP_TABLE.get("EPIC");
        long[] legendary = PetManager.PET_XP_TABLE.get("LEGENDARY");
        // The XP needed to reach level 2 strictly increases with rarity.
        assertTrue(common[0] < uncommon[0]);
        assertTrue(uncommon[0] < rare[0]);
        assertTrue(rare[0] < epic[0]);
        assertTrue(epic[0] < legendary[0]);
    }

    @Test
    void rarityProgression_HigherRarityScalesStatsLowerAtEqualXp() {
        PetManager mgr = PetManager.getInstance();
        UUID epicPlayer = UUID.randomUUID();
        UUID legendaryPlayer = UUID.randomUUID();
        long xp = 100_000L;
        // TIGER carries a strength base stat; rarity drives the cheaper/steeper XP curve.
        Pet epicPet = mgr.addPet(epicPlayer, PetType.TIGER, Rarity.EPIC);
        Pet legendaryPet = mgr.addPet(legendaryPlayer, PetType.TIGER, Rarity.LEGENDARY);
        mgr.addExperience(epicPlayer, PetType.TIGER, xp);
        mgr.addExperience(legendaryPlayer, PetType.TIGER, xp);
        mgr.equipPet(epicPlayer, epicPet.id);
        mgr.equipPet(legendaryPlayer, legendaryPet.id);
        int epicStrength = mgr.getActivePetStats(epicPlayer)[1];
        int legendaryStrength = mgr.getActivePetStats(legendaryPlayer)[1];
        // Same XP reaches a higher level (and thus higher stat scale) on the cheaper EPIC curve.
        assertTrue(epicStrength > legendaryStrength,
                "EPIC strength " + epicStrength + " should exceed LEGENDARY " + legendaryStrength);
        assertTrue(legendaryStrength > 0);
        mgr.reset(epicPlayer);
        mgr.reset(legendaryPlayer);
    }

    // --- held-pet-item bonus application ---

    @Test
    void heldItemBonus_AddedToActivePetStats() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetType.TIGER, Rarity.EPIC);
        mgr.equipPet(player, pet.id);
        int[] before = mgr.getActivePetStats(player);
        // QUICK_CLAW adds flat speed; TIGER has no base speed so the bonus is exactly the item's.
        assertTrue(mgr.setHeldItem(player, pet.id, PetItem.QUICK_CLAW));
        int[] after = mgr.getActivePetStats(player);
        assertEquals(before[0] + PetItem.QUICK_CLAW.speedBonus, after[0], "held-item speed bonus applied");
        assertEquals(before[1], after[1], "strength unchanged by a speed-only item");
        mgr.reset(player);
    }

    @Test
    void heldItemBonus_StrengthItemRaisesActivePetStrength() {
        PetManager mgr = PetManager.getInstance();
        UUID player = UUID.randomUUID();
        Pet pet = mgr.addPet(player, PetType.TIGER, Rarity.EPIC);
        mgr.equipPet(player, pet.id);
        int[] before = mgr.getActivePetStats(player);
        assertTrue(mgr.setHeldItem(player, pet.id, PetItem.SHARPENED_CLAWS));
        int[] after = mgr.getActivePetStats(player);
        assertEquals(before[1] + PetItem.SHARPENED_CLAWS.strengthBonus, after[1]);
        // Clearing the item removes the bonus again.
        assertTrue(mgr.setHeldItem(player, pet.id, PetItem.NONE));
        assertEquals(before[1], mgr.getActivePetStats(player)[1]);
        mgr.reset(player);
    }
}
