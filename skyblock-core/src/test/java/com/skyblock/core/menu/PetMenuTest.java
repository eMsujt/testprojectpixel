package com.skyblock.core.menu;

import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetManager.Pet;
import com.skyblock.core.manager.PetManager.PetType;
import com.skyblock.core.model.Rarity;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PetMenuTest {

    private static final UUID PLAYER = UUID.randomUUID();

    @Test
    void title_isPets() {
        PetMenu menu = new PetMenu(PLAYER);
        assertEquals("§dPets", menu.getTitle());
    }

    @Test
    void rows_isSix() {
        PetMenu menu = new PetMenu(PLAYER);
        assertEquals(6, menu.getRows());
    }

    @Test
    void constructor_doesNotThrow() {
        assertDoesNotThrow(() -> new PetMenu(PLAYER));
    }

    @Test
    void rarityWool_common_isWhite() {
        assertEquals(Material.WHITE_WOOL, PetMenu.RARITY_WOOL.get(Rarity.COMMON));
    }

    @Test
    void rarityWool_uncommon_isLime() {
        assertEquals(Material.LIME_WOOL, PetMenu.RARITY_WOOL.get(Rarity.UNCOMMON));
    }

    @Test
    void rarityWool_rare_isBlue() {
        assertEquals(Material.BLUE_WOOL, PetMenu.RARITY_WOOL.get(Rarity.RARE));
    }

    @Test
    void rarityWool_epic_isPurple() {
        assertEquals(Material.PURPLE_WOOL, PetMenu.RARITY_WOOL.get(Rarity.EPIC));
    }

    @Test
    void rarityWool_legendary_isOrange() {
        assertEquals(Material.ORANGE_WOOL, PetMenu.RARITY_WOOL.get(Rarity.LEGENDARY));
    }

    @Test
    void rarityWool_allRaritiesMapped() {
        for (Rarity rarity : Rarity.values()) {
            assertNotNull(PetMenu.RARITY_WOOL.get(rarity),
                    "RARITY_WOOL must contain an entry for " + rarity);
        }
    }

    @Test
    void petManager_addAndGetPets_roundTrips() {
        UUID pid = UUID.randomUUID();
        PetManager pm = PetManager.getInstance();
        pm.reset(pid);
        Pet added = pm.addPet(pid, PetType.WOLF, Rarity.EPIC);
        assertEquals(1, pm.getPets(pid).size());
        assertEquals(PetType.WOLF, pm.getPets(pid).get(0).type);
        assertEquals(Rarity.EPIC, added.rarity);
        pm.reset(pid);
    }

    @Test
    void petManager_equipUnequip_activePetChanges() {
        UUID pid = UUID.randomUUID();
        PetManager pm = PetManager.getInstance();
        pm.reset(pid);
        Pet pet = pm.addPet(pid, PetType.GRIFFIN, Rarity.LEGENDARY);
        assertNull(pm.getActivePet(pid));
        pm.equipPet(pid, pet.id);
        assertNotNull(pm.getActivePet(pid));
        pm.unequipPet(pid);
        assertNull(pm.getActivePet(pid));
        pm.reset(pid);
    }
}
